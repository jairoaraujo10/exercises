package infra.contexts

import application.auth.AuthController
import application.auth.ResetPasswordTokenSender
import configuration.SecurityConfig
import configuration.SmtpConfig
import domain.users.UserRepository
import domain.users.auth.AuthService
import infra.api.AuthFilter
import infra.jwt.JwtTokenService
import infra.smtp.SmtpResetPasswordTokenSender
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import javax.mail.Authenticator
import javax.mail.PasswordAuthentication
import javax.mail.Session


@Configuration
open class AuthContext {
    @Autowired
    lateinit var userRepository: UserRepository

    private fun tokenService() = JwtTokenService(SecurityConfig.jwtSecret(), userRepository)

    @Bean
    open fun authService() = AuthService(userRepository, tokenService())

    @Bean
    open fun authController() = AuthController(authService(), resetPasswordTokenSender())

    private fun resetPasswordTokenSender(): ResetPasswordTokenSender {
        val props = System.getProperties().apply {
            put("mail.smtp.host", SmtpConfig.host())
            put("mail.smtp.socketFactory.port", SmtpConfig.port().toString());
            put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory")
            put("mail.smtp.auth", "true")
            put("mail.smtp.port", SmtpConfig.port().toString())
        }
        val session = Session.getInstance(props, object : Authenticator() {
            override fun getPasswordAuthentication(): PasswordAuthentication {
                return PasswordAuthentication(SmtpConfig.username(), SmtpConfig.password())
            }
        })
        return SmtpResetPasswordTokenSender(session, SmtpConfig.fromEmail())
    }

    @Bean
    open fun authFilter() = AuthFilter(tokenService())
}