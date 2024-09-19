package integration.infrastructure.jdbc

import domain.exercises.list.ExercisesListId
import domain.exercises.list.ExercisesListRepository
import infra.contexts.JdbcContext
import infra.jdbc.JdbcExercisesListRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension
import kotlin.test.assertNotNull

@ExtendWith(SpringExtension::class)
@ContextConfiguration(classes = [DataSourceTestContext::class, JdbcContext::class])
class JdbcExercisesListRepositoryTest {
    @Autowired
    private lateinit var jdbcTemplate: JdbcTemplate
    private lateinit var repository: ExercisesListRepository

    @BeforeEach
    fun setup() {
        repository = JdbcExercisesListRepository(jdbcTemplate)
    }

    @Test
    fun test() {
        val result = repository.get(ExercisesListId("1"))

        assertNotNull(result)
    }
}