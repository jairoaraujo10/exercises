package application

import domain.exercises.Tag

data class SearchRequest(
    val searchTerm: String = "",
    val tags: Set<Tag> = emptySet()
)