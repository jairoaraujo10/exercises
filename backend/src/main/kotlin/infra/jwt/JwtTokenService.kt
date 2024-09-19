package infra.jwt

import domain.users.auth.Token
import domain.users.auth.TokenService
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import domain.security.Requester
import domain.users.User
import domain.users.UserId
import domain.users.UserRepository
import java.time.ZonedDateTime
import java.util.*


class JwtTokenService(private val jwtSecretKey: String, private val userRepository: UserRepository) : TokenService {
    companion object {
        private const val PASSWORD_RESET_EXPIRATION_TIME_SECONDS = 60L * 60L
        private const val SESSION_EXPIRATION_TIME_SECONDS = 60L * 60L * 24L
    }

    override fun newResetPasswordToken(user: User): Token {
        val issuedAt = Date.from(ZonedDateTime.now().toInstant())
        val expiration = Date.from(issuedAt.toInstant().plusSeconds(PASSWORD_RESET_EXPIRATION_TIME_SECONDS))
        return Token(JWT.create()
            .withIssuedAt(issuedAt).withExpiresAt(expiration)
            .withSubject(user.id.toString())
            .sign(algorithm()))
    }

    override fun newToken(user: User): Token {
        val issuedAt = Date.from(ZonedDateTime.now().toInstant())
        val expiration = Date.from(issuedAt.toInstant().plusSeconds(SESSION_EXPIRATION_TIME_SECONDS))
        return Token(JWT.create()
            .withIssuedAt(issuedAt).withExpiresAt(expiration)
            .withSubject(user.id.toString())
            .withClaim("roles", user.roles.joinToString(","))
            .sign(algorithm()))
    }

    override fun requesterFrom(token: Token): Requester {
        val verifier = JWT.require(algorithm()).build()
        val decodedJWT = verifier.verify(token.value)
        val userId = UserId(decodedJWT.subject.toLong())
        val user: User = userRepository.getUserById(userId)
        return Requester(userId, user.email, user.roles)
    }

    private fun algorithm(): Algorithm = Algorithm.HMAC256(jwtSecretKey)
}