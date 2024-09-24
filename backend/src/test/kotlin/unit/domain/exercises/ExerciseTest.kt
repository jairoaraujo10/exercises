package unit.domain.exercises

import domain.exercises.Tag
import domain.exercises.base.Exercise
import domain.exercises.base.ExerciseId
import domain.exercises.base.request.UpdateExerciseRequest
import org.junit.Assert.assertEquals
import org.junit.Test
import utils.domain.exercises.ExerciseFixture.Companion.anyWith

class ExerciseTest {
    private val exercise: Exercise = anyWith(ExerciseId("exercise123"))

    @Test
    fun `should update title and tags when provided in update request`() {
        val updateRequest = UpdateExerciseRequest(
            title = "Updated Title",
            tags = setOf(Tag("tag3"))
        )

        exercise.update(updateRequest)

        assertEquals("Updated Title", exercise.metadata.title)
        assertEquals(setOf(Tag("tag3")), exercise.metadata.tags)
    }

    @Test
    fun `should update content fields when provided in update request`() {
        val updateRequest = UpdateExerciseRequest(
            description = "Updated description",
            answers = listOf("X", "Y", "Z"),
            correctAnswerIndex = 2
        )

        exercise.update(updateRequest)

        assertEquals("Updated description", exercise.content.description)
        assertEquals(listOf("X", "Y", "Z"), exercise.content.possibleAnswers)
        assertEquals(2, exercise.content.correctAnswerIndex)
    }

    @Test
    fun `should not update metadata and content fields when update request contains nulls`() {
        val updateRequest = UpdateExerciseRequest()

        exercise.update(updateRequest)

        assertEquals("Exercise title", exercise.metadata.title)
        assertEquals(setOf(Tag("value_1"), Tag("value_2")), exercise.metadata.tags)
        assertEquals("This is the exercise description", exercise.content.description)
        assertEquals(listOf("Option A", "Option B", "Option C"), exercise.content.possibleAnswers)
        assertEquals(1, exercise.content.correctAnswerIndex)
    }

    @Test
    fun `should partially update metadata and content when only some fields are provided`() {
        val updateRequest = UpdateExerciseRequest(
            title = "Partially Updated Title",
            answers = listOf("Option D", "Option E"),
            correctAnswerIndex = 0
        )

        exercise.update(updateRequest)

        assertEquals("Partially Updated Title", exercise.metadata.title)
        assertEquals(setOf(Tag("value_1"), Tag("value_2")), exercise.metadata.tags)
        assertEquals("This is the exercise description", exercise.content.description)
        assertEquals(listOf("Option D", "Option E"), exercise.content.possibleAnswers)
        assertEquals(0, exercise.content.correctAnswerIndex)
    }

    @Test
    fun `should apply all updates correctly`() {
        val updateRequest = UpdateExerciseRequest(
            title = "Completely Updated Title",
            description = "Completely Updated description",
            answers = listOf("New1", "New2", "New3"),
            correctAnswerIndex = 1,
            tags = setOf(Tag("newTag"))
        )

        exercise.update(updateRequest)

        assertEquals("Completely Updated Title", exercise.metadata.title)
        assertEquals(setOf(Tag("newTag")), exercise.metadata.tags)
        assertEquals("Completely Updated description", exercise.content.description)
        assertEquals(listOf("New1", "New2", "New3"), exercise.content.possibleAnswers)
        assertEquals(1, exercise.content.correctAnswerIndex)
    }
}