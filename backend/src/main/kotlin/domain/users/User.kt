package domain.users

import domain.security.Role

data class User(
    val id: UserId,
    val name: String,
    val email: Email,
    val hashedPassword: HashedPassword,
    val roles: MutableSet<Role>
) {
    fun initialize(id: UserId): User {
        return User(id, name, email, hashedPassword, roles.toMutableSet())
    }

    fun authenticate(plainPassword: PlainPassword): Boolean {
        return hashedPassword.matches(plainPassword)
    }

    fun updatePassword(newPlainPassword: PlainPassword) {
        hashedPassword.update(newPlainPassword)
    }
}