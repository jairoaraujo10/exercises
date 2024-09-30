package domain.exercises

import domain.security.Requester

class IndexMetadataFilter(val requester: Requester){
    var searchTerm: String = ""
    var tags: Set<Tag> = mutableSetOf()

    companion object {
        fun exercisesFilterFor(requester: Requester): IndexMetadataFilter {
            return IndexMetadataFilter(requester)
        }
    }

    fun filteredBy(searchTerm: String): IndexMetadataFilter {
        this.searchTerm = searchTerm
        return this
    }

    fun withTags(tags: Set<Tag>): IndexMetadataFilter {
        this.tags = this.tags.plus(tags)
        return this
    }
}