package infra.api

import com.google.gson.Gson
import domain.security.Requester
import domain.utils.PaginationParams
import infra.api.routes.response.SimpleMessageResponse
import spark.Request
import spark.Response
import spark.Service


abstract class AbstractRoutesDeclaration(private val gson: Gson, private val authFilter: AuthFilter) {
    abstract fun declareRoutes(service: Service)

    protected fun <T> extractBody(request: Request, bodyClass: Class<T>): T {
        return gson.fromJson(request.body(), bodyClass)
    }

    protected fun requester(): Requester {
        return authFilter.requester()
    }

    protected fun ok(response: Response, objectBody: Any): String {
        return responseBody(response, 200, objectBody)
    }

    protected fun created(response: Response): String {
        return responseBody(response, 201, SimpleMessageResponse("Created"))
    }

    protected fun noContent(response: Response): String {
        return responseBody(response, 204)
    }

    protected fun responseBody(response: Response, statusCode: Int, objectBody: Any? = null): String {
        response.status(statusCode)
        response.type("application/json")
        return if (objectBody != null) gson.toJson(objectBody) else ""
    }

    protected fun paginationParamsFrom(request: Request): PaginationParams {
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