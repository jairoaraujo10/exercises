package infra.api.routes

import application.exercises.ExercisesListController
import com.google.gson.Gson
import infra.api.AbstractRoutesDeclaration
import infra.api.AuthFilter
import spark.Service

class ExercisesListRoutesDeclaration(
    gson: Gson, private val authFilter: AuthFilter, private val exercisesListController: ExercisesListController
): AbstractRoutesDeclaration(gson,authFilter) {

    companion object {
        const val BASE_ENDPOINT = "/exercises-list"
    }

    override fun declareRoutes(service: Service) {
        authFilter.applyTo(service, BASE_ENDPOINT)
        authFilter.applyTo(service, "${BASE_ENDPOINT}/*")
//        TODO("Not yet implemented")
    }
}