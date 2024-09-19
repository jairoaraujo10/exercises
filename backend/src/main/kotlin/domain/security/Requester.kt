package domain.security

import domain.users.Email
import domain.users.UserId

data class Requester(
    val id: UserId,
    val email: Email,
    val roles: Set<Role>
)