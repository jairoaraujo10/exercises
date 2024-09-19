package infra.api.routes.response

import domain.exercises.base.Exercise

data class ExerciseView(
    val id: String?, val title: String, val description: String, val possibleAnswers: List<String>,
    val correctAnswerIndex: Int, val tags: List<TagView>
) {
    companion object {
        fun from(exercise: Exercise): ExerciseView {
            return ExerciseView(
                exercise.id.value, exercise.metadata.title, exercise.content.description,
                exercise.content.possibleAnswers,
                exercise.content.correctAnswerIndex, exercise.metadata.tags.map { tag -> TagView.from(tag) }
            )
        }
    }
}