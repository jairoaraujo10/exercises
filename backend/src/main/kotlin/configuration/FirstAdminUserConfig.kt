package configuration

import domain.users.Email
import domain.users.PlainPassword

class FirstAdminUserConfig {
    companion object {
        fun username(): String = System.getProperty("ADMIN_USERNAME") ?: System.getenv("ADMIN_USERNAME")
        fun email(): Email = Email(System.getProperty("ADMIN_EMAIL") ?: System.getenv("ADMIN_EMAIL"))
        fun password(): PlainPassword =
            PlainPassword(System.getProperty("ADMIN_PASSWORD") ?: System.getenv("ADMIN_PASSWORD"))
    }
}