package domain.users.auth

data class Token(val value: String) {
    init {
        require(value.isNotBlank()) { "Token value must not be blank" }
    }
}