package infra.api.routes.request

import domain.exercises.Tag
import domain.exercises.list.request.UpdateExercisesListRequest
import infra.api.routes.response.TagView

class UpdateExercisesListApiRequestBody(
    val title: String?,
    val tags: Set<TagView>?
) {
    fun toRequest(): UpdateExercisesListRequest {
        return UpdateExercisesListRequest(title, tags?.map{ tagView -> Tag(tagView.value) }?.toSet())
    }
}
