package infra.api.routes.response

import domain.exercises.base.Exercise
import domain.utils.PaginatedList

data class SearchExercisesResponseView(val exercises: List<ExerciseMetadataView>, val total: Long) {
    companion object {
        fun from(exercisePaginatedList: PaginatedList<Exercise>): Any {
            return SearchExercisesResponseView(exercisePaginatedList.items.map { exercise ->
                ExerciseMetadataView.from(exercise.id, exercise.metadata)
            }, exercisePaginatedList.total)
        }
    }
}