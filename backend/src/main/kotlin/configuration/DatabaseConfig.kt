package configuration

class DatabaseConfig {
    companion object {
        fun dbUrl(): String = System.getProperty("MYSQL_URL") ?: System.getenv("MYSQL_URL")
        fun dbUsername(): String = System.getProperty("MYSQL_USERNAME") ?: System.getenv("MYSQL_USERNAME")
        fun dbPassword(): String = System.getProperty("MYSQL_PASSWORD") ?: System.getenv("MYSQL_PASSWORD")
        fun dbSchema(): String = "exercises"
    }
}