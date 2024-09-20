package integration.infrastructure.jdbc

import com.google.gson.Gson
import domain.exercises.Tag
import domain.exercises.base.Exercise
import domain.exercises.base.ExerciseFilter
import domain.exercises.base.ExerciseId
import domain.exercises.base.ExerciseRepository
import domain.users.UserId
import domain.utils.PaginationParams
import infra.contexts.JdbcContext
import infra.jdbc.JdbcExerciseRepository
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.dao.DuplicateKeyException
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension
import utils.domain.exercises.ExerciseFixture
import utils.domain.security.RequesterFixture
import utils.domain.security.RequesterFixture.Companion.anyRequester

@ExtendWith(SpringExtension::class)
@ContextConfiguration(classes = [DataSourceTestContext::class, JdbcContext::class])
class JdbcExerciseRepositoryTest {

    @Autowired
    private lateinit var jdbcTemplate: JdbcTemplate
    private lateinit var repository: ExerciseRepository

    @BeforeEach
    fun setup() {
        jdbcTemplate.execute("DELETE FROM exercise_tags")
        jdbcTemplate.execute("DELETE FROM exercises")
        repository = JdbcExerciseRepository(jdbcTemplate, Gson())
    }

    @Test
    fun `add inserts exercise when it does not exist`() {
        val exercise = ExerciseFixture.anyExercise().copy(id = ExerciseId(null))

        val result = repository.add(exercise)

        assertNotNull(result.id)
        assertEquals(exercise.metadata.title, result.metadata.title)
        assertEquals(exercise.metadata.authorId, result.metadata.authorId)
        assertEquals(exercise.metadata.tags, result.metadata.tags)
        assertEquals(exercise.content, result.content)

        val insertedExercise = repository.get(result.id)
        assertEquals(result.copy(id = insertedExercise.id), insertedExercise)
    }

    @Test
    fun `get returns exercise when it exists`() {
        val exercise = ExerciseFixture.anyWith(ExerciseId("1"))
        insertExerciseIntoDatabase(exercise)

        val result = repository.get(exercise.id)

        assertNotNull(result)
        assertEquals(exercise, result)
    }

    @Test
    fun `get throws exception when exercise does not exist`() {
        val exerciseId = ExerciseId("9999")

        val exception = assertThrows<NoSuchElementException> {
            repository.get(exerciseId)
        }
        assertEquals("Exercise not found", exception.message)
    }

    @Test
    fun `update modifies existing exercise`() {
        val originalExercise = ExerciseFixture.anyWith(ExerciseId("1"))
        insertExerciseIntoDatabase(originalExercise)

        val updatedExercise = ExerciseFixture.otherWith(ExerciseId("1"))

        repository.update(updatedExercise)

        val result = repository.get(originalExercise.id)
        assertEquals(updatedExercise, result)
    }

    @Test
    fun `update throws exception when exercise does not exist`() {
        val nonExistingExercise = ExerciseFixture.anyWith(ExerciseId("9999"))

        val exception = assertThrows<NoSuchElementException> {
            repository.update(nonExistingExercise)
        }
        assertEquals("Exercise not found", exception.message)
    }

    @Test
    fun `delete removes exercise when it exists`() {
        val exercise = ExerciseFixture.anyWith(ExerciseId("1"))
        insertExerciseIntoDatabase(exercise)

        repository.delete(exercise)

        val exception = assertThrows<NoSuchElementException> {
            repository.get(exercise.id)
        }
        assertEquals("Exercise not found", exception.message)
    }

    @Test
    fun `delete throws no such element exception when exercise does not exist`() {
        val nonExistingExercise = ExerciseFixture.anyWith(ExerciseId("9999"))

        val exception = assertThrows<NoSuchElementException> {
            repository.delete(nonExistingExercise)
        }
        assertEquals("Exercise not found", exception.message)
    }

    @Test
    fun `searchBy returns exercises matching filter and pagination`() {
        val exercise1 = ExerciseFixture.anyWith(ExerciseId("1"))
        val exercise2 = ExerciseFixture.otherWith(ExerciseId("2"))
        val exercise3 = ExerciseFixture.anyWith(ExerciseId("3")).copy(
            metadata = ExerciseFixture.anyExercise().metadata.copy(
                title = "Different Title",
                tags = mutableSetOf(Tag("value_5")),
                authorId = UserId(2L)
            )
        )
        insertExerciseIntoDatabase(exercise1)
        insertExerciseIntoDatabase(exercise2)
        insertExerciseIntoDatabase(exercise3)
        val filter = ExerciseFilter(RequesterFixture.builder().withUserId(UserId(1L)).build())
            .filteredBy("Exercise")
            .withTags(setOf(Tag("value_1"), Tag("value_3")))
        val paginationParams = PaginationParams(limit = 2, offset = 0)

        val result = repository.searchBy(filter, paginationParams)

        assertEquals(2, result.items.size)
        assertEquals(2, result.total)
        val expectedExercises = listOf(exercise1, exercise2)
        assertEquals(expectedExercises, result.items)
    }

    @Test
    fun `searchBy returns empty list when no exercises match filter`() {
        val filter = ExerciseFilter(anyRequester())
            .filteredBy("Non-matching")
            .withTags(emptySet())
        val paginationParams = PaginationParams(limit = 10, offset = 0)

        val result = repository.searchBy(filter, paginationParams)

        assertTrue(result.items.isEmpty())
        assertEquals(0, result.total)
    }

    @Test
    fun `getTags returns tags used by author`() {
        val exercise1 = ExerciseFixture.anyWith(ExerciseId("1"))
        val exercise2 = ExerciseFixture.otherWith(ExerciseId("2"))
        val exercise3 = ExerciseFixture.anyWith(ExerciseId("3")).copy(
            metadata = ExerciseFixture.anyExercise().metadata.copy(
                tags = mutableSetOf(Tag("value_5")),
                authorId = UserId(2L)
            )
        )
        insertExerciseIntoDatabase(exercise1)
        insertExerciseIntoDatabase(exercise2)
        insertExerciseIntoDatabase(exercise3)

        val tags = repository.getTags(UserId(1L))

        val expectedTags = setOf(Tag("value_1"), Tag("value_2"), Tag("value_3"), Tag("value_4"))
        assertEquals(expectedTags, tags)
    }

    private fun insertExerciseIntoDatabase(exercise: Exercise) {
        insertAuthor(exercise)
        insertExercise(exercise)
        insertExerciseTags(exercise)
    }

    private fun insertAuthor(exercise: Exercise) {
        try {
            jdbcTemplate.update(
                "INSERT INTO users (id, name, email, password, roles) VALUES (?, ?, ?, ?, ?)",
                exercise.metadata.authorId.value, "test user",
                exercise.metadata.authorId.value.toString() + "test@mail.com", "psw", "[]"
            )
        } catch (e: DuplicateKeyException) {
//            do nothing
        }
    }

    private fun insertExercise(exercise: Exercise) {
        jdbcTemplate.update(
            "INSERT INTO exercises (id, title, author_id, creation_timestamp, content) VALUES (?, ?, ?, ?, ?)",
            exercise.id.value?.toLong(),
            exercise.metadata.title,
            exercise.metadata.authorId.value!!,
            exercise.metadata.creationTimestamp,
            Gson().toJson(exercise.content)
        )
    }

    private fun insertExerciseTags(exercise: Exercise) {
        val sql = "INSERT INTO exercise_tags (exercise_id, tag_value) VALUES (?, ?)"
        exercise.metadata.tags.forEach { tag ->
            jdbcTemplate.update(sql, exercise.id.value?.toLong(), tag.value)
        }
    }
}