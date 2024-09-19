package unit.application.auth

import application.auth.*
import domain.users.Email
import domain.users.PlainPassword
import domain.users.auth.AuthService
import domain.users.auth.Token
import io.mockk.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import utils.domain.security.RequesterFixture

class AuthControllerTest {

    @Test
    fun `delegates authentication to auth service`() {
        val email = Email("user@example.com")
        val plainPassword = PlainPassword("password")
        val token = mockk<Token>()
        every { authService.authenticate(any(), any()) } returns token

        val result = controller.login(email, plainPassword)

        verify { authService.authenticate(email, plainPassword) }
        assertEquals(token, result)
    }

    @Test
    fun `send reset token if not null`() {
        val email = Email("user@example.com")
        val token = mockk<Token>()
        every { authService.generateResetPasswordToken(any()) } returns token

        controller.requestResetPassword(email)

        verify { authService.generateResetPasswordToken(email) }
        verify { resetPasswordTokenSender.send(email, token) }
    }

    @Test
    fun `do nothing when generated token is null`() {
        val email = Email("user@example.com")
        every { authService.generateResetPasswordToken(any()) } returns null

        controller.requestResetPassword(email)

        verify { authService.generateResetPasswordToken(email) }
        verify(exactly = 0) { resetPasswordTokenSender.send(any(), any()) }
    }

    @Test
    fun `delegates password update to auth service`() {
        val newPlainPassword = PlainPassword("newPassword")
        val requester = RequesterFixture.anyRequester()

        controller.resetPassword(newPlainPassword, requester)

        verify { authService.updatePassword(requester.id, newPlainPassword) }
    }

    private val authService: AuthService = mockk<AuthService>(relaxed = true)
    private val resetPasswordTokenSender = mockk<ResetPasswordTokenSender>(relaxed = true)
    private val controller = AuthController(authService, resetPasswordTokenSender)
}