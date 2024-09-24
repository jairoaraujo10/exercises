package integration.infrastructure.api.routes

import application.auth.AuthController
import com.google.gson.Gson
import domain.users.Email
import domain.users.PlainPassword
import domain.users.auth.Unauthorized
import domain.users.auth.Token
import domain.users.auth.TokenService
import infra.api.AuthFilter
import infra.api.Gateway
import infra.api.routes.AuthRoutesDeclaration
import infra.api.routes.ErrorsRoutesDeclaration
import io.mockk.*
import io.restassured.RestAssured
import io.restassured.RestAssured.given
import io.restassured.http.ContentType
import org.hamcrest.CoreMatchers.containsString
import org.hamcrest.CoreMatchers.equalTo
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import utils.domain.security.RequesterFixture.Companion.anyRequester

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AuthRoutesDeclarationTest {
    companion object {
        private val gson = Gson()
        private val tokenService: TokenService = mockk()
        private val authFilter = AuthFilter(tokenService)
        private val authController = mockk<AuthController>()
        private val errorRoutesDeclaration = ErrorsRoutesDeclaration(gson, authFilter)
        private val declaration = AuthRoutesDeclaration(gson, authFilter, authController)
        private lateinit var gateway: Gateway

        @JvmStatic
        @BeforeAll
        fun beforeAll() {
            every { authController.login(any(), any()) } answers {
                val email = it.invocation.args[0] as Email
                val password = it.invocation.args[1] as PlainPassword
                if (email.address == "test@mail.com" && password.value == "test-password") {
                    Token("token-value")
                } else {
                    throw Unauthorized("Invalid credentials")
                }
            }

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
    fun `successful login when valid credentials`() {
        given().contentType(ContentType.JSON)
            .body("""{"email": "test@mail.com", "password": "test-password"}""".trimIndent())
        .`when`().post("/auth/login")
        .then().assertThat()
            .statusCode(200)
            .contentType(ContentType.JSON)
            .body("token", equalTo("token-value"))
    }

    @Test
    fun `fails unauthorized when invalid credentials`() {
        given().contentType(ContentType.JSON)
            .body("""{"email": "wrong@mail.com", "password": "wrongpass"}""".trimIndent())
        .`when`().post("/auth/login")
        .then().assertThat()
            .statusCode(401)
            .contentType(ContentType.JSON)
            .body("message", equalTo("Invalid credentials"))
    }

    @Test
    fun `fails bad request when missing email`() {
        given().contentType(ContentType.JSON)
            .body("""{"password": "test-password"}""".trimIndent())
        .`when`().post("/auth/login")
        .then().assertThat()
            .statusCode(400)
            .contentType(ContentType.JSON)
            .body("message", containsString("Missing email"))
    }

    @Test
    fun `fails bad request when empty email`() {
        given().contentType(ContentType.JSON)
            .body("""{"email": "", "password": "test-password"}""".trimIndent())
        .`when`().post("/auth/login")
        .then().assertThat()
            .statusCode(400)
            .contentType(ContentType.JSON)
            .body("message", containsString("Email cannot be empty"))
    }

    @Test
    fun `fails bad request when invalid email`() {
        given().contentType(ContentType.JSON)
            .body("""{"email": "invalid", "password": "test-password"}""".trimIndent())
        .`when`().post("/auth/login")
        .then().assertThat()
            .statusCode(400)
            .contentType(ContentType.JSON)
            .body("message", containsString("Invalid email format"))
    }

    @Test
    fun `fails bad request when missing password`() {
        given().contentType(ContentType.JSON)
            .body("""{"email": "test@mail.com"}""".trimIndent())
        .`when`().post("/auth/login")
        .then().assertThat()
            .statusCode(400)
            .contentType(ContentType.JSON)
            .body("message", containsString("Missing password"))
    }

    @Test
    fun `fails bad request when empty password`() {
        given().contentType(ContentType.JSON)
            .body("""{"email": "test@mail.com", "password": ""}""".trimIndent())
        .`when`().post("/auth/login")
        .then().assertThat()
            .statusCode(400)
            .contentType(ContentType.JSON)
            .body("message", containsString("Password cannot be empty"))
    }

    @Test
    fun `fails bad request when invalid password`() {
        given().contentType(ContentType.JSON)
            .body("""{"email": "test@mail.com", "password": "short"}""".trimIndent())
        .`when`().post("/auth/login")
        .then().assertThat()
            .statusCode(400)
            .contentType(ContentType.JSON)
            .body("message", containsString("Password must be at least 8 characters long"))
    }

    @Test
    fun `successful password reset request`() {
        every { authController.requestResetPassword(any()) } just Runs

        given().contentType(ContentType.JSON)
            .body("""{"email": "test@mail.com"}""".trimIndent())
        .`when`().post("/auth/reset-password/request")
        .then().assertThat()
            .statusCode(204)
            .contentType(ContentType.JSON)
            .body(equalTo(""))

        verify { authController.requestResetPassword(Email("test@mail.com")) }
    }

    @Test
    fun `password reset request with missing email`() {
        given().contentType(ContentType.JSON)
            .body("""{}""".trimIndent())
            .`when`().post("/auth/reset-password/request")
            .then().assertThat()
            .statusCode(400)
            .contentType(ContentType.JSON)
            .body("message", equalTo("Missing email"))
    }

    @Test
    fun `password reset request with invalid email`() {
        given().contentType(ContentType.JSON)
            .body("""{}""".trimIndent())
            .`when`().post("/auth/reset-password/request")
            .then().assertThat()
            .statusCode(400)
            .contentType(ContentType.JSON)
            .body("message", equalTo("Missing email"))
    }

    @Test
    fun `successful password reset with authorization`() {
        val requester = anyRequester()
        every { tokenService.requesterFrom(Token("valid-token")) } returns requester
        every { authController.resetPassword(any(), any()) } just Runs

        given().contentType(ContentType.JSON)
            .header("Authorization", "Bearer valid-token")
            .body("""{"password": "test-password"}""".trimIndent())
        .`when`().post("/auth/reset-password")
        .then().assertThat()
            .statusCode(200)
            .contentType(ContentType.JSON)
            .body("message", equalTo("Password reset successfully"))

        verify { authController.resetPassword(PlainPassword("test-password"), requester) }
    }

    @Test
    fun `password reset for non-existent user derived from token`() {
        val requester = anyRequester()
        every { tokenService.requesterFrom(any()) } returns requester
        every { authController.resetPassword(any(), any()) } throws NoSuchElementException("User not found")

        given().contentType(ContentType.JSON)
            .header("Authorization", "Bearer valid-token")
            .body("""{"password": "test-password"}""".trimIndent())
        .`when`().post("/auth/reset-password")
        .then().assertThat()
            .statusCode(404)
            .contentType(ContentType.JSON)
            .body("message", equalTo("User not found"))

        verify { authController.resetPassword(PlainPassword("test-password"), requester) }
    }

    @Test
    fun `password reset with missing authorization token`() {
        given().contentType(ContentType.JSON)
            .body("""{"password": "test-password"}""")
        .`when`().post("/auth/reset-password")
        .then().assertThat()
            .statusCode(401)
            .contentType(ContentType.JSON)
            .body("message", containsString("Unauthorized"))
    }

    @Test
    fun `password reset with invalid authorization token`() {
        every { tokenService.requesterFrom(any()) } throws Unauthorized("Unauthorized")

        given().contentType(ContentType.JSON)
            .header("Authorization", "Bearer invalid-token")
            .body("""{"password": "test-password"}""")
        .`when`().post("/auth/reset-password")
        .then().assertThat()
            .statusCode(401)
            .contentType(ContentType.JSON)
            .body("message", containsString("Unauthorized"))
    }

    @Test
    fun `password reset with missing password`() {
        val requester = anyRequester()
        every { tokenService.requesterFrom(any()) } returns requester

        given().contentType(ContentType.JSON)
            .header("Authorization", "Bearer valid-token")
            .body("""{}""")
        .`when`().post("/auth/reset-password")
        .then().assertThat()
            .statusCode(400)
            .contentType(ContentType.JSON)
            .body("message", containsString("Missing password"))
    }

    @Test
    fun `password reset with invalid password format`() {
        val requester = anyRequester()
        every { tokenService.requesterFrom(any()) } returns requester

        given().contentType(ContentType.JSON)
            .header("Authorization", "Bearer valid-token")
            .body("""{"password": "invalid"}""")
        .`when`().post("/auth/reset-password")
        .then().assertThat()
            .statusCode(400)
            .contentType(ContentType.JSON)
            .body("message", containsString("Password must be at least 8 characters long"))
    }
}