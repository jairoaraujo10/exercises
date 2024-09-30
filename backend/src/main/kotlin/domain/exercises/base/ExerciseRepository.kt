package domain.exercises.base

import domain.exercises.IndexMetadataFilter
import domain.exercises.Tag
import domain.users.UserId
import domain.utils.PaginatedList
import domain.utils.PaginationParams

interface ExerciseRepository {
    fun add(exercise: Exercise): Exercise
    fun get(id: ExerciseId): Exercise
    fun update(exercise: Exercise)
    fun delete(exercise: Exercise)
    fun searchBy(filter: IndexMetadataFilter, paginationParams: PaginationParams): PaginatedList<Exercise>
    fun getTags(authorId: UserId): Set<Tag>
}