package unit.domain.security

import domain.security.PermissionValidator
import domain.security.AccessForbiddenException
import domain.security.AccessPolicy
import domain.security.Role
import domain.users.UserId
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertDoesNotThrow
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import utils.domain.security.RequesterFixture

class PermissionValidatorTest {

    @Test
    fun `validate permission`() {
        assertDoesNotThrow { permissionValidator.validatePermission(userRequester, Role.USER) }
        assertDoesNotThrow { permissionValidator.validatePermission(adminRequester, Role.USER) }

        val exception = assertThrows<AccessForbiddenException> {
            permissionValidator.validatePermission(noRolesRequester, Role.USER)
        }
        assertEquals("You do not have required permission.", exception.message)
    }

    @Test
    fun `validate permission to view`() {
        assertDoesNotThrow { permissionValidator.validatePermissionToView(userRequester, accessPolicy) }
        assertDoesNotThrow { permissionValidator.validatePermissionToView(adminRequester, accessPolicy) }

        val exception = assertThrows<NoSuchElementException> {
            permissionValidator.validatePermissionToView(noRolesRequester, accessPolicy)
        }
        assertEquals("Not found.", exception.message)
    }

    @Test
    fun `validate permission to view before to update`() {
        assertDoesNotThrow { permissionValidator.validatePermissionToUpdate(adminRequester, accessPolicy) }

        val exception = assertThrows<NoSuchElementException> {
            permissionValidator.validatePermissionToUpdate(noRolesRequester, accessPolicy)
        }
        assertEquals("Not found.", exception.message)
    }

    @Test
    fun `validate permission to update`() {
        assertDoesNotThrow { permissionValidator.validatePermissionToUpdate(adminRequester, accessPolicy) }

        val exception = assertThrows<AccessForbiddenException> {
            permissionValidator.validatePermissionToUpdate(userRequester, accessPolicy)
        }
        assertEquals("You do not have permission to update.", exception.message)
    }

    @Test
    fun `validate permission to view before to delete`() {
        assertDoesNotThrow { permissionValidator.validatePermissionToDelete(adminRequester, accessPolicy) }

        val exception = assertThrows<NoSuchElementException> {
            permissionValidator.validatePermissionToDelete(noRolesRequester, accessPolicy)
        }
        assertEquals("Not found.", exception.message)
    }

    @Test
    fun `validate permission to delete`() {
        assertDoesNotThrow { permissionValidator.validatePermissionToDelete(adminRequester, accessPolicy) }

        val exception = assertThrows<AccessForbiddenException> {
            permissionValidator.validatePermissionToDelete(userRequester, accessPolicy)
        }
        assertEquals("You do not have permission to delete.", exception.message)
    }

    @BeforeEach
    fun setup() {
        every { accessPolicy.isAllowedToView(userRequester) } returns true
        every { accessPolicy.isAllowedToView(adminRequester) } returns true
        every { accessPolicy.isAllowedToView(noRolesRequester) } returns false
        every { accessPolicy.isAllowedToUpdate(userRequester) } returns false
        every { accessPolicy.isAllowedToUpdate(adminRequester) } returns true
        every { accessPolicy.isAllowedToUpdate(noRolesRequester) } returns false
        every { accessPolicy.isAllowedToDelete(userRequester) } returns false
        every { accessPolicy.isAllowedToDelete(adminRequester) } returns true
        every { accessPolicy.isAllowedToDelete(noRolesRequester) } returns false
    }

    private val userRequester = RequesterFixture.builder()
        .withUserId(UserId(1L))
        .withRoles(setOf(Role.USER))
        .build()
    private val adminRequester = RequesterFixture.builder()
        .withUserId(UserId(2L))
        .withRoles(setOf(Role.USER, Role.ADMIN))
        .build()
    private val noRolesRequester = RequesterFixture.builder()
        .withUserId(UserId(3L))
        .withRoles(emptySet())
        .build()
    private val accessPolicy = mockk<AccessPolicy>(relaxed = true)
    private val permissionValidator = PermissionValidator()
}