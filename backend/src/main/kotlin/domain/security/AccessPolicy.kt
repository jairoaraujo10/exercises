package domain.security

import domain.users.UserId

class AccessPolicy(
    private val ownerId: UserId,
    private val viewAccessRule: AccessRule = AccessRule(mutableSetOf(), mutableSetOf(Role.ADMIN)),
    private val updateAccessRule: AccessRule = AccessRule(mutableSetOf(), mutableSetOf(Role.ADMIN)),
    private val deleteAccessRule: AccessRule = AccessRule(mutableSetOf(), mutableSetOf(Role.ADMIN))
) {
    fun isAllowedToView(requester: Requester): Boolean {
        return requester.id == ownerId || viewAccessRule.allows(requester)
    }

    fun grantViewAccessTo(role: Role) {
        viewAccessRule.grantAccessTo(role)
    }

    fun isAllowedToUpdate(requester: Requester): Boolean {
        return requester.id == ownerId || updateAccessRule.allows(requester)
    }

    fun grantUpdateAccessTo(role: Role) {
        updateAccessRule.grantAccessTo(role)
    }

    fun isAllowedToDelete(requester: Requester): Boolean {
        return requester.id == ownerId || deleteAccessRule.allows(requester)
    }

    fun grantDeleteAccessTo(role: Role) {
        deleteAccessRule.grantAccessTo(role)
    }
}