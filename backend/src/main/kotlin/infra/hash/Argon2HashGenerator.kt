package infra.hash

import de.mkammerer.argon2.Argon2
import de.mkammerer.argon2.Argon2Factory
import domain.utils.HashGenerator

class Argon2HashGenerator : HashGenerator {
    override fun generate(input: String): String {
        val argon2: Argon2 = Argon2Factory.create(Argon2Factory.Argon2Types.ARGON2id)
        try {
            return argon2.hash(10, 65536, 1, input.toCharArray())
        } finally {
            argon2.wipeArray(input.toCharArray())
        }
    }

    override fun verifyHash(hashedInput: String, input: String): Boolean {
        val argon2: Argon2 = Argon2Factory.create(Argon2Factory.Argon2Types.ARGON2id)
        try {
            return argon2.verify(hashedInput, input.toCharArray())
        } finally {
            argon2.wipeArray(input.toCharArray())
        }
    }
}