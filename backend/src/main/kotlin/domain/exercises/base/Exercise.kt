package domain.exercises.base

import domain.exercises.IndexMetadata
import domain.exercises.base.request.UpdateExerciseRequest
import domain.security.AccessPolicy

data class Exercise(
    val id: ExerciseId,
    var metadata: IndexMetadata,
    var content: Content
) {
    val accessPolicy: AccessPolicy = AccessPolicy(metadata.authorId)

    fun update(updateRequest: UpdateExerciseRequest) {
        metadata = metadata.copy(
            title = updateRequest.title ?: metadata.title,
            tags = updateRequest.tags ?: metadata.tags
        )
        content = content.copy(
            description = updateRequest.description ?: content.description,
            possibleAnswers = updateRequest.answers ?: content.possibleAnswers,
            correctAnswerIndex = updateRequest.correctAnswerIndex ?: content.correctAnswerIndex
        )
    }
}