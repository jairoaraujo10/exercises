package domain.users

import domain.utils.HashGenerator

data class HashedPassword(private var hashedPassword: String?, private val hashGenerator: HashGenerator) {
    init {
        if(hashedPassword.isNullOrBlank()) {
            hashedPassword = null
        }
    }
    companion object {
        fun with(plainPassword: PlainPassword?, hashGenerator: HashGenerator): HashedPassword {
            if(plainPassword != null) {
                val hashedPassword = hashGenerator.generate(plainPassword.value)
                return HashedPassword(hashedPassword, hashGenerator)
            }
            return HashedPassword(null, hashGenerator)
        }
    }

    fun matches(plainPassword: PlainPassword): Boolean {
        if (hashedPassword.isNullOrBlank()) {
            return false
        }
        return hashGenerator.verifyHash(hashedPassword!!, plainPassword.value)
    }

    fun update(plainPassword: PlainPassword) {
        this.hashedPassword = this.hashGenerator.generate(plainPassword.value)
    }

    override fun toString(): String {
        return hashedPassword.orEmpty()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as HashedPassword
        return hashedPassword == other.hashedPassword && hashGenerator.javaClass == other.hashGenerator.javaClass
    }

    override fun hashCode(): Int {
        return hashedPassword?.hashCode() ?: 0
    }
}