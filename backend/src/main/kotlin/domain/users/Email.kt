package domain.users

data class Email(val address: String) {
    companion object {
        private const val EMAIL_ADDRESS_PATTERN = "^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}$"
    }

    init {
        require(address.isNotBlank()) { "Email cannot be empty" }
        require(Regex(EMAIL_ADDRESS_PATTERN).matches(address)) { "Invalid email format" }
    }

    override fun toString(): String {
        return address
    }
}