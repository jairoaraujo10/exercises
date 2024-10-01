package application.exercises

import application.SearchRequest
import domain.exercises.IndexMetadataFilter.Companion.exercisesFilterFor
import domain.exercises.base.ExerciseId
import domain.security.PermissionValidator
import domain.exercises.list.ExercisesList
import domain.exercises.list.ExercisesListFactory
import domain.exercises.list.ExercisesListId
import domain.exercises.list.ExercisesListRepository
import domain.exercises.list.request.CreateExercisesListRequest
import domain.exercises.list.request.UpdateExercisesListRequest
import domain.security.Requester
import domain.security.Role
import domain.utils.PaginatedList
import domain.utils.PaginationParams

class ExercisesListController(
    private val permissionValidator: PermissionValidator,
    private val factory: ExercisesListFactory,
    private val repository: ExercisesListRepository
) {
    fun create(request: CreateExercisesListRequest, requester: Requester): ExercisesList {
        permissionValidator.validatePermission(requester, Role.USER)
        val exercisesList = factory.createNew(request, requester.id)
        return repository.add(exercisesList)
    }

    fun get(id: ExercisesListId, requester: Requester): ExercisesList {
        val exercisesList = repository.get(id)
        permissionValidator.validatePermissionToView(requester, exercisesList.accessPolicy)
        return exercisesList
    }

    fun update(id: ExercisesListId, request: UpdateExercisesListRequest, requester: Requester) {
        val exercisesList = repository.get(id)
        permissionValidator.validatePermissionToUpdate(requester, exercisesList.accessPolicy)
        exercisesList.update(request)
        repository.update(exercisesList)
    }

    fun delete(id: ExercisesListId, requester: Requester) {
        val exercisesList = repository.get(id)
        permissionValidator.validatePermissionToDelete(requester, exercisesList.accessPolicy)
        repository.delete(exercisesList)
    }

    fun addExerciseToList(id: ExercisesListId, exerciseId: ExerciseId, requester: Requester) {
        val exercisesList = repository.get(id)
        permissionValidator.validatePermissionToUpdate(requester, exercisesList.accessPolicy)
        exercisesList.addExercise(exerciseId)
        repository.update(exercisesList)
    }

    fun removeExerciseFromList(id: ExercisesListId, exerciseId: ExerciseId, requester: Requester) {
        val exercisesList = repository.get(id)
        permissionValidator.validatePermissionToUpdate(requester, exercisesList.accessPolicy)
        exercisesList.removeExercise(exerciseId)
        repository.update(exercisesList)
    }

    fun searchExercisesList(
        searchRequest: SearchRequest, pagination: PaginationParams, requester: Requester
    ): PaginatedList<ExercisesList> {
        val filter = exercisesFilterFor(requester)
            .filteredBy(searchRequest.searchTerm)
            .withTags(searchRequest.tags)
        return repository.searchBy(filter, pagination)
    }
}