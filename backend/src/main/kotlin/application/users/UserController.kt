package application.users

import domain.utils.PaginatedList
import domain.security.PermissionValidator
import domain.security.Requester
import domain.security.Role
import domain.users.*
import domain.utils.PaginationParams

class UserController(
    private val permissionValidator: PermissionValidator,
    private val factory: UserFactory,
    private val repository: UserRepository
) {
    fun getUser(userId: UserId, requester: Requester): User {
        permissionValidator.validatePermission(requester, Role.ADMIN)
        return repository.getUserById(userId)
    }

    fun createUser(username: String, email: Email, requester: Requester) {
        permissionValidator.validatePermission(requester, Role.ADMIN)
        val user = factory.createBasicUserWith(username, email)
        repository.add(user)
    }

    fun delete(userId: UserId, requester: Requester) {
        permissionValidator.validatePermission(requester, Role.ADMIN)
        val user = repository.getUserById(userId)
        repository.delete(user)
    }

    fun listUsers(filter: String, paginationParams: PaginationParams, requester: Requester): PaginatedList<User> {
        permissionValidator.validatePermission(requester, Role.ADMIN)
        return repository.listUsers(filter, paginationParams)
    }
}