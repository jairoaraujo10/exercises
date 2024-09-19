package infra.api.routes

import application.exercises.ExerciseController
import com.google.gson.Gson
import domain.exercises.base.ExerciseId
import domain.exercises.base.request.SearchExercisesRequest
import domain.utils.PaginationParams
import infra.api.AbstractRoutesDeclaration
import infra.api.AuthFilter
import spark.Request
import spark.Response
import spark.Service
import infra.api.routes.request.CreateExerciseApiRequestBody
import infra.api.routes.request.UpdateExerciseApiRequestBody
import infra.api.routes.response.ExerciseView
import infra.api.routes.response.SearchExercisesResponseView

class ExerciseRoutesDeclaration(
    gson: Gson, private val authFilter: AuthFilter, private val exerciseController: ExerciseController
): AbstractRoutesDeclaration(gson,authFilter) {

    companion object {
        const val BASE_ENDPOINT = "/exercise"
    }

    override fun declareRoutes(service: Service) {
        authFilter.applyTo(service, BASE_ENDPOINT)
        authFilter.applyTo(service, "$BASE_ENDPOINT/*")
        service.post(BASE_ENDPOINT, this::createExercise)
        service.get("$BASE_ENDPOINT/:id", this::getExercise)
        service.put("$BASE_ENDPOINT/:id", this::updateExercise)
        service.delete("$BASE_ENDPOINT/:id", this::deleteExercise)
        service.post("${BASE_ENDPOINT}/search", this::search)
    }

    private fun createExercise(request: Request, response: Response): String {
        val createRequestView = extractBody(request, CreateExerciseApiRequestBody::class.java)
        exerciseController.create(createRequestView.toRequest(), requester())
        return created(response)
    }

    private fun getExercise(request: Request, response: Response): String {
        val exerciseId = ExerciseId(request.params("id"))
        val exercise = exerciseController.get(exerciseId, requester())
        val exerciseView = ExerciseView.from(exercise)
        return ok(response, exerciseView)
    }

    private fun updateExercise(request: Request, response: Response): String {
        val exerciseId = ExerciseId(request.params("id"))
        val requestView = extractBody(request, UpdateExerciseApiRequestBody::class.java)
        exerciseController.update(exerciseId, requestView.toRequest(), requester())
        return noContent(response)
    }

    private fun deleteExercise(request: Request, response: Response): String {
        val exerciseId = ExerciseId(request.params("id"))
        exerciseController.delete(exerciseId, requester())
        return noContent(response)
    }

    private fun search(request: Request, response: Response): String {
        val searchRequest = extractBody(request, SearchExercisesRequest::class.java)
        val exercisePaginatedList = exerciseController
            .searchExercises(searchRequest, paginationParamsFrom(request), requester())
        val searchExercisesResponseView = SearchExercisesResponseView.from(exercisePaginatedList)
        return ok(response, searchExercisesResponseView)
    }

    private fun paginationParamsFrom(request: Request): PaginationParams {
        return PaginationParams(
            intQueryParam(request, "limit"),
            intQueryParam(request, "offset")
        )
    }

    private fun intQueryParam(request: Request, key: String): Int? {
        return try {
            request.queryParams(key).toInt()
        } catch (ex: NullPointerException) {
            null
        } catch (ex: NumberFormatException) {
            throw IllegalArgumentException("Invalid query param '$key'")
        }
    }

}