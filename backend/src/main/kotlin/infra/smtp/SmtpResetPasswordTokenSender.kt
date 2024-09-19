package infra.smtp

import application.auth.ResetPasswordTokenSender
import domain.users.Email
import domain.users.auth.Token
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import javax.mail.Message
import javax.mail.Session
import javax.mail.Transport
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage

class SmtpResetPasswordTokenSender(
    private val session: Session,
    private val fromEmail: String
) : ResetPasswordTokenSender {
    private var logger: Logger = LogManager.getLogger(SmtpResetPasswordTokenSender::class.java)

    override fun send(email: Email, token: Token) {
        try {
            val message = MimeMessage(session).apply {
                setFrom(InternetAddress(fromEmail))
                setRecipients(
                    Message.RecipientType.TO,
                    InternetAddress.parse(email.address)
                )
                subject = "Password Reset"
                setText("Please use the following token to reset your password: ${token.value}")
            }
            Transport.send(message)
            logger.info("Reset password email sent successfully to {}", email.address)
        } catch (e: Exception) {
            logger.error("Failed to send reset password email to {}", email.address, e)
            throw e
        }
    }
}