package configuration

class SecurityConfig {
    companion object {
        fun jwtSecret(): String = System.getProperty("JWT_SECRET") ?: System.getenv("JWT_SECRET")
    }
}