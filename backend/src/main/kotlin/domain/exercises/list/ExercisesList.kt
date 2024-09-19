package domain.exercises.list

import domain.exercises.Metadata
import domain.exercises.base.ExerciseId
import domain.exercises.list.request.UpdateExercisesListRequest
import domain.security.AccessPolicy

data class ExercisesList(
    val id: ExercisesListId,
    val metadata: Metadata,
    val exercises: List<ExerciseId>
) {
    fun apply(updateRequest: UpdateExercisesListRequest) {
        TODO("Not yet implemented")
    }

    val accessPolicy: AccessPolicy = AccessPolicy(metadata.authorId)
}