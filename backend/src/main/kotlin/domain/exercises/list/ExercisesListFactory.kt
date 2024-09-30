package domain.exercises.list

import domain.exercises.IndexMetadata
import domain.exercises.list.request.CreateExercisesListRequest
import domain.users.UserId
import domain.utils.TimeProvider
import java.util.ArrayList

class ExercisesListFactory(
    private val timeProvider: TimeProvider
) {
    fun createNew(request: CreateExercisesListRequest, authorId: UserId): ExercisesList {
        return ExercisesList(
            ExercisesListId.notInitiated(),
            IndexMetadata(
                request.title,
                request.tags.toMutableSet(),
                authorId,
                timeProvider.currentTimeMillis()
            ),
            ArrayList()
        )
    }
}