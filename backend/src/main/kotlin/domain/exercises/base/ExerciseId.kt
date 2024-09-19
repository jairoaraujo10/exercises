package domain.exercises.base

data class ExerciseId(val value: String?) {
    companion object {
        fun notInitiated(): ExerciseId {
            return ExerciseId(null)
        }
    }

    fun initialized(): Boolean {
        return !value.isNullOrBlank()
    }

    override fun toString(): String {
        return value.orEmpty()
    }
}