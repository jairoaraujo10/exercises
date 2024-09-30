package domain.exercises.list

import domain.exercises.IndexMetadataFilter
import domain.utils.PaginatedList
import domain.utils.PaginationParams

interface ExercisesListRepository {
    fun add(exercisesList: ExercisesList): ExercisesList
    fun get(id: ExercisesListId): ExercisesList
    fun update(exercisesList: ExercisesList)
    fun delete(exercisesList: ExercisesList)
    fun searchBy(filter: IndexMetadataFilter, pagination: PaginationParams): PaginatedList<ExercisesList>
}
