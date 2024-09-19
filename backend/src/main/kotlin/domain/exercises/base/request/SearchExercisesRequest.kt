package domain.exercises.base.request

import domain.exercises.Tag

data class SearchExercisesRequest(
    val searchTerm: String = "",
    val tags: Set<Tag> = emptySet()
)