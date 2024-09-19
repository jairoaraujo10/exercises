package unit.domain.users;

import domain.security.Role
import domain.users.Email
import domain.users.HashedPassword
import domain.users.UserFactory
import domain.utils.HashGenerator
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import kotlin.test.assertTrue

class UserFactoryTest {

    @Test
    fun `create an user with provided data`() {
        val name = "test"
        val email = Email("user@mail.com")

        val createdUser = factory.createBasicUserWith(name, email)

        assertEquals(createdUser.name, name)
        assertEquals(createdUser.email, email)
        assertEquals(createdUser.hashedPassword, HashedPassword.with(null, hashGenerator))
        assertEquals(createdUser.roles.size, 1)
        assertTrue{ createdUser.roles.contains(Role.USER) }
    }

    private val hashGenerator = mockk<HashGenerator>(relaxed = true)
    private val factory = UserFactory(hashGenerator)
}
