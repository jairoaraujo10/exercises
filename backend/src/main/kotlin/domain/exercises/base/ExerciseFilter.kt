package domain.exercises.base

import domain.exercises.Tag
import domain.security.Requester

class ExerciseFilter(val requester: Requester){
    var searchTerm: String = ""
    var tags: Set<Tag> = mutableSetOf()

    companion object {
        fun exercisesFilterFor(requester: Requester): ExerciseFilter {
            return ExerciseFilter(requester)
        }
    }

    fun filteredBy(searchTerm: String): ExerciseFilter {
        this.searchTerm = searchTerm
        return this
    }

    fun withTags(tags: Set<Tag>): ExerciseFilter {
        this.tags = this.tags.plus(tags)
        return this
    }
}