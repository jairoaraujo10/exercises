package infra.contexts

import domain.security.PermissionValidator
import application.exercises.ExercisesListController
import domain.exercises.list.ExercisesListFactory
import domain.utils.TimeProvider
import infra.jdbc.JdbcExercisesListRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jdbc.core.JdbcTemplate

@Configuration
open class ExercisesListContext {
    @Autowired
    lateinit var timeProvider: TimeProvider
    @Autowired
    lateinit var jdbcTemplate: JdbcTemplate
    @Autowired
    lateinit var permissionValidator: PermissionValidator

    @Bean
    open fun exercisesListFactory() = ExercisesListFactory(timeProvider)

    @Bean
    open fun exercisesListRepository() = JdbcExercisesListRepository(jdbcTemplate)

    @Bean
    open fun exercisesListController() = ExercisesListController(
        permissionValidator, exercisesListFactory(), exercisesListRepository()
    )
}