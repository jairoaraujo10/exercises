package utils.domain.security

import domain.security.Requester
import domain.security.Role
import domain.users.Email
import domain.users.UserId

class RequesterFixture {
    companion object {
        fun anyRequester(): Requester {
            return builder().build()
        }

        fun builder() = RequesterBuilder()
    }

    class RequesterBuilder {
        private var userId: UserId = UserId(123L)
        private var email: Email = Email("email@mail.com")
        private var roles: Set<Role> = mutableSetOf(Role.USER)

        fun withUserId(userId: UserId) = apply {
            this.userId = userId
            this.email = Email("${userId.value}@mail.com")
        }
        fun withRoles(roles: Set<Role>) = apply { this.roles = roles }
        fun withRole(role: Role) = apply { this.roles = this.roles.plus(role) }

        fun build() = Requester(userId, email, roles)
    }
}