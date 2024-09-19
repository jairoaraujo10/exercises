package integration.infrastructure.api.routes

import application.users.UserController
import com.google.gson.Gson
import domain.users.Email
import domain.users.UserId
import domain.users.auth.Token
import domain.users.auth.TokenService
import domain.utils.PaginatedList
import infra.api.AuthFilter
import infra.api.Gateway
import infra.api.routes.ErrorsRoutesDeclaration
import infra.api.routes.UserRoutesDeclaration
import infra.api.routes.response.ListUsersResponseView
import infra.api.routes.response.UserView
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.restassured.RestAssured
import io.restassured.RestAssured.given
import io.restassured.http.ContentType
import org.hamcrest.CoreMatchers.equalTo
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import utils.domain.security.RequesterFixture.Companion.anyRequester
import utils.domain.users.UserFixture

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserRoutesDeclarationTest {
    companion object {
        private val gson = Gson()
        private val requester = anyRequester()
        private val tokenService: TokenService = mockk()
        private val authFilter = AuthFilter(tokenService)
        private val userController = mockk<UserController>()
        private val errorRoutesDeclaration = ErrorsRoutesDeclaration(gson, authFilter)
        private val declaration = UserRoutesDeclaration(gson, authFilter, userController)
        private lateinit var gateway: Gateway

        @JvmStatic
        @BeforeAll
        fun beforeAll() {
            every { tokenService.requesterFrom(Token("valid-token")) } returns requester
            gateway = Gateway(4567)
            gateway.addRoutes(errorRoutesDeclaration)
            gateway.addRoutes(declaration)
            gateway.start()
            RestAssured.port = gateway.port
        }

        @JvmStatic
        @AfterAll
        fun afterAll() {
            gateway.stop()
        }
    }

    @Test
    fun `returns user found by id`() {
        val expectedUser = UserFixture.anyUser()
        every { userController.getUser(UserId(1L), requester) } returns expectedUser

        given()
            .header("Authorization", "Bearer valid-token")
        .`when`().get("/user/1")
        .then().assertThat()
            .statusCode(200)
            .contentType(ContentType.JSON)
            .body(equalTo(gson.toJson(UserView.from(expectedUser))))
    }

    @Test
    fun `get user request with non existing user id`() {
        every { userController.getUser(UserId(1L), requester) } throws NoSuchElementException("User not found")

        given()
            .header("Authorization", "Bearer valid-token")
        .`when`().get("/user/1")
        .then().assertThat()
            .statusCode(404)
            .contentType(ContentType.JSON)
            .body("message", equalTo("User not found"))
    }

    @Test
    fun `get user request with missing authorization token`() {
        given()
        .`when`().get("/user/1")
        .then().assertThat()
            .statusCode(401)
            .contentType(ContentType.JSON)
            .body("message", equalTo("Unauthorized"))
    }

    @Test
    fun `creates a new user successfully`() {
        val requestBody = """{"name": "Test", "email": "test@mail.com"}"""
        every { userController.createUser("Test", Email("test@mail.com"), requester) } just Runs

        given().contentType(ContentType.JSON)
            .header("Authorization", "Bearer valid-token")
            .body(requestBody)
        .`when`().post("/user")
        .then().assertThat()
            .statusCode(201)
    }

    @Test
    fun `create user request with invalid email format`() {
        val requestBody = """{"name": "Test", "email": "invalid-email"}"""

        given().contentType(ContentType.JSON)
            .header("Authorization", "Bearer valid-token")
            .body(requestBody)
        .`when`().post("/user")
        .then().assertThat()
            .statusCode(400)
            .contentType(ContentType.JSON)
            .body("message", equalTo("Invalid email format"))
    }

    @Test
    fun `create user request with missing name`() {
        val requestBody = """{"email": "test@mail.com"}"""

        given().contentType(ContentType.JSON)
            .header("Authorization", "Bearer valid-token")
            .body(requestBody)
        .`when`().post("/user")
        .then().assertThat()
            .statusCode(400)
            .contentType(ContentType.JSON)
            .body("message", equalTo("Missing name"))
    }

    @Test
    fun `create user request with missing authorization token`() {
        val requestBody = """{"name": "Test", "email": "test@mail.com"}"""

        given().contentType(ContentType.JSON)
            .body(requestBody)
        .`when`().post("/user")
        .then().assertThat()
            .statusCode(401)
            .contentType(ContentType.JSON)
            .body("message", equalTo("Unauthorized"))
    }

    @Test
    fun `deletes a user successfully`() {
        every { userController.delete(UserId(1L), requester) } just Runs

        given()
            .header("Authorization", "Bearer valid-token")
        .`when`().delete("/user/1")
        .then().assertThat()
            .statusCode(204)
    }

    @Test
    fun `delete user request with non-existing user id`() {
        every { userController.delete(UserId(1L), requester) } throws NoSuchElementException("User not found")

        given()
            .header("Authorization", "Bearer valid-token")
        .`when`().delete("/user/1")
        .then().assertThat()
            .statusCode(404)
            .contentType(ContentType.JSON)
            .body("message", equalTo("User not found"))
    }

    @Test
    fun `delete user request with missing authorization token`() {
        given()
        .`when`().delete("/user/1")
        .then().assertThat()
            .statusCode(401)
            .contentType(ContentType.JSON)
            .body("message", equalTo("Unauthorized"))
    }

    @Test
    fun `searches for users successfully`() {
        val expectedUsers = PaginatedList(listOf(UserFixture.anyUser()), 1L)
        val expectedResponse = ListUsersResponseView.from(expectedUsers)

        every { userController.listUsers("", any(), requester) } returns expectedUsers

        given().contentType(ContentType.JSON)
            .header("Authorization", "Bearer valid-token")
        .`when`().post("/user/search")
        .then().assertThat()
            .statusCode(200)
            .contentType(ContentType.JSON)
            .body(equalTo(gson.toJson(expectedResponse)))
    }

    @Test
    fun `searches for users with invalid pagination params`() {
        given().contentType(ContentType.JSON)
            .header("Authorization", "Bearer valid-token")
        .`when`().post("/user/search?limit=1&offset=a")
        .then().assertThat()
            .statusCode(400)
            .contentType(ContentType.JSON)
            .body("message", equalTo("Invalid query param 'offset'"))
    }

    @Test
    fun `search users request with missing authorization token`() {
        given().contentType(ContentType.JSON)
        .`when`().post("/user/search")
        .then().assertThat()
            .statusCode(401)
            .contentType(ContentType.JSON)
            .body("message", equalTo("Unauthorized"))
    }
}