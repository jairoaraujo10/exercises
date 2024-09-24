package domain.exercises.list

import domain.exercises.Metadata
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
            Metadata(
                request.title,
                request.tags.toMutableSet(),
                authorId,
                timeProvider.currentTimeMillis()
            ),
            ArrayList()
        )
    }
}