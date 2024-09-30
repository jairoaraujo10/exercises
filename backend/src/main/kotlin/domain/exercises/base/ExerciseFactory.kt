package domain.exercises.base

import domain.exercises.IndexMetadata
import domain.exercises.base.request.CreateExerciseRequest
import domain.users.UserId
import domain.utils.TimeProvider

class ExerciseFactory(
    private val timeProvider: TimeProvider
) {
    fun createNew(request: CreateExerciseRequest, authorId: UserId): Exercise {
        return Exercise(
            ExerciseId.notInitiated(),
            IndexMetadata(
                request.title,
                request.tags.toMutableSet(),
                authorId,
                timeProvider.currentTimeMillis()
            ),
            Content(
                request.description,
                request.possibleAnswers,
                request.correctAnswerIndex
            )
        )
    }
}