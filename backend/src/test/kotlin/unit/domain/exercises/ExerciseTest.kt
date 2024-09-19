package unit.domain.exercises

import domain.exercises.Tag
import domain.exercises.base.ExerciseId
import domain.exercises.base.request.UpdateExerciseRequest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import utils.domain.exercises.ExerciseFixture.Companion.anyWith

class ExerciseTest {
    @Test
    fun `apply should update metadata and content`() {
        val exercise = anyWith(ExerciseId("exercise123"))
        val updateRequest = UpdateExerciseRequest(
            title = "Updated Title",
            tags = setOf(Tag("Programming")),
            description = "Updated Description",
            answers = listOf("Answer1", "Answer2", "Answer3"),
            correctAnswerIndex = 2
        )

        exercise.apply(updateRequest)

        assertEquals("Updated Title", exercise.metadata.title)
        assertEquals(setOf(Tag("Programming")), exercise.metadata.tags)
        assertEquals("Updated Description", exercise.content.description)
        assertEquals(listOf("Answer1", "Answer2", "Answer3"), exercise.content.possibleAnswers)
        assertEquals(2, exercise.content.correctAnswerIndex)
    }
}