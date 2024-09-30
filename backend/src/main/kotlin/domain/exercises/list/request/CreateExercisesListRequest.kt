package domain.exercises.list.request

import domain.exercises.Tag

data class CreateExercisesListRequest(
    val title: String,
    val tags: Set<Tag>,
)