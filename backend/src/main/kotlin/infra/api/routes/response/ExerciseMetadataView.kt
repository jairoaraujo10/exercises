package infra.api.routes.response

import domain.exercises.base.ExerciseId
import domain.exercises.Metadata

data class ExerciseMetadataView(val id: String?, val title: String, val tags: List<TagView>) {
    companion object {
        fun from(id: ExerciseId, metadata: Metadata): ExerciseMetadataView {
            return ExerciseMetadataView(id.value, metadata.title, metadata.tags.map { tag -> TagView.from(tag) })
        }
    }
}