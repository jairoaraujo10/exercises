package infra.api.routes.response

import domain.exercises.base.Exercise
import domain.utils.PaginatedList

data class SearchExercisesResponseView(val exercises: List<IndexMetadataView>, val total: Long) {
    companion object {
        fun from(exercisePaginated: PaginatedList<Exercise>): Any {
            return SearchExercisesResponseView(exercisePaginated.items.map { exercise ->
                IndexMetadataView.from(exercise.id.value!!, exercise.metadata)
            }, exercisePaginated.total)
        }
    }
}