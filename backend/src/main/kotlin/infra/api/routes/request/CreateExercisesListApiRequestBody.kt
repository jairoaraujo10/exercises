package infra.api.routes.request

import domain.exercises.list.request.CreateExercisesListRequest
import infra.api.routes.response.TagView

data class CreateExercisesListApiRequestBody(
    val title: String,
    val tags: List<TagView>,
) {
    fun toRequest(): CreateExercisesListRequest {
        return CreateExercisesListRequest(
            title,
            tags.map { tagView -> tagView.toTag() }.toSet(),
        )
    }
}
