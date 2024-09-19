package domain.users.auth

import domain.users.*

class AuthService(
    private val userRepository: UserRepository,
    private val tokenService: TokenService
) {
    fun authenticate(email: Email, plainPassword: PlainPassword): Token {
        val user: User = userRepository.getUserByEmail(email)
            ?: throw Unauthorized("Invalid credentials")
        if (!user.authenticate(plainPassword)) {
            throw Unauthorized("Invalid credentials")
        }
        return tokenService.newToken(user)
    }

    fun generateResetPasswordToken(email: Email): Token? {
        return userRepository.getUserByEmail(email)?.let { user ->
            tokenService.newResetPasswordToken(user)
        }
    }

    fun updatePassword(userId: UserId, newPlainPassword: PlainPassword) {
        val user: User = userRepository.getUserById(userId)
        user.updatePassword(newPlainPassword)
        userRepository.update(user)
    }
}