package infra.api.routes.request

import domain.users.Email

data class CreateUserApiRequestBody(
    val name: String?,
    val email: String?
) {
    fun name(): String {
        require(!name.isNullOrBlank()) { "Missing name" }
        return name
    }

    fun email(): Email {
        require(!email.isNullOrBlank()) { "Missing email" }
        return Email(email)
    }
}