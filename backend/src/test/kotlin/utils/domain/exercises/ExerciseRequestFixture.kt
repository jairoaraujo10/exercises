package utils.domain.exercises

import domain.exercises.Tag
import domain.exercises.base.request.CreateExerciseRequest
import application.SearchRequest
import domain.exercises.base.request.UpdateExerciseRequest

class ExerciseRequestFixture {
    companion object {
        fun anyCreateExerciseRequest(): CreateExerciseRequest = CreateExerciseRequest(
            title = "Sample Title",
            tags = setOf(Tag("Math"), Tag("Algebra")),
            description = "Sample Description",
            possibleAnswers = listOf("Answer1", "Answer2"),
            correctAnswerIndex = 1
        )

        fun anyUpdateExerciseRequest(): UpdateExerciseRequest = UpdateExerciseRequest()

        fun anySearchExercisesRequest(): SearchRequest = SearchRequest(
            "search term", setOf(
                Tag("Math"),
                Tag("Algebra")
            )
        )
    }
}