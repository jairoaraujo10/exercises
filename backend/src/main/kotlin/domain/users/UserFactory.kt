package domain.users

import domain.security.Role
import domain.utils.HashGenerator

class UserFactory(
    private val hashGenerator: HashGenerator
) {
    fun createBasicUserWith(username: String, email: Email): User {
        val user = User(UserId.notInitiated(), username, email, HashedPassword.with(null, hashGenerator), HashSet())
        user.roles.add(Role.USER)
        return user
    }
}