package configuration

class SmtpConfig {
    companion object {
        fun host(): String = System.getProperty("SMTP_HOST") ?: System.getenv("SMTP_HOST")
        fun port(): Int  = (System.getProperty("SMTP_PORT") ?: System.getenv("SMTP_PORT")).toInt()
        fun username(): String = System.getProperty("SMTP_USERNAME") ?: System.getenv("SMTP_USERNAME")
        fun password(): String = System.getProperty("SMTP_PASSWORD") ?: System.getenv("SMTP_PASSWORD")
        fun fromEmail(): String = System.getProperty("SMTP_FROM_EMAIL") ?: System.getenv("SMTP_FROM_EMAIL")
    }
}