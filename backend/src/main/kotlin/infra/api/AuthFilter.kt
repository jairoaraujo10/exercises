package infra.api

import domain.security.Requester
import domain.users.auth.Token
import domain.users.auth.TokenService
import domain.users.auth.Unauthorized
import spark.Request
import spark.Response
import spark.Service


class AuthFilter(private val tokenService: TokenService) {
    companion object {
        private const val TOKEN_HEADER = "Authorization"
        private const val TOKEN_PREFIX = "Bearer"
    }

    private val threadLocalRequester: ThreadLocal<Requester> = ThreadLocal()

    fun applyTo(service: Service, path: String) {
        service.before(path, this::setRequesterFrom)
        service.afterAfter(path, this::unsetRequester)
    }

    private fun setRequesterFrom(request: Request, response: Response) {
        if (request.requestMethod().lowercase() != "options") {
            val authorizationHeader = request.headers(TOKEN_HEADER) ?: ""
            val authorization: String = authorizationHeader.replace(TOKEN_PREFIX, "").trim()
            if (authorization.isBlank()) {
                throw Unauthorized("Unauthorized")
            }
            val token = Token(authorization)
            val requester: Requester = tokenService.requesterFrom(token)
            threadLocalRequester.set(requester)
        }
    }

    private fun unsetRequester(request: Request, response: Response) {
        if (request.requestMethod().lowercase() != "options") {
            threadLocalRequester.remove()
        }
    }

    fun requester(): Requester {
        return threadLocalRequester.get()
    }
}