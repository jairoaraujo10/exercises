package infra.api.routes.response

import domain.users.User
import domain.utils.PaginatedList

data class ListUsersResponseView(val users: List<UserView>, val total: Long) {
    companion object {
        fun from(userPaginatedList: PaginatedList<User>): Any {
            return ListUsersResponseView(userPaginatedList.items.map { user ->
                UserView.from(user)
            }, userPaginatedList.total)
        }
    }
}