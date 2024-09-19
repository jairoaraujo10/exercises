package infra.api.routes.request

import domain.exercises.base.request.UpdateExerciseRequest
import domain.exercises.Tag
import infra.api.routes.response.TagView

data class UpdateExerciseApiRequestBody(
    val title: String?,
    val description: String?,
    val possibleAnswers: List<String>?,
    val correctAnswerIndex: Int?,
    val tags: Set<TagView>?
) {
    fun toRequest(): UpdateExerciseRequest {
        return UpdateExerciseRequest(title, description, possibleAnswers, correctAnswerIndex,
            tags?.map{ tagView -> Tag(tagView.value) }?.toSet()
        )
    }
}