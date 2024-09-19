package application.exercises

import domain.security.PermissionValidator
import domain.exercises.list.ExercisesList
import domain.exercises.list.ExercisesListFactory
import domain.exercises.list.ExercisesListId
import domain.exercises.list.ExercisesListRepository
import domain.exercises.list.request.CreateExercisesListRequest
import domain.exercises.list.request.UpdateExercisesListRequest
import domain.security.Requester
import domain.security.Role

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

    fun update(id: ExercisesListId, request: UpdateExercisesListRequest, requester: Requester) {
        val exercisesList = repository.get(id)
        permissionValidator.validatePermissionToUpdate(requester, exercisesList.accessPolicy)
        exercisesList.apply(request)
        repository.update(exercisesList)
    }

    fun delete(id: ExercisesListId, requester: Requester) {
        val exercisesList = repository.get(id)
        permissionValidator.validatePermissionToDelete(requester, exercisesList.accessPolicy)
        repository.delete(exercisesList)
    }
}