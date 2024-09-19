package infra.api.routes.response

import domain.users.User

data class UserView(val id: String, val name: String, val email: String, val roles: List<String>) {
    companion object {
        fun from(user: User): UserView {
            return UserView(user.id.toString(), user.name, user.email.address, user.roles.map { role -> role.name })
        }
    }
}