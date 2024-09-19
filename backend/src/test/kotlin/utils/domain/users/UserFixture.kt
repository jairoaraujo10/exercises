package utils.domain.users

import domain.security.Role
import domain.users.*
import infra.hash.Argon2HashGenerator

class UserFixture {
    companion object {
        fun anyUser(): User {
            return builder().build()
        }

        fun builder() = UserBuilder()
    }

    class UserBuilder {
        private var id: UserId = UserId(123L)
        private var name: String = "test"
        private var email: Email = Email("test@mail.com")
        private var hashedPassword: HashedPassword = HashedPassword.with(null, Argon2HashGenerator())
        private var roles: MutableSet<Role> = hashSetOf(Role.USER, Role.ADMIN)

        fun withId(id: UserId) = apply { this.id = id }
        fun withName(name: String) = apply { this.name = name }
        fun withEmail(email: Email) = apply { this.email = email }
        fun withPassword(password: PlainPassword) = apply {
            this.hashedPassword = HashedPassword.with(password, Argon2HashGenerator())
        }
        fun withRoles(roles: MutableSet<Role>) = apply { this.roles = roles }

        fun build(): User {
            return User(id, name, email, hashedPassword, roles)
        }
    }
}