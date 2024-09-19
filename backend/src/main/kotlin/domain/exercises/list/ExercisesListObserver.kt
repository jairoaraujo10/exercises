package domain.exercises.list

interface ExercisesListObserver {
    fun created(list: ExercisesList)
}