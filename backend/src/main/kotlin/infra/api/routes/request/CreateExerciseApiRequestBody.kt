package infra.api.routes.request

import domain.exercises.base.request.CreateExerciseRequest
import infra.api.routes.response.TagView

data class CreateExerciseApiRequestBody(
    val title: String,
    val description: String,
    val tags: List<TagView>,
    val possibleAnswers: List<String>,
    val correctAnswerIndex: Int
) {
    fun toRequest(): CreateExerciseRequest {
        return CreateExerciseRequest(
            title,
            tags.map { tagView -> tagView.toTag() }.toSet(),
            description,
            possibleAnswers,
            correctAnswerIndex
        )
    }
}