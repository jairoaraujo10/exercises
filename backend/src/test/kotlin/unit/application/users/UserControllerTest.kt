package unit.application.users

import domain.security.PermissionValidator
import application.users.UserController
import domain.security.Requester
import domain.security.Role
import domain.users.Email
import domain.users.UserFactory
import domain.users.UserId
import domain.users.UserRepository
import domain.utils.PaginatedList
import domain.utils.PaginationParams
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.mockk.verifyOrder
import org.junit.jupiter.api.Test
import utils.domain.users.UserFixture
import utils.domain.users.UserFixture.Companion.anyUser
import kotlin.test.assertEquals

class UserControllerTest {

    @Test
    fun `create new user`() {
        controller.createUser("user", Email("email@email.com"), requester)

        verifyOrder {
            permissionValidator.validatePermission(requester, Role.ADMIN)
            factory.createBasicUserWith("user", Email("email@email.com"))
        }
    }

    @Test
    fun `returns user by id`() {
        val userId = UserId(1L)
        val expected = UserFixture.builder()
            .withId(userId)
            .build()
        every { repository.getUserById(userId) }.returns(expected)

        val result = controller.getUser(userId, requester)

        verify { permissionValidator.validatePermission(requester, Role.ADMIN) }
        assertEquals(expected, result)
    }

    @Test
    fun `delete user by id`() {
        val userId = UserId(1L)
        val user = UserFixture.builder()
            .withId(userId)
            .build()
        every { repository.getUserById(userId) }.returns(user)

        controller.delete(userId, requester)

        verifyOrder {
            permissionValidator.validatePermission(requester, Role.ADMIN)
            repository.getUserById(userId)
            repository.delete(user)
        }
    }

    @Test
    fun `list users with filter and pagination`() {
        val filter = "filter"
        val paginationParams = PaginationParams(10, 1)
        val users = PaginatedList(listOf(anyUser()), 10)
        every { repository.listUsers(filter, paginationParams) }.returns(users)

        val result = controller.listUsers(filter, paginationParams, requester)

        verify { permissionValidator.validatePermission(requester, Role.ADMIN) }
        assertEquals(users, result)
    }

    private val requester = mockk<Requester>()
    private val permissionValidator: PermissionValidator = mockk<PermissionValidator>(relaxed = true)
    private val factory: UserFactory = mockk<UserFactory>(relaxed = true)
    private val repository: UserRepository = mockk<UserRepository>(relaxed = true)
    private val controller = UserController(permissionValidator, factory, repository);
}