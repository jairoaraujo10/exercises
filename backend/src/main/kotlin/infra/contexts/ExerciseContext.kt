package infra.contexts

import application.exercises.ExerciseController
import com.google.gson.Gson
import domain.exercises.base.ExerciseFactory
import domain.security.PermissionValidator
import domain.utils.TimeProvider
import infra.jdbc.JdbcExerciseRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jdbc.core.JdbcTemplate

@Configuration
open class ExerciseContext {
    @Autowired
    lateinit var timeProvider: TimeProvider
    @Autowired
    lateinit var jdbcTemplate: JdbcTemplate
    @Autowired
    lateinit var gson: Gson
    @Autowired
    lateinit var permissionValidator: PermissionValidator

    @Bean
    open fun exerciseFactory() = ExerciseFactory(timeProvider)

    @Bean
    open fun exercisesRepository() = JdbcExerciseRepository(jdbcTemplate, gson)

    @Bean
    open fun exerciseController() = ExerciseController(
        permissionValidator, exerciseFactory(), exercisesRepository()
    )
}