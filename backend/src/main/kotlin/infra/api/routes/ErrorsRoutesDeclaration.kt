package infra.api.routes

import domain.users.auth.Unauthorized
import com.auth0.jwt.exceptions.JWTVerificationException
import com.google.gson.Gson
import domain.security.AccessForbiddenException
import infra.api.AbstractRoutesDeclaration
import infra.api.AuthFilter
import infra.api.routes.response.SimpleMessageResponse
import spark.Request
import spark.Response
import spark.Service

class ErrorsRoutesDeclaration(
    gson: Gson, authFilter: AuthFilter
) : AbstractRoutesDeclaration(gson, authFilter) {

    override fun declareRoutes(service: Service) {
        service.exception(IllegalArgumentException::class.java, this::badRequest)
        service.exception(Unauthorized::class.java, this::unauthorized)
        service.exception(JWTVerificationException::class.java, this::forbidden)
        service.exception(AccessForbiddenException::class.java, this::forbidden)
        service.exception(NoSuchElementException::class.java, this::notFound)
        service.exception(Exception::class.java, this::internalServerError)
    }

    private fun badRequest(exception: Exception, request: Request, response: Response) {
        val body = responseBody(response, 400, SimpleMessageResponse(exception.message.orEmpty()))
        response.body(body)
    }

    private fun unauthorized(exception: Exception, request: Request, response: Response) {
        val body = responseBody(response, 401, SimpleMessageResponse(exception.message.orEmpty()))
        response.body(body)
    }

    private fun forbidden(exception: Exception, request: Request, response: Response) {
        val body = responseBody(response, 403, SimpleMessageResponse(exception.message.orEmpty()))
        response.body(body)
    }

    private fun notFound(exception: Exception, request: Request, response: Response) {
        val body = responseBody(response, 404, SimpleMessageResponse(exception.message.orEmpty()))
        response.body(body)
    }

    private fun internalServerError(exception: Exception, request: Request, response: Response) {
        exception.printStackTrace()
        val body = responseBody(response, 500, SimpleMessageResponse("Internal Server Error"))
        response.body(body)
    }

}