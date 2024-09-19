package unit.application.exercises;

import domain.security.PermissionValidator
import application.exercises.ExerciseController
import domain.exercises.base.Exercise
import domain.exercises.base.ExerciseFactory
import domain.exercises.base.ExerciseId
import domain.exercises.base.ExerciseRepository
import domain.security.Role
import domain.utils.PaginatedList
import domain.utils.PaginationParams
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.mockk.verifyOrder
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import utils.domain.exercises.ExerciseFixture
import utils.domain.exercises.ExerciseRequestFixture
import utils.domain.security.RequesterFixture.Companion.anyRequester

class ExerciseControllerTest {

    @Test
    fun `create new exercise`() {
        val expected = ExerciseFixture.anyExercise()
        val request = ExerciseRequestFixture.anyCreateExerciseRequest()
        every { factory.createNew(request, requester.id) }.returns(expected)
        every { repository.add(expected) }.returns(expected)

        val result = controller.create(request, requester)

        verifyOrder {
            permissionValidator.validatePermission(requester, Role.USER)
            factory.createNew(request, requester.id)
            repository.add(expected)
        }
        assertEquals(expected, result)
    }

    @Test
    fun `get exercise by id`() {
        val expected = ExerciseFixture.anyExercise()
        every { repository.get(expected.id) }.returns(expected)

        val result = controller.get(expected.id, requester)

        verifyOrder {
            repository.get(expected.id)
            permissionValidator.validatePermissionToView(requester, expected.accessPolicy)
        }
        assertEquals(expected, result)
    }

    @Test
    fun `updates exercise by id`() {
        val exerciseId = ExerciseId("123")
        val toUpdate = mockk<Exercise>(relaxed = true)
        val request = ExerciseRequestFixture.anyUpdateExerciseRequest()
        every { repository.get(exerciseId) }.returns(toUpdate)

        controller.update(exerciseId, request, requester)

        verifyOrder {
            repository.get(exerciseId)
            permissionValidator.validatePermissionToUpdate(requester, toUpdate.accessPolicy)
            toUpdate.apply(request)
            repository.update(toUpdate)
        }
    }

    @Test
    fun `deletes exercise by id`() {
        val exerciseId = ExerciseId("123")
        val toDelete = mockk<Exercise>(relaxed = true)
        every { repository.get(exerciseId) }.returns(toDelete)

        controller.delete(exerciseId, requester)

        verifyOrder {
            repository.get(exerciseId)
            permissionValidator.validatePermissionToDelete(requester, toDelete.accessPolicy)
            repository.delete(toDelete)
        }
    }

    @Test
    fun `returns exercises filtered by request data and requester`() {
        val request = ExerciseRequestFixture.anySearchExercisesRequest()
        val paginationParams = PaginationParams(10, 0);
        val expected = mockk<PaginatedList<Exercise>>(relaxed = true)
        every { repository.searchBy(any(), any()) } returns expected

        val result = controller.searchExercises(request, paginationParams, requester)

        verify { repository.searchBy(match { filter ->
            filter.searchTerm === request.searchTerm &&
                    filter.tags.containsAll(request.tags) &&
                    filter.requester == requester
        }, paginationParams) }
        assertEquals(expected, result)
    }

    private val requester = anyRequester()
    private val permissionValidator: PermissionValidator = mockk<PermissionValidator>(relaxed = true)
    private val factory: ExerciseFactory = mockk<ExerciseFactory>(relaxed = true)
    private val repository: ExerciseRepository = mockk<ExerciseRepository>(relaxed = true)
    private val controller = ExerciseController(permissionValidator, factory, repository)
}