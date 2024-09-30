package integration.infrastructure.api.routes

import application.exercises.ExercisesListController
import com.google.gson.Gson
import domain.exercises.list.ExercisesListId
import domain.users.auth.Token
import domain.users.auth.TokenService
import infra.api.AuthFilter
import infra.api.Gateway
import infra.api.routes.ErrorsRoutesDeclaration
import infra.api.routes.ExercisesListRoutesDeclaration
import infra.api.routes.response.ExercisesListView
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
import org.junit.jupiter.api.TestInstance
import utils.domain.exercises.ExercisesListFixture
import utils.domain.security.RequesterFixture.Companion.anyRequester
import kotlin.test.Test

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ExercisesListRoutesDeclarationTest {
    companion object {
        private val gson = Gson()
        private val requester = anyRequester()
        private val tokenService: TokenService = mockk()
        private val authFilter = AuthFilter(tokenService)
        private val exercisesListController = mockk<ExercisesListController>()
        private val errorRoutesDeclaration = ErrorsRoutesDeclaration(gson, authFilter)
        private val declaration = ExercisesListRoutesDeclaration(gson, authFilter, exercisesListController)
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
    fun `creates a new exercises list successfully`() {
        val requestBody = """{"title": "Exercise title", "tags": [{"value": "value_1"}]}"""
        every { exercisesListController.create(any(), requester) } returns ExercisesListFixture.anyExercisesList()

        given().contentType(ContentType.JSON)
            .header("Authorization", "Bearer valid-token")
            .body(requestBody)
        .`when`().post("/exercises-list")
            .then().assertThat()
            .statusCode(201)
    }

    @Test
    fun `create exercises list request with missing authorization token`() {
        val requestBody = """{"name": "Exercises List name", "description": "This is the exercises list description"}"""

        given().contentType(ContentType.JSON)
            .body(requestBody)
        .`when`().post("/exercises-list")
        .then().assertThat()
            .statusCode(401)
            .contentType(ContentType.JSON)
            .body("message", equalTo("Unauthorized"))
    }

    @Test
    fun `returns exercises list found by id`() {
        val expectedExercisesList = ExercisesListFixture.anyExercisesList()
        every { exercisesListController.get(ExercisesListId("1"), requester) } returns expectedExercisesList

        given()
            .header("Authorization", "Bearer valid-token")
        .`when`().get("/exercises-list/1")
        .then().assertThat()
            .statusCode(200)
            .contentType(ContentType.JSON)
            .body(equalTo(gson.toJson(ExercisesListView.from(expectedExercisesList))))
    }

    @Test
    fun `get exercises list request with non existing id`() {
        every {
            exercisesListController.get(ExercisesListId("1"), requester)
        } throws NoSuchElementException("Exercises list not found")

        given()
            .header("Authorization", "Bearer valid-token")
        .`when`().get("/exercises-list/1")
        .then().assertThat()
            .statusCode(404)
            .contentType(ContentType.JSON)
            .body("message", equalTo("Exercises list not found"))
    }

    @Test
    fun `get exercises list request with missing authorization token`() {
        given()
        .`when`().get("/exercises-list/1")
        .then().assertThat()
            .statusCode(401)
            .contentType(ContentType.JSON)
            .body("message", equalTo("Unauthorized"))
    }

    @Test
    fun `updates an exercises list successfully`() {
        val requestBody = """{"name": "Updated Exercises List", "description": "Updated description"}"""
        every { exercisesListController.update(ExercisesListId("1"), any(), requester) } just Runs

        given().contentType(ContentType.JSON)
            .header("Authorization", "Bearer valid-token")
            .body(requestBody)
        .`when`().put("/exercises-list/1")
        .then().assertThat()
            .statusCode(204)
    }

    @Test
    fun `update exercises list request with missing authorization token`() {
        val requestBody = """{"name": "Updated Exercises List", "description": "Updated description"}"""

        given()
            .contentType(ContentType.JSON)
            .body(requestBody)
        .`when`().put("/exercises-list/1")
        .then().assertThat()
            .statusCode(401)
            .contentType(ContentType.JSON)
            .body("message", equalTo("Unauthorized"))
    }

    @Test
    fun `deletes an exercises list successfully`() {
        every { exercisesListController.delete(ExercisesListId("1"), requester) } just Runs

        given()
            .header("Authorization", "Bearer valid-token")
            .`when`().delete("/exercises-list/1")
            .then().assertThat()
            .statusCode(204)
    }

    @Test
    fun `delete exercises list request with non-existing id`() {
        every {
            exercisesListController.delete(ExercisesListId("1"), requester)
        } throws NoSuchElementException("Exercises list not found")

        given()
            .header("Authorization", "Bearer valid-token")
            .`when`().delete("/exercises-list/1")
            .then().assertThat()
            .statusCode(404)
            .contentType(ContentType.JSON)
            .body("message", equalTo("Exercises list not found"))
    }

    @Test
    fun `delete exercises list request with missing authorization token`() {
        given()
            .`when`().delete("/exercises-list/1")
            .then().assertThat()
            .statusCode(401)
            .contentType(ContentType.JSON)
            .body("message", equalTo("Unauthorized"))
    }
}