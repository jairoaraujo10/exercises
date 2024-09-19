package unit.domain.security

import domain.security.AccessPolicy
import domain.security.Role
import domain.users.UserId
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import utils.domain.security.RequesterFixture

class AccessPolicyTest {

    @Test
    fun `allow view when user is the author or has required role`() {
        Assertions.assertTrue(adminRequiredAccessPolicy.isAllowedToView(ownerUserRequester))
        Assertions.assertTrue(adminRequiredAccessPolicy.isAllowedToView(otherAdminRequester))
        Assertions.assertFalse(adminRequiredAccessPolicy.isAllowedToView(otherUserRequester))

        adminRequiredAccessPolicy.grantViewAccessTo(Role.USER)

        Assertions.assertTrue(adminRequiredAccessPolicy.isAllowedToView(otherUserRequester))
    }

    @Test
    fun `allow update when user is the author or has required role`() {
        Assertions.assertTrue(adminRequiredAccessPolicy.isAllowedToUpdate(ownerUserRequester))
        Assertions.assertTrue(adminRequiredAccessPolicy.isAllowedToUpdate(otherAdminRequester))
        Assertions.assertFalse(adminRequiredAccessPolicy.isAllowedToUpdate(otherUserRequester))

        adminRequiredAccessPolicy.grantUpdateAccessTo(Role.USER)

        Assertions.assertTrue(adminRequiredAccessPolicy.isAllowedToUpdate(otherUserRequester))
    }

    @Test
    fun `allow deleted when user is the author or has required role`() {
        Assertions.assertTrue(adminRequiredAccessPolicy.isAllowedToDelete(ownerUserRequester))
        Assertions.assertTrue(adminRequiredAccessPolicy.isAllowedToDelete(otherAdminRequester))
        Assertions.assertFalse(adminRequiredAccessPolicy.isAllowedToDelete(otherUserRequester))

        adminRequiredAccessPolicy.grantDeleteAccessTo(Role.USER)

        Assertions.assertTrue(adminRequiredAccessPolicy.isAllowedToDelete(otherUserRequester))
    }

    private val ownerId = UserId(123L)
    private val ownerUserRequester = RequesterFixture.builder()
        .withUserId(ownerId)
        .withRole(Role.USER)
        .build()
    private val otherId = UserId(456L)
    private val otherAdminRequester = RequesterFixture.builder()
        .withUserId(otherId)
        .withRoles(setOf(Role.USER, Role.ADMIN))
        .build()
    private val otherUserRequester = RequesterFixture.builder()
        .withUserId(otherId)
        .withRole(Role.USER)
        .build()
    private val adminRequiredAccessPolicy: AccessPolicy = AccessPolicy(ownerId)
}