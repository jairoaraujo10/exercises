package infra.api.routes.response

import domain.exercises.IndexMetadata

data class IndexMetadataView(val id: String?, val title: String, val tags: List<TagView>) {
    companion object {
        fun from(id: String, metadata: IndexMetadata): IndexMetadataView {
            return IndexMetadataView(id, metadata.title, metadata.tags.map { tag -> TagView.from(tag) })
        }
    }
}