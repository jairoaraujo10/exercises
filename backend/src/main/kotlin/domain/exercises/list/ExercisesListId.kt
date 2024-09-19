package domain.exercises.list

class ExercisesListId(val value: String?) {
    companion object {
        fun notInitiated(): ExercisesListId {
            return ExercisesListId(null)
        }
    }

}
