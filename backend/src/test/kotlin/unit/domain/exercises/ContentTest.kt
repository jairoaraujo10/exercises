package unit.domain.exercises

import domain.exercises.base.Content
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class ContentTest {

    @Test
    fun `valid content should be created successfully`() {
        val description = "What is the capital of France?"
        val possibleAnswers = listOf("Paris", "London", "Berlin", "Madrid")
        val correctAnswerIndex = 0

        val content = Content(description, possibleAnswers, correctAnswerIndex)

        assertEquals(description, content.description)
        assertEquals(possibleAnswers, content.possibleAnswers)
        assertEquals(correctAnswerIndex, content.correctAnswerIndex)
    }

    @Test
    fun `description should not be blank or whitespace`() {
        val exception = assertThrows<IllegalArgumentException> {
            Content(" ", listOf("Paris", "London"), 0)
        }
        assertEquals("Description cannot be blank or whitespace.", exception.message)
    }

    @Test
    fun `should have at least two possible answers`() {
        val exception = assertThrows<IllegalArgumentException> {
            Content("What is the capital of France?", listOf("Paris"), 0)
        }
        assertEquals("There must be at least two possible answers.", exception.message)
    }

    @Test
    fun `answer text cannot be blank or whitespace`() {
        val exception = assertThrows<IllegalArgumentException> {
            Content("What is the capital of France?", listOf(" ", "London"), 0)
        }
        assertEquals("Answer text cannot be blank or whitespace.", exception.message)
    }

    @Test
    fun `possible answers must not contain duplicates`() {
        val exception = assertThrows<IllegalArgumentException> {
            Content("What is the capital of France?", listOf("Paris", "Paris"), 0)
        }
        assertEquals("Possible answers must not contain duplicates.", exception.message)
    }

    @Test
    fun `correct answer index must be present in possible answers`() {
        val exception = assertThrows<IllegalArgumentException> {
            Content("What is the capital of France?", listOf("Paris", "London"), 2)
        }
        assertEquals("Correct answer id must be present in possibleAnswers.", exception.message)
    }
}