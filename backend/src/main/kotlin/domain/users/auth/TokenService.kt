package domain.users.auth

import domain.security.Requester
import domain.users.User

interface TokenService {
    fun newToken(user: User): Token
    fun newResetPasswordToken(user: User): Token
    fun requesterFrom(token: Token): Requester
}