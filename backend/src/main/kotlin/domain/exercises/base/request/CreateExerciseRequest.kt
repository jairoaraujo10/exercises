package domain.exercises.base.request

import domain.exercises.Tag

data class CreateExerciseRequest(
    val title: String,
    val tags: Set<Tag>,
    val description: String,
    val possibleAnswers: List<String>,
    val correctAnswerIndex: Int
)