package domain.exercises.base.request;

import domain.exercises.Tag

data class UpdateExerciseRequest(
    val title: String? = null,
    val description: String? = null,
    val answers: List<String>? = null,
    val correctAnswerIndex: Int? = null,
    val tags: Set<Tag>? = null
)