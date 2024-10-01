package infra.api.routes

import application.exercises.ExercisesListController
import com.google.gson.Gson
import application.SearchRequest
import domain.exercises.base.ExerciseId
import domain.exercises.list.ExercisesListId
import infra.api.AbstractRoutesDeclaration
import infra.api.AuthFilter
import infra.api.routes.request.AddExerciseRequest
import infra.api.routes.request.CreateExercisesListApiRequestBody
import infra.api.routes.request.UpdateExercisesListApiRequestBody
import infra.api.routes.response.ExercisesListView
import infra.api.routes.response.SearchExercisesListResponseView
import spark.Request
import spark.Response
import spark.Service

class ExercisesListRoutesDeclaration(
    gson: Gson, private val authFilter: AuthFilter, private val exercisesListController: ExercisesListController
): AbstractRoutesDeclaration(gson,authFilter) {

    companion object {
        const val BASE_ENDPOINT = "/exercises-list"
    }

    override fun declareRoutes(service: Service) {
        authFilter.applyTo(service, BASE_ENDPOINT)
        authFilter.applyTo(service, "$BASE_ENDPOINT/*")
        service.post(BASE_ENDPOINT, this::createExercisesList)
        service.get("$BASE_ENDPOINT/:id", this::getExercisesList)
        service.put("$BASE_ENDPOINT/:id", this::updateExercisesList)
        service.delete("$BASE_ENDPOINT/:id", this::deleteExercisesList)
        service.post("$BASE_ENDPOINT/:id/exercises", this::addExerciseToList)
        service.delete("$BASE_ENDPOINT/:id/exercises/:exerciseId", this::removeExerciseFromList)
        service.post("$BASE_ENDPOINT/search", this::searchExercisesList)
    }

    private fun createExercisesList(request: Request, response: Response): String {
        val createRequestView = extractBody(request, CreateExercisesListApiRequestBody::class.java)
        exercisesListController.create(createRequestView.toRequest(), requester())
        return created(response)
    }

    private fun getExercisesList(request: Request, response: Response): String {
        val exercisesListId = ExercisesListId(request.params("id"))
        val exercisesList = exercisesListController.get(exercisesListId, requester())
        val exercisesListView = ExercisesListView.from(exercisesList)
        return ok(response, exercisesListView)
    }

    private fun updateExercisesList(request: Request, response: Response): String {
        val exercisesListId = ExercisesListId(request.params("id"))
        val requestView = extractBody(request, UpdateExercisesListApiRequestBody::class.java)
        exercisesListController.update(exercisesListId, requestView.toRequest(), requester())
        return noContent(response)
    }

    private fun deleteExercisesList(request: Request, response: Response): String {
        val exercisesListId = ExercisesListId(request.params("id"))
        exercisesListController.delete(exercisesListId, requester())
        return noContent(response)
    }

    private fun addExerciseToList(request: Request, response: Response): String {
        val exercisesListId = ExercisesListId(request.params("id"))
        val addExerciseRequest = extractBody(request, AddExerciseRequest::class.java)
        exercisesListController.addExerciseToList(exercisesListId, addExerciseRequest.asExerciseId(), requester())
        return noContent(response)
    }

    private fun removeExerciseFromList(request: Request, response: Response): String {
        val exercisesListId = ExercisesListId(request.params("id"))
        val exerciseId = ExerciseId(request.params("exerciseId"))
        exercisesListController.removeExerciseFromList(exercisesListId, exerciseId, requester())
        return noContent(response)
    }

    private fun searchExercisesList(request: Request, response: Response): String {
        val searchRequest = extractBody(request, SearchRequest::class.java)
        val exercisesListPaginated = exercisesListController
            .searchExercisesList(searchRequest, paginationParamsFrom(request), requester())
        val searchExercisesListResponseView = SearchExercisesListResponseView.from(exercisesListPaginated)
        return ok(response, searchExercisesListResponseView)
    }
}