package infra.api.routes.request

import domain.exercises.base.ExerciseId

data class AddExerciseRequest(val exerciseId: String) {
    fun asExerciseId(): ExerciseId {
        return ExerciseId(exerciseId)
    }
}