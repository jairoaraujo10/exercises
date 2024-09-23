package integration.infrastructure.api.routes

import application.exercises.ExerciseController
import com.google.gson.Gson
import domain.exercises.base.ExerciseId
import domain.users.auth.Token
import domain.users.auth.TokenService
import domain.utils.PaginatedList
import infra.api.AuthFilter
import infra.api.Gateway
import infra.api.routes.ErrorsRoutesDeclaration
import infra.api.routes.ExerciseRoutesDeclaration
import infra.api.routes.response.ExerciseView
import infra.api.routes.response.SearchExercisesResponseView
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
import utils.domain.exercises.ExerciseFixture
import utils.domain.security.RequesterFixture.Companion.anyRequester

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ExerciseRoutesDeclarationTest {
    companion object {
        private val gson = Gson()
        private val requester = anyRequester()
        private val tokenService: TokenService = mockk()
        private val authFilter = AuthFilter(tokenService)
        private val exerciseController = mockk<ExerciseController>()
        private val errorRoutesDeclaration = ErrorsRoutesDeclaration(gson, authFilter)
        private val declaration = ExerciseRoutesDeclaration(gson, authFilter, exerciseController)
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
    fun `creates a new exercise successfully`() {
        val requestBody = """{"title": "Exercise title", "description": "This is the exercise description", 
            |"tags": [{"value": "value_1"}], "possibleAnswers": ["Option A", "Option B", "Option C"], 
            |"correctAnswerIndex": 1}""".trimMargin()
        every { exerciseController.create(any(), requester) } returns ExerciseFixture.anyExercise()

        given().contentType(ContentType.JSON)
            .header("Authorization", "Bearer valid-token")
            .body(requestBody)
        .`when`().post("/exercise")
            .then().assertThat()
            .statusCode(201)
    }

    @Test
    fun `create exercise request with missing authorization token`() {
        val requestBody = """{"title": "Exercise title", "description": "This is the exercise description", 
            |"tags": [{"value": "value_1"}], "possibleAnswers": ["Option A", "Option B", "Option C"], 
            |"correctAnswerIndex": 1}""".trimMargin()

        given().contentType(ContentType.JSON)
            .body(requestBody)
        .`when`().post("/exercise")
        .then().assertThat()
            .statusCode(401)
            .contentType(ContentType.JSON)
            .body("message", equalTo("Unauthorized"))
    }

    @Test
    fun `returns exercise found by id`() {
        val expectedExercise = ExerciseFixture.anyExercise()
        every { exerciseController.get(ExerciseId("1"), requester) } returns expectedExercise

        given()
            .header("Authorization", "Bearer valid-token")
        .`when`().get("/exercise/1")
        .then().assertThat()
            .statusCode(200)
            .contentType(ContentType.JSON)
            .body(equalTo(gson.toJson(ExerciseView.from(expectedExercise))))
    }

    @Test
    fun `get exercise request with non existing exercise id`() {
        every {
            exerciseController.get(ExerciseId("1"), requester)
        } throws NoSuchElementException("Exercise not found")

        given()
            .header("Authorization", "Bearer valid-token")
        .`when`().get("/exercise/1")
        .then().assertThat()
            .statusCode(404)
            .contentType(ContentType.JSON)
            .body("message", equalTo("Exercise not found"))
    }

    @Test
    fun `get exercise request with missing authorization token`() {
        given()
        .`when`().get("/exercise/1")
        .then().assertThat()
            .statusCode(401)
            .contentType(ContentType.JSON)
            .body("message", equalTo("Unauthorized"))
    }

    @Test
    fun `updates an exercise successfully`() {
        val requestBody = """{"title": "Updated Exercise", "description": "Updated description"}"""
        every { exerciseController.update(ExerciseId("1"), any(), requester) } just Runs

        given().contentType(ContentType.JSON)
            .header("Authorization", "Bearer valid-token")
        .body(requestBody)
        .`when`().put("/exercise/1")
            .then().assertThat()
            .statusCode(204)
    }

    @Test
    fun `update exercise request with missing authorization token`() {
        val requestBody = """{"title": "Updated Exercise", "description": "Updated description"}"""

        given()
            .contentType(ContentType.JSON)
            .body(requestBody)
        .`when`().put("/exercise/1")
        .then().assertThat()
            .statusCode(401)
            .contentType(ContentType.JSON)
            .body("message", equalTo("Unauthorized"))
    }

    @Test
    fun `deletes an exercise successfully`() {
        every { exerciseController.delete(ExerciseId("1"), requester) } just Runs

        given()
            .header("Authorization", "Bearer valid-token")
        .`when`().delete("/exercise/1")
        .then().assertThat()
            .statusCode(204)
    }

    @Test
    fun `delete exercise request with non-existing exercise id`() {
        every {
            exerciseController.delete(ExerciseId("1"), requester)
        } throws NoSuchElementException("Exercise not found")

        given()
            .header("Authorization", "Bearer valid-token")
            .`when`().delete("/exercise/1")
        .then().assertThat()
        .statusCode(404)
            .contentType(ContentType.JSON)
            .body("message", equalTo("Exercise not found"))
    }

    @Test
    fun `delete exercise request with missing authorization token`() {
        given()
        .`when`().delete("/exercise/1")
        .then().assertThat()
            .statusCode(401)
            .contentType(ContentType.JSON)
            .body("message", equalTo("Unauthorized"))
    }

    @Test
    fun `searches for exercises successfully`() {
        val searchRequest = """{"searchTerm": "text"}"""
        val expectedExercises = PaginatedList(listOf(ExerciseFixture.anyExercise()), 1)
        val expectedResponse = SearchExercisesResponseView.from(expectedExercises)

        every { exerciseController.searchExercises(any(), any(), requester) } returns expectedExercises

        given().contentType(ContentType.JSON)
            .header("Authorization", "Bearer valid-token")
            .body(searchRequest)
        .`when`().post("/exercise/search")
        .then().assertThat()
            .statusCode(200)
            .contentType(ContentType.JSON)
            .body(equalTo(gson.toJson(expectedResponse)))
    }

    @Test
    fun `searches for exercises with invalid pagination params`() {
        val searchRequest = """{"searchTerm": "text"}"""

        given().contentType(ContentType.JSON)
            .body(searchRequest)
            .header("Authorization", "Bearer valid-token")
        .`when`().post("/exercise/search?limit=1&offset=a")
        .then().assertThat()
            .statusCode(400)
            .contentType(ContentType.JSON)
            .body("message", equalTo("Invalid query param 'offset'"))
    }

    @Test
    fun `search exercises request with missing authorization token`() {
        val searchRequest = """{"searchTerm": "text"}"""

        given().contentType(ContentType.JSON)
            .body(searchRequest)
        .`when`().post("/exercise/search")
        .then().assertThat()
            .statusCode(401)
            .contentType(ContentType.JSON)
            .body("message", equalTo("Unauthorized"))
    }
}