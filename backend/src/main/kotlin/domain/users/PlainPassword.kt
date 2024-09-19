package domain.users

data class PlainPassword(val value: String) {
    init {
        require(value.isNotBlank()) { "Password cannot be empty" }
        require(value.length >= 8) { "Password must be at least 8 characters long" }
    }
}