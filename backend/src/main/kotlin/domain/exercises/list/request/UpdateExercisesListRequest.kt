package domain.exercises.list.request

import domain.exercises.Tag

data class UpdateExercisesListRequest(
    val title: String? = null,
    val tags: Set<Tag>? = null
)