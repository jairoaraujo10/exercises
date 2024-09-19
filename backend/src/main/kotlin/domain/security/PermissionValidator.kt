package domain.security

class PermissionValidator {
    fun validatePermission(requester: Requester, requiredRole: Role) {
        if(!requester.roles.contains(requiredRole) ) {
            throw AccessForbiddenException("You do not have required permission.")
        }
    }

    fun validatePermissionToView(requester: Requester, accessPolicy: AccessPolicy) {
        if(!accessPolicy.isAllowedToView(requester)) {
            throw NoSuchElementException("Not found.")
        }
    }

    fun validatePermissionToUpdate(requester: Requester, accessPolicy: AccessPolicy) {
        validatePermissionToView(requester, accessPolicy)
        if(!accessPolicy.isAllowedToUpdate(requester)) {
            throw AccessForbiddenException("You do not have permission to update.")
        }
    }

    fun validatePermissionToDelete(requester: Requester, accessPolicy: AccessPolicy) {
        validatePermissionToView(requester, accessPolicy)
        if(!accessPolicy.isAllowedToDelete(requester)) {
            throw AccessForbiddenException("You do not have permission to delete.")
        }
    }
}