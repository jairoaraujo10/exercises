package utils.domain.exercises

import domain.exercises.IndexMetadata
import domain.exercises.Tag
import domain.exercises.list.ExercisesList
import domain.exercises.list.ExercisesListId
import domain.users.UserId

class ExercisesListFixture {
    companion object {
        fun anyExercisesList(): ExercisesList {
            return ExercisesList(
                id = ExercisesListId("123"),
                metadata = IndexMetadata(
                    "Exercise title",
                    mutableSetOf(
                        Tag("value_1"),
                        Tag("value_2")
                    ),
                    UserId(1L),
                    1234
                ),
                exercises = arrayListOf()
            )
        }
    }
}