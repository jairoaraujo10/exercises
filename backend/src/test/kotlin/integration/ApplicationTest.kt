package integration

import Application
import com.icegreen.greenmail.util.ServerSetupTest
import integration.infrastructure.jdbc.DataSourceTestContext
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class ApplicationTest {
    @Test
    fun `starts and stops successfully the application`() {
        val application = Application(dataSourceContext = DataSourceTestContext::class.java)
        application.start()
        application.stop()
    }

    @BeforeEach
    fun setUp() {
        System.setProperty("JWT_SECRET", "a-jwt-secret")
        System.setProperty("ADMIN_USERNAME", "testAdmin")
        System.setProperty("ADMIN_EMAIL", "test@example.com")
        System.setProperty("ADMIN_PASSWORD", "testPassword")
        System.setProperty("SMTP_HOST", "localhost")
        System.setProperty("SMTP_PORT", ServerSetupTest.SMTP.port.toString())
        System.setProperty("SMTP_FROM_EMAIL", "noreply@example.com")
        System.setProperty("SMTP_USERNAME", "testUser")
        System.setProperty("SMTP_PASSWORD", "testPass")
    }
}