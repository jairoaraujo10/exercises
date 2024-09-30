package infra.api.routes.response

import domain.exercises.base.ExerciseId
import domain.exercises.IndexMetadata
import domain.exercises.list.ExercisesListId

data class IndexMetadataView(val id: String?, val title: String, val tags: List<TagView>) {
    companion object {
        fun from(id: ExerciseId, metadata: IndexMetadata): IndexMetadataView {
            return IndexMetadataView(id.value, metadata.title, metadata.tags.map { tag -> TagView.from(tag) })
        }

        fun from(id: ExercisesListId, metadata: IndexMetadata): IndexMetadataView {
            return IndexMetadataView(id.value, metadata.title, metadata.tags.map { tag -> TagView.from(tag) })
        }
    }
}