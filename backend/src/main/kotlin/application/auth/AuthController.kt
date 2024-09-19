package application.auth

import domain.security.Requester
import domain.users.Email
import domain.users.PlainPassword
import domain.users.auth.AuthService
import domain.users.auth.Token

class AuthController(
    private val authService: AuthService,
    private val resetPasswordTokenSender: ResetPasswordTokenSender
) {
    fun login(email: Email, password: PlainPassword): Token {
        return authService.authenticate(email, password)
    }

    fun requestResetPassword(email: Email) {
        authService.generateResetPasswordToken(email)?.let { token ->
            resetPasswordTokenSender.send(email, token)
        }
    }

    fun resetPassword(newPassword: PlainPassword, requester: Requester) {
        authService.updatePassword(requester.id, newPassword)
    }
}