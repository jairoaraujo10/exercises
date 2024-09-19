package infra.api.routes

import application.users.UserController
import com.google.gson.Gson
import domain.users.UserId
import domain.utils.PaginationParams
import infra.api.AbstractRoutesDeclaration
import infra.api.AuthFilter
import infra.api.routes.request.CreateUserApiRequestBody
import infra.api.routes.response.ListUsersResponseView
import infra.api.routes.response.UserView
import spark.Request
import spark.Response
import spark.Service

class UserRoutesDeclaration(
    gson: Gson, private val authFilter: AuthFilter, private val userController: UserController
) : AbstractRoutesDeclaration(gson, authFilter) {

    companion object {
        const val BASE_ENDPOINT = "/user"
    }

    override fun declareRoutes(service: Service) {
        authFilter.applyTo(service, BASE_ENDPOINT)
        authFilter.applyTo(service, "$BASE_ENDPOINT/*")
        service.get("$BASE_ENDPOINT/:id", this::getUser)
        service.post(BASE_ENDPOINT, this::createUser)
        service.delete("$BASE_ENDPOINT/:id", this::deleteUser)
        service.post("${BASE_ENDPOINT}/search", this::searchUsers)
    }

    private fun getUser(request: Request, response: Response): String {
        val userId = UserId(request.params(":id").toLong())
        val user = userController.getUser(userId, requester())
        return ok(response, UserView.from(user))
    }

    private fun createUser(request: Request, response: Response): String {
        val requestBody = extractBody(request, CreateUserApiRequestBody::class.java)
        userController.createUser(requestBody.name(), requestBody.email(), requester())
        return created(response)
    }

    private fun deleteUser(request: Request, response: Response): String {
        val userId = UserId(request.params(":id").toLong())
        userController.delete(userId, requester())
        return noContent(response)
    }

    private fun searchUsers(request: Request, response: Response): String {
        val userPaginatedList = userController.listUsers("", paginationParamsFrom(request), requester())
        return ok(response, ListUsersResponseView.from(userPaginatedList))
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
            throw IllegalArgumentException("Invalid query param '${key}'")
        }
    }

}