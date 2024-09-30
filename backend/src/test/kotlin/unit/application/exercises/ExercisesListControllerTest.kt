package unit.application.exercises

import domain.security.PermissionValidator
import application.exercises.ExercisesListController
import domain.exercises.list.ExercisesList
import domain.exercises.list.ExercisesListFactory
import domain.exercises.list.ExercisesListId
import domain.exercises.list.ExercisesListRepository
import domain.security.Role
import domain.utils.PaginatedList
import domain.utils.PaginationParams
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.mockk.verifyOrder
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import utils.domain.exercises.ExercisesListFixture
import utils.domain.exercises.ExercisesListRequestFixture
import utils.domain.security.RequesterFixture.Companion.anyRequester

class ExercisesListControllerTest {

    @Test
    fun `create new exercises list`() {
        val expected = ExercisesListFixture.anyExercisesList()
        val request = ExercisesListRequestFixture.anyCreateExercisesListRequest()
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
    fun `get exercises list by id`() {
        val expected = ExercisesListFixture.anyExercisesList()
        every { repository.get(expected.id) }.returns(expected)

        val result = controller.get(expected.id, requester)

        verifyOrder {
            repository.get(expected.id)
            permissionValidator.validatePermissionToView(requester, expected.accessPolicy)
        }
        assertEquals(expected, result)
    }

    @Test
    fun `updates exercises list by id`() {
        val listId = ExercisesListId("123")
        val toUpdate = mockk<ExercisesList>(relaxed = true)
        val request = ExercisesListRequestFixture.anyUpdateExercisesListRequest()
        every { repository.get(listId) }.returns(toUpdate)

        controller.update(listId, request, requester)

        verifyOrder {
            repository.get(listId)
            permissionValidator.validatePermissionToUpdate(requester, toUpdate.accessPolicy)
            toUpdate.update(request)
            repository.update(toUpdate)
        }
    }

    @Test
    fun `deletes exercises list by id`() {
        val listId = ExercisesListId("123")
        val toDelete = mockk<ExercisesList>(relaxed = true)
        every { repository.get(listId) }.returns(toDelete)

        controller.delete(listId, requester)

        verifyOrder {
            repository.get(listId)
            permissionValidator.validatePermissionToDelete(requester, toDelete.accessPolicy)
            repository.delete(toDelete)
        }
    }

    @Test
    fun `returns exercises lists filtered by request data and requester`() {
        val request = ExercisesListRequestFixture.anySearchExercisesListRequest()
        val paginationParams = PaginationParams(10, 0);
        val expected = mockk<PaginatedList<ExercisesList>>(relaxed = true)
        every { repository.searchBy(any(), any()) } returns expected

        val result = controller.searchExercisesList(request, paginationParams, requester)

        verify { repository.searchBy(match { filter ->
            filter.searchTerm === request.searchTerm &&
                    filter.tags.containsAll(request.tags) &&
                    filter.requester == requester
        }, paginationParams) }
        assertEquals(expected, result)
    }

    private val requester = anyRequester()
    private val permissionValidator: PermissionValidator = mockk<PermissionValidator>(relaxed = true)
    private val factory: ExercisesListFactory = mockk<ExercisesListFactory>(relaxed = true)
    private val repository: ExercisesListRepository = mockk<ExercisesListRepository>(relaxed = true)
    private val controller = ExercisesListController(permissionValidator, factory, repository)
}
