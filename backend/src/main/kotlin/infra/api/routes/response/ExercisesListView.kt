package infra.api.routes.response

import domain.exercises.list.ExercisesList

class ExercisesListView(
    val id: String?, val title: String, val tags: List<TagView>
) {
    companion object {
        fun from(exercisesList: ExercisesList): ExercisesListView {
            return ExercisesListView(
                exercisesList.id.value, exercisesList.metadata.title,
                exercisesList.metadata.tags.map { tag -> TagView.from(tag) }
            )
        }
    }
}