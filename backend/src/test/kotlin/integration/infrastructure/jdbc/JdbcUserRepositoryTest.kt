import com.google.gson.Gson
import domain.security.Role
import domain.users.*
import domain.utils.PaginationParams
import infra.contexts.JdbcContext
import infra.hash.Argon2HashGenerator
import infra.jdbc.JdbcUserRepository
import integration.infrastructure.jdbc.DataSourceTestContext
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension
import utils.domain.users.UserFixture

@ExtendWith(SpringExtension::class)
@ContextConfiguration(classes = [DataSourceTestContext::class, JdbcContext::class])
class JdbcUserRepositoryTest {

    @Autowired
    private lateinit var jdbcTemplate: JdbcTemplate
    private lateinit var repository: UserRepository

    @BeforeEach
    fun setup() {
        jdbcTemplate.execute("DELETE FROM users")
        repository = JdbcUserRepository(jdbcTemplate, Gson(), Argon2HashGenerator())
    }

    @Test
    fun `getUserById returns user when user exists`() {
        val user = UserFixture.builder()
            .withId(UserId(1L))
            .withName("John Doe")
            .withEmail(Email("john.doe@example.com"))
            .withPassword(PlainPassword("plainpassword"))
            .withRoles(mutableSetOf(Role.USER))
            .build()
        insertUserIntoDatabase(user)

        val result = repository.getUserById(UserId(1L))

        assertNotNull(result)
        assertEquals(user.id, result.id)
        assertEquals(user.name, result.name)
        assertEquals(user.email, result.email)
        assertEquals(user.hashedPassword.toString(), result.hashedPassword.toString())
        assertEquals(user.roles, result.roles)
    }

    @Test
    fun `getUserById throws exception when user does not exist`() {
        val userId = UserId(9999L)

        val exception = assertThrows<NoSuchElementException> {
            repository.getUserById(userId)
        }
        assertEquals("User not found", exception.message)
    }

    @Test
    fun `getUserByEmail returns user when user exists`() {
        val user = UserFixture.builder()
            .withId(UserId(1L))
            .withEmail(Email("john.doe@example.com"))
            .withPassword(PlainPassword("plainpassword"))
            .build()
        insertUserIntoDatabase(user)

        val result = repository.getUserByEmail(Email("john.doe@example.com"))

        assertNotNull(result)
        assertEquals(user.id, result?.id)
        assertEquals(user.name, result?.name)
        assertEquals(user.email, result?.email)
        assertEquals(user.hashedPassword.toString(), result?.hashedPassword.toString())
        assertEquals(user.roles, result?.roles)
    }

    @Test
    fun `getUserByEmail returns null when user does not exist`() {
        val email = Email("nonexistent@example.com")

        val result = repository.getUserByEmail(email)

        assertNull(result)
    }

    @Test
    fun `add inserts user when email does not exist`() {
        val user = UserFixture.builder()
            .withName("Jane Doe")
            .withEmail(Email("jane.doe@example.com"))
            .withPassword(PlainPassword("plainpassword"))
            .withRoles(mutableSetOf(Role.USER))
            .build()

        val result = repository.add(user)

        assertNotNull(result.id)
        assertEquals(user.name, result.name)
        assertEquals(user.email, result.email)
        assertEquals(user.hashedPassword.toString(), result.hashedPassword.toString())
        assertEquals(user.roles, result.roles)

        val insertedUser = repository.getUserById(result.id)
        assertEquals(result, insertedUser)
    }

    @Test
    fun `add throws exception when email already exists`() {
        val existingUser = UserFixture.builder()
            .withId(UserId(1L))
            .withEmail(Email("existing@example.com"))
            .withPassword(PlainPassword("plainpassword"))
            .build()
        insertUserIntoDatabase(existingUser)
        val newUser = UserFixture.builder()
            .withEmail(Email("existing@example.com")) // Same email as existing user
            .withPassword(PlainPassword("plainpassword"))
            .build()

        val exception = assertThrows<DuplicatedUserException> {
            repository.add(newUser)
        }
        assertEquals("User already exists", exception.message)
    }

    @Test
    fun `delete removes user when user exists`() {
        val user = UserFixture.builder()
            .withId(UserId(1L))
            .withEmail(Email("john.doe@example.com"))
            .withPassword(PlainPassword("plainpassword"))
            .build()
        insertUserIntoDatabase(user)

        repository.delete(user)

        assertThrows<NoSuchElementException> {
            repository.getUserById(user.id)
        }
    }

