package infra.jdbc

import domain.exercises.list.ExercisesList
import domain.exercises.list.ExercisesListId
import domain.exercises.list.ExercisesListRepository
import org.springframework.jdbc.core.JdbcTemplate

class JdbcExercisesListRepository(jdbcTemplate: JdbcTemplate) : ExercisesListRepository {
    override fun add(exercisesList: ExercisesList): ExercisesList {
        TODO("Not yet implemented")
    }

    override fun get(id: ExercisesListId): ExercisesList {
        TODO("Not yet implemented")
    }

    override fun update(exercisesList: ExercisesList) {
        TODO("Not yet implemented")
    }

    override fun delete(exercisesList: ExercisesList) {
        TODO("Not yet implemented")
    }
}