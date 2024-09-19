package unit.domain.users

import domain.users.PlainPassword
import org.junit.jupiter.api.Test
import utils.domain.users.UserFixture
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class UserTest {

    @Test
    fun `auth returns true if password matches`() {
        val plainPassword = PlainPassword("validPassword")

        val user = UserFixture.builder()
            .withPassword(plainPassword)
            .build()

        assertTrue { user.authenticate(plainPassword) }
    }

    @Test
    fun `auth returns false if password does not match`() {
        val plainPassword = PlainPassword("validPassword")

        val user = UserFixture.builder()
            .withPassword(plainPassword)
            .build()

        assertFalse { user.authenticate(PlainPassword("invalidPassword")) }
    }

    @Test
    fun `updatePassword changes the user's password`() {
        val oldPassword = PlainPassword("oldPassword")
        val newPassword = PlainPassword("newPassword")
        val user = UserFixture.builder()
            .withPassword(oldPassword)
            .build()
        assertTrue { user.authenticate(oldPassword) }

        user.updatePassword(newPassword)

        assertTrue { user.authenticate(newPassword) }
        assertFalse { user.authenticate(oldPassword) }
    }
}