package domain.exercises.list

import domain.exercises.Metadata
import domain.exercises.base.ExerciseId
import domain.exercises.list.request.UpdateExercisesListRequest
import domain.security.AccessPolicy

data class ExercisesList(
    val id: ExercisesListId,
    var metadata: Metadata,
    val exercises: ArrayList<ExerciseId>
) {
    val accessPolicy: AccessPolicy = AccessPolicy(metadata.authorId)

    fun addExercise(exerciseId: ExerciseId){
        exercises.add(exerciseId)
    }

    fun removeExercise(exerciseId: ExerciseId){
        exercises.remove(exerciseId)
    }

    fun update(updateRequest: UpdateExercisesListRequest) {
        metadata = metadata.copy(
            title = updateRequest.title ?: metadata.title,
            tags = updateRequest.tags ?: metadata.tags
        )
    }
}