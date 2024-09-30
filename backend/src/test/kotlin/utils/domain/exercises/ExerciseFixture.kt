package utils.domain.exercises

import domain.exercises.*
import domain.exercises.base.*
import domain.users.UserId

class ExerciseFixture {
    companion object {
        fun anyExercise(): Exercise {
            return Exercise(
                ExerciseId("123"),
                IndexMetadata(
                    "Exercise title",
                    mutableSetOf(
                        Tag("value_1"),
                        Tag("value_2")
                    ),
                    UserId(1L),
                    1234
                ),
                Content(
                    "This is the exercise description",
                    listOf(
                        "Option A",
                        "Option B",
                        "Option C"
                    ),
                    1
                )
            )
        }

        fun anyWith(id: ExerciseId): Exercise {
            return Exercise(id,
                IndexMetadata(
                    "Exercise title",
                    mutableSetOf(
                        Tag("value_1"),
                        Tag("value_2")
                    ),
                    UserId(1L),
                    1234
                ),
                Content(
                    "This is the exercise description",
                    listOf(
                        "Option A",
                        "Option B",
                        "Option C"
                    ),
                    1
                )
            )
        }

        fun otherWith(id: ExerciseId): Exercise {
            return Exercise(id,
                IndexMetadata(
                    "Other exercise title",
                    mutableSetOf(
                        Tag("value_3"),
                        Tag("value_4")
                    ),
                    UserId(1L),
                    1234
                ),
                Content(
                    "This is the other exercise description",
                    listOf(
                        "Other option A",
                        "Other option B",
                        "Other option C"
                    ),
                    2
                )
            )
        }
    }
}
