package infra.api.routes.request

import domain.users.Email
import domain.users.PlainPassword

data class LoginRequest(
    private val email: String?,
    private val password: String?
) {
    fun email(): Email {
        require(email != null) { "Missing email" }
        return Email(email)
    }
    fun password(): PlainPassword {
        require(password != null) { "Missing password" }
        return PlainPassword(password)
    }
}