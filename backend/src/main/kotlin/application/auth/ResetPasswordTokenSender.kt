package application.auth

import domain.users.Email
import domain.users.auth.Token

fun interface ResetPasswordTokenSender {
    fun send(email: Email, token: Token)
}