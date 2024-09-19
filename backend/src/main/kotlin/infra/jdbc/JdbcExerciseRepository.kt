package infra.jdbc

import com.google.gson.Gson
import domain.exercises.Metadata
import domain.exercises.Tag
import domain.exercises.base.*
import domain.users.UserId
import domain.utils.PaginatedList
import domain.utils.PaginationParams
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.PreparedStatementCreator
import org.springframework.jdbc.support.GeneratedKeyHolder
import java.sql.ResultSet
import java.sql.Statement

class JdbcExerciseRepository(
    private val jdbcTemplate: JdbcTemplate,
    private val gson: Gson
) : ExerciseRepository {

    override fun add(exercise: Exercise): Exercise {
        val keyHolder = GeneratedKeyHolder()
        jdbcTemplate.update(addExerciseStatementCreator(exercise), keyHolder)
        val generatedId = keyHolder.key?.toLong() ?: throw IllegalStateException("Failed to retrieve generated key")
        insertTags(exercise.metadata.tags, generatedId.toString())
        return exercise.copy(id = ExerciseId(generatedId.toString()))
    }

    private fun addExerciseStatementCreator(exercise: Exercise) = PreparedStatementCreator { connection ->
        val ps = connection.prepareStatement(
            "INSERT INTO exercises (title, author_id, creation_timestamp, content) VALUES (?, ?, ?, ?)",
            Statement.RETURN_GENERATED_KEYS
        )
        ps.setString(1, exercise.metadata.title)
        ps.setLong(2, exercise.metadata.authorId.value!!)
        ps.setLong(3, exercise.metadata.creationTimestamp)
        ps.setString(4, gson.toJson(exercise.content))
        ps
    }

    private fun insertTags(tags: Set<Tag>, exerciseId: String) {
        val sql = "INSERT INTO exercise_tags (exercise_id, tag_value) VALUES (?, ?)"
        val batchArgs = tags.map { tag ->
            arrayOf<Any>(exerciseId, tag.value)
        }
        jdbcTemplate.batchUpdate(sql, batchArgs)
    }

    override fun get(id: ExerciseId): Exercise {
        return try {
            jdbcTemplate.queryForObject(
                "SELECT * FROM exercises WHERE id = ?",
                exerciseRowMapper(), id.value!!
            )!!
        } catch (e: EmptyResultDataAccessException) {
            throw NoSuchElementException("Exercise not found")
        }
    }

    override fun update(exercise: Exercise) {
        val rowsAffected = jdbcTemplate.update(
            "UPDATE exercises SET title = ?, content = ? WHERE id = ?",
            exercise.metadata.title,
            gson.toJson(exercise.content),
            exercise.id.value!!
        )
        if (rowsAffected == 0) {
            throw NoSuchElementException("Exercise not found")
        }
        updateTags(exercise.metadata.tags, exercise.id.value)
    }

    private fun updateTags(tags: Set<Tag>, exerciseId: String) {
        jdbcTemplate.update("DELETE FROM exercise_tags WHERE exercise_id = ?", exerciseId)
        insertTags(tags, exerciseId)
    }

    override fun delete(exercise: Exercise) {
        jdbcTemplate.update("DELETE FROM exercise_tags WHERE exercise_id = ?", exercise.id.value!!)
        val rowsAffected = jdbcTemplate.update("DELETE FROM exercises WHERE id = ?", exercise.id.value)
        if (rowsAffected == 0) {
            throw NoSuchElementException("Exercise not found")
        }
    }

    override fun searchBy(filter: ExerciseFilter, paginationParams: PaginationParams): PaginatedList<Exercise> {
        val (sql, params) = buildSearchQuery(filter, paginationParams)
        val exercises = jdbcTemplate.query(sql, exerciseRowMapper(), *params.toTypedArray())
        val total = jdbcTemplate.queryForObject(
            "SELECT COUNT(DISTINCT e.id) FROM exercises e ${buildSearchWhereClause(filter)}",
            Long::class.java,
            *params.dropLast(2).toTypedArray()
        ) ?: 0L
        return PaginatedList(exercises, total)
    }

    private fun buildSearchQuery(filter: ExerciseFilter, paginationParams: PaginationParams): Pair<String, List<Any>> {
        val params = mutableListOf<Any>()
        val whereClause = buildSearchWhereClause(filter, params)
        val sql = """
            SELECT DISTINCT e.*
            FROM exercises e
            $whereClause
            LIMIT ? OFFSET ?
        """.trimIndent()
        params.add(paginationParams.limit)
        params.add(paginationParams.offset)
        return Pair(sql, params)
    }

    private fun buildSearchWhereClause(filter: ExerciseFilter, params: MutableList<Any> = mutableListOf()): String {
        val conditions = mutableListOf<String>()
        conditions.add("""
            CONCAT(
                e.title, ' ',
                JSON_UNQUOTE(JSON_EXTRACT(e.content, '$.description'))
            ) LIKE ?
        """.trimIndent())
        params.add("%${filter.searchTerm}%")
        conditions.add("e.author_id = ?")
        params.add(filter.requester.id.value!!)
        if (filter.tags.isNotEmpty()) {
            conditions.add("""
                EXISTS (
                    SELECT 1 FROM exercise_tags et
                    WHERE et.exercise_id = e.id
                    AND et.tag_value IN (${filter.tags.joinToString(",") { "?" }})
                )
            """.trimIndent())
            params.addAll(filter.tags.map { it.value })
        }
        return if (conditions.isNotEmpty()) {
            "WHERE ${conditions.joinToString(" AND ")}"
        } else {
            ""
        }
    }

    override fun getTags(authorId: UserId): Set<Tag> {
        val sql = """
            SELECT DISTINCT et.tag_value
            FROM exercise_tags et
            JOIN exercises e ON et.exercise_id = e.id
            WHERE e.author_id = ?
        """.trimIndent()
        val tags = jdbcTemplate.query(sql, { rs, _ -> Tag(rs.getString("tag_value")) }, authorId.value!!)
        return tags.toSet()
    }

    private fun exerciseRowMapper() = { rs: ResultSet, _: Int ->
        val exerciseId = ExerciseId(rs.getString("id"))
        val metadata = Metadata(
            title = rs.getString("title"),
            tags = getExerciseTags(exerciseId.value!!),
            authorId = UserId(rs.getLong("author_id")),
            creationTimestamp = rs.getLong("creation_timestamp")
        )
        val content = gson.fromJson(rs.getString("content"), Content::class.java)
        Exercise(
            id = exerciseId,
            metadata = metadata,
            content = content
        )
    }

    private fun getExerciseTags(exerciseId: String): Set<Tag> {
        val sql = "SELECT tag_value FROM exercise_tags WHERE exercise_id = ?"
        val tags = jdbcTemplate.query(sql, { rs, _ -> Tag(rs.getString("tag_value")) }, exerciseId)
        return tags.toSet()
    }
}