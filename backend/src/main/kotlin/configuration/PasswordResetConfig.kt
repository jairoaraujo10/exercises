package configuration

class PasswordResetConfig {
    companion object {
        fun resetPasswordBaseEndpoint(): String =
            System.getProperty("RESET_PASSWORD_BASE_ENDPOINT") ?: System.getenv("RESET_PASSWORD_BASE_ENDPOINT")
    }
}