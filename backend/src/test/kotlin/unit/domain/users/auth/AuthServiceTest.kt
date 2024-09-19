package unit.domain.users.auth

import domain.users.*
import domain.users.auth.AuthService
import domain.users.auth.Unauthorized
import domain.users.auth.Token
import domain.users.auth.TokenService
import io.mockk.every
import io.mockk.mockk
import io.mockk.verifyOrder
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import utils.domain.users.UserFixture
import kotlin.test.assertFailsWith
import kotlin.test.assertNull

class AuthServiceTest {

    @Test
    fun `auth with valid credentials should return token`() {
        val token = mockk<Token>()
        val password = PlainPassword("password")
        every { userRepository.getUserByEmail(user.email) } returns user
        every { user.authenticate(password) } returns true
        every { tokenService.newToken(user) } returns token

        val result = authService.authenticate(user.email, password)

        assertEquals(token, result)
    }

    @Test
    fun `auth with invalid credentials should throw LoginFailedException`() {
        val token = mockk<Token>()
        val password = PlainPassword("password")
        every { userRepository.getUserByEmail(user.email) } returns user
        every { user.authenticate(password) } returns false
        every { tokenService.newToken(user) } returns token

        assertFailsWith<Unauthorized> {
            authService.authenticate(user.email, password)
        }
    }

    @Test
    fun `login with non-existent user should throw LoginFailedException`() {
        val email = Email("nonexistent@example.com")
        val plainPassword = PlainPassword("somePassword")
        every { userRepository.getUserByEmail(email) } returns null

        assertFailsWith<Unauthorized> {
            authService.authenticate(email, plainPassword)
        }
    }

    @Test
    fun `returns reset token if user exists`() {
        val user = UserFixture.anyUser()
        val resetToken = Token("resetToken")
        every { userRepository.getUserByEmail(user.email) } returns user
        every { tokenService.newResetPasswordToken(user) } returns resetToken

        val result = authService.generateResetPasswordToken(user.email)

        assertEquals(resetToken, result)
    }

    @Test
    fun `returns null token if user doesn't exist`() {
        val email = Email("nonexistent@example.com")
        every { userRepository.getUserByEmail(email) } returns null

        val result = authService.generateResetPasswordToken(email)

        assertNull(result)
    }

    @Test
    fun `update user password by id and persist changes`() {
        val userId = UserId(123L)
        val newPassword = PlainPassword("newPassword")
        every { userRepository.getUserById(userId) } returns user

        authService.updatePassword(userId, newPassword)

        verifyOrder {
            userRepository.getUserById(userId)
            user.updatePassword(newPassword)
            userRepository.update(user)
        }
    }

    private val user = mockk<User>(relaxed = true)
    private val userRepository = mockk<UserRepository>(relaxed = true)
    private val tokenService = mockk<TokenService>(relaxed = true)
    private val authService = AuthService(userRepository, tokenService)
}