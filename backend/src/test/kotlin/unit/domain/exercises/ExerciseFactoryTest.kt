package unit.domain.exercises

import domain.exercises.base.ExerciseFactory
import domain.users.UserId
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import utils.domain.exercises.ExerciseRequestFixture

class ExerciseFactoryTest {

    @Test
    fun `create an exercise with provided data`() {
        val createExerciseRequest = ExerciseRequestFixture.anyCreateExerciseRequest()
        val requesterId = UserId(123L)

        val createdExercise = factory.createNew(createExerciseRequest, requesterId)

        assertEquals(createdExercise.metadata.title, createExerciseRequest.title)
        assertEquals(createdExercise.metadata.tags, createExerciseRequest.tags)
        assertEquals(createdExercise.content.description, createExerciseRequest.description)
        assertEquals(createdExercise.content.possibleAnswers, createExerciseRequest.possibleAnswers)
        assertEquals(createdExercise.content.correctAnswerIndex, createExerciseRequest.correctAnswerIndex)
    }

    private val currentTime: Long = 1234L
    private val factory = ExerciseFactory { currentTime }
}