    @Test
    fun `delete does nothing when user does not exist`() {
        val user = UserFixture.builder()
            .withId(UserId(9999L))
            .withEmail(Email("nonexisting@example.com"))
            .withPassword(PlainPassword("plainpassword"))
            .build()

        assertDoesNotThrow{
            repository.delete(user)
        }
    }

    @Test
    fun `update modifies existing user`() {
        val originalUser = UserFixture.builder()
            .withId(UserId(1L))
            .withName("Original Name")
            .withEmail(Email("original@example.com"))
            .withPassword(PlainPassword("originalpassword"))
            .withRoles(mutableSetOf(Role.USER))
            .build()
        insertUserIntoDatabase(originalUser)
        originalUser.updatePassword(PlainPassword("updatedpassword"))
        val updatedUser = originalUser.copy(
            name = "Updated Name",
            email = Email("updated@example.com"),
            hashedPassword = originalUser.hashedPassword,
            roles = mutableSetOf(Role.ADMIN)
        )

        repository.update(updatedUser)

        val result = repository.getUserById(originalUser.id)
        assertEquals(updatedUser.name, result.name)
        assertEquals(updatedUser.email, result.email)
        assertEquals(updatedUser.hashedPassword.toString(), result.hashedPassword.toString())
        assertEquals(updatedUser.roles, result.roles)
    }

    @Test
    fun `update throws exception when user does not exist`() {
        val nonExistingUser = UserFixture.builder()
            .withId(UserId(9999L))
            .withName("Non Existing")
            .withEmail(Email("nonexisting@example.com"))
            .withPassword(PlainPassword("password"))
            .withRoles(mutableSetOf(Role.USER))
            .build()

        val exception = assertThrows<NoSuchElementException> {
            repository.update(nonExistingUser)
        }
        assertEquals("User not found", exception.message)
    }

    @Test
    fun `update throws exception when email already exists`() {
        val user1 = UserFixture.builder()
            .withId(UserId(1L))
            .withEmail(Email("user1@example.com"))
            .build()
        val user2 = UserFixture.builder()
            .withId(UserId(2L))
            .withEmail(Email("user2@example.com"))
            .build()
        insertUserIntoDatabase(user1)
        insertUserIntoDatabase(user2)
        val updatedUser1 = user1.copy(email = user2.email)

        val exception = assertThrows<DuplicatedUserException> {
            repository.update(updatedUser1)
        }
        assertEquals("Email already exists", exception.message)
    }

    @Test
    fun `listUsers returns users matching filter and pagination`() {
        val user1 = UserFixture.builder()
            .withId(UserId(1L))
            .withName("Alice")
            .withEmail(Email("alice@example.com"))
            .build()

        val user2 = UserFixture.builder()
            .withId(UserId(2L))
            .withName("Bob")
            .withEmail(Email("bob@example.com"))
            .build()

        val user3 = UserFixture.builder()
            .withId(UserId(3L))
            .withName("Charlie")
            .withEmail(Email("charlie@example.com"))
            .build()

        val users = listOf(user1, user2, user3)
        users.forEach { insertUserIntoDatabase(it) }

        val paginationParams = PaginationParams(limit = 2, offset = 0)

        val result = repository.listUsers("a", paginationParams)

        assertEquals(2, result.items.size)
        assertEquals(2, result.total)
        val expectedUsers = listOf(user1, user3).sortedBy { it.name }
        val actualUsers = result.items.sortedBy { it.name }
        assertEquals(expectedUsers, actualUsers)
    }

    @Test
    fun `listUsers returns empty list when no users match filter`() {
        val paginationParams = PaginationParams(limit = 10, offset = 0)

        val result = repository.listUsers("non-matching-filter", paginationParams)

        assertTrue(result.items.isEmpty())
        assertEquals(0, result.total)
    }

    private fun insertUserIntoDatabase(user: User) {
        jdbcTemplate.update(
            "INSERT INTO users (id, name, email, password, roles) VALUES (?, ?, ?, ?, ?)",
            user.id.toString(),
            user.name,
            user.email.toString(),
            user.hashedPassword.toString(),
            Gson().toJson(user.roles)
        )
    }
}