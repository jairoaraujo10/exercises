package infra.api.routes

import application.auth.AuthController
import com.google.gson.Gson
import domain.users.auth.Unauthorized
import infra.api.AbstractRoutesDeclaration
import infra.api.AuthFilter
import infra.api.routes.request.LoginRequest
import infra.api.routes.response.SimpleMessageResponse
import infra.api.routes.response.TokenResponse
import spark.Request
import spark.Response
import spark.Service

class AuthRoutesDeclaration(
    gson: Gson, private val authFilter: AuthFilter, private val authController: AuthController
) : AbstractRoutesDeclaration(gson, authFilter) {

    companion object {
        const val BASE_ENDPOINT = "/auth"
    }

    override fun declareRoutes(service: Service) {
        service.post("$BASE_ENDPOINT/login", this::login)
        service.post("$BASE_ENDPOINT/reset-password/request", this::resetPasswordRequest)
        authFilter.applyTo(service, "${BASE_ENDPOINT}/reset-password")
        service.post("$BASE_ENDPOINT/reset-password", this::resetPassword)
    }

    private fun login(request: Request, response: Response): String {
        val loginRequest = extractBody(request, LoginRequest::class.java)
        val accessToken = authController.login(loginRequest.email(), loginRequest.password())
        return ok(response, TokenResponse(accessToken.value))
    }

    private fun resetPasswordRequest(request: Request, response: Response): String {
        val loginRequest = extractBody(request, LoginRequest::class.java)
        authController.requestResetPassword(loginRequest.email())
        return noContent(response)
    }

    private fun resetPassword(request: Request, response: Response): String {
        val loginRequest = extractBody(request, LoginRequest::class.java)
        authController.resetPassword(loginRequest.password(), requester())
        return ok(response, SimpleMessageResponse("Password reset successfully"))
    }

}