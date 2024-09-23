package integration.infrastructure.smtp

import application.auth.ResetPasswordTokenSender
import com.icegreen.greenmail.util.GreenMail
import com.icegreen.greenmail.util.ServerSetupTest
import domain.users.Email
import domain.users.auth.Token
import infra.smtp.SmtpResetPasswordTokenSender
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import javax.mail.Message
import javax.mail.Session
import javax.mail.internet.InternetAddress

class SmtpResetPasswordTokenSenderTest {
    private lateinit var greenMail: GreenMail
    private lateinit var sender: ResetPasswordTokenSender

    @BeforeEach
    fun setUp() {
        greenMail = GreenMail(ServerSetupTest.SMTP)
        greenMail.start()
        val props = System.getProperties().apply {
            put("mail.smtp.host", "localhost")
            put("mail.smtp.port", ServerSetupTest.SMTP.port.toString())
        }
        val session = Session.getInstance(props)
        sender = SmtpResetPasswordTokenSender(
            session = session,
            fromEmail = "noreply@example.com",
            baseEndpoint = "http://localhost:8080/password-reset",
        )
    }

    @AfterEach
    fun tearDown() {
        greenMail.stop()
    }

    @Test
    fun `should send reset password token email`() {
        val testEmail = Email("test@example.com")
        val testToken = Token("123456")

        sender.send(testEmail, testToken)

        greenMail.waitForIncomingEmail(1)

        val messages = greenMail.receivedMessages
        assertEquals(1, messages.size)

        val message = messages[0]
        assertEquals("Password Reset", message.subject)
        assertEquals("noreply@example.com", (message.from[0] as InternetAddress).address)
        assertEquals(
            "test@example.com",
            (message.getRecipients(Message.RecipientType.TO)[0] as InternetAddress).address
        )
        val content = message.content as String
        assertEquals(
            "Reset password url: http://localhost:8080/password-reset?token=123456",
            content.trim()
        )
    }
}