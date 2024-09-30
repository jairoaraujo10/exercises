package application.exercises;

import domain.security.PermissionValidator
import domain.exercises.base.Exercise
import domain.exercises.base.ExerciseFactory
import domain.exercises.IndexMetadataFilter.Companion.exercisesFilterFor
import domain.exercises.base.ExerciseId
import domain.exercises.base.ExerciseRepository
import domain.exercises.base.request.CreateExerciseRequest
import application.SearchRequest
import domain.exercises.base.request.UpdateExerciseRequest
import domain.security.Requester
import domain.security.Role
import domain.utils.PaginatedList
import domain.utils.PaginationParams

class ExerciseController(
    private val permissionValidator: PermissionValidator,
    private val factory: ExerciseFactory,
    private val repository: ExerciseRepository
) {
    fun create(request: CreateExerciseRequest, requester: Requester): Exercise {
        permissionValidator.validatePermission(requester, Role.USER)
        val exercise = factory.createNew(request, requester.id)
        return repository.add(exercise)
    }

    fun get(id: ExerciseId, requester: Requester): Exercise {
        val exercise = repository.get(id)
        permissionValidator.validatePermissionToView(requester, exercise.accessPolicy)
        return exercise
    }

    fun update(id: ExerciseId, request: UpdateExerciseRequest, requester: Requester) {
        val exercise = repository.get(id)
        permissionValidator.validatePermissionToUpdate(requester, exercise.accessPolicy)
        exercise.update(request)
        repository.update(exercise)
    }

    fun delete(id: ExerciseId, requester: Requester) {
        val exercise = repository.get(id)
        permissionValidator.validatePermissionToDelete(requester, exercise.accessPolicy)
        repository.delete(exercise)
    }

    fun searchExercises(
        searchRequest: SearchRequest, pagination: PaginationParams, requester: Requester
    ): PaginatedList<Exercise> {
        val filter = exercisesFilterFor(requester)
            .filteredBy(searchRequest.searchTerm)
            .withTags(searchRequest.tags)
        return repository.searchBy(filter, pagination)
    }
}