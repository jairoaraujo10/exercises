package domain.security

import domain.users.UserId

class AccessRule(
    private val allowedIds: MutableSet<UserId>,
    private val requiredRoles: MutableSet<Role>
) {
    fun grantAccessTo(role: Role) {
        requiredRoles.add(role)
    }

    fun allows(requester: Requester): Boolean {
        return allowedIds.contains(requester.id) || requester.roles.any { it in requiredRoles }
    }
}