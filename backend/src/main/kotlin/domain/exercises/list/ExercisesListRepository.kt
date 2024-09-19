package domain.exercises.list

interface ExercisesListRepository {
    fun add(exercisesList: ExercisesList): ExercisesList
    fun get(id: ExercisesListId): ExercisesList
    fun update(exercisesList: ExercisesList)
    fun delete(exercisesList: ExercisesList)
}
