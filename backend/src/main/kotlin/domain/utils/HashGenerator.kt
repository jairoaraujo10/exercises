package domain.utils

interface HashGenerator {
    fun generate(input: String): String
    fun verifyHash(hashedInput: String, input: String): Boolean
}