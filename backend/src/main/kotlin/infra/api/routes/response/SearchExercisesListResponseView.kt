package infra.api.routes.response

import domain.exercises.list.ExercisesList
import domain.utils.PaginatedList

class SearchExercisesListResponseView {
    companion object {
        fun from(exercisesListPaginated: PaginatedList<ExercisesList>): Any {
            return SearchExercisesResponseView(exercisesListPaginated.items.map { exercise ->
                IndexMetadataView.from(exercise.id, exercise.metadata)
            }, exercisesListPaginated.total)
        }
    }
}