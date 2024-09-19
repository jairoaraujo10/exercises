package domain.users

data class UserId(val value: Long?) {
    companion object {
        fun notInitiated(): UserId {
            return UserId(null)
        }
    }

    override fun toString(): String {
        return value?.toString().orEmpty()
    }
}