package utils.domain.exercises

import application.SearchRequest
import domain.exercises.Tag
import domain.exercises.list.request.CreateExercisesListRequest
import domain.exercises.list.request.UpdateExercisesListRequest

class ExercisesListRequestFixture {
    companion object {
        fun anyCreateExercisesListRequest(): CreateExercisesListRequest = CreateExercisesListRequest(
            title = "Sample Title",
            tags = setOf(Tag("Math"), Tag("Algebra"))
        )

        fun anyUpdateExercisesListRequest(): UpdateExercisesListRequest = UpdateExercisesListRequest()

        fun anySearchExercisesListRequest(): SearchRequest = SearchRequest(
            "search term", setOf(
                Tag("Math"),
                Tag("Algebra")
            )
        )
    }
}