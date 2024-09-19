package domain.utils

fun interface TimeProvider {
    fun currentTimeMillis(): Long
}