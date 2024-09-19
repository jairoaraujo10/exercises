package domain.users

import domain.utils.PaginatedList
import domain.utils.PaginationParams

interface UserRepository {
    fun getUserById(id: UserId): User
    fun getUserByEmail(email: Email): User?
    fun add(user: User): User
    fun delete(user: User)
    fun update(user: User)
    fun listUsers(filter: String, paginationParams: PaginationParams): PaginatedList<User>
}