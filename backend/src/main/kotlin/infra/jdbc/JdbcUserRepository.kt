package infra.jdbc

import com.google.gson.Gson
import domain.security.Role
import domain.users.*
import domain.utils.HashGenerator
import domain.utils.PaginatedList
import domain.utils.PaginationParams
import org.springframework.dao.DuplicateKeyException
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.PreparedStatementCreator
import org.springframework.jdbc.support.GeneratedKeyHolder
import java.sql.ResultSet
import java.sql.Statement

class JdbcUserRepository(private val jdbcTemplate: JdbcTemplate, private val gson: Gson,
                         private val hashGenerator: HashGenerator) : UserRepository {
    override fun getUserById(id: UserId): User {
        try {
            return jdbcTemplate.queryForObject("SELECT * FROM users WHERE id = ?",
                userRowMapper(), id.toString())!!
        } catch (e: EmptyResultDataAccessException) {
            throw NoSuchElementException("User not found")
        }
    }

    override fun getUserByEmail(email: Email): User? {
        return try {
            jdbcTemplate.queryForObject("SELECT * FROM users WHERE email = ?",
                userRowMapper(), email.toString())
        } catch (e: EmptyResultDataAccessException) {
            null
        }
    }

    override fun add(user: User): User {
        val keyHolder = GeneratedKeyHolder()
        try{
            jdbcTemplate.update(addUserStatementCreator(user), keyHolder)
        } catch (e: DuplicateKeyException) {
            throw DuplicatedUserException("User already exists")
        }
        val generatedId = keyHolder.key?.toLong() ?: throw IllegalStateException("Failed to retrieve generated key")
        return user.initialize(UserId(generatedId))
    }

    private fun addUserStatementCreator(user: User) = PreparedStatementCreator { connection ->
        val ps = connection.prepareStatement(
            "INSERT INTO users (name, email, password, roles) VALUES (?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS
        )
        ps.setString(1, user.name)
        ps.setString(2, user.email.toString())
        ps.setString(3, user.hashedPassword.toString())
        ps.setString(4, gson.toJson(user.roles))
        ps
    }

    override fun delete(user: User) {
        jdbcTemplate.update("DELETE FROM users WHERE id = ?", user.id.toString())
    }

    override fun update(user: User) {
        try {
            val rowsAffected = jdbcTemplate.update(
                "UPDATE users SET name = ?, email = ?, password = ?, roles = ? WHERE id = ?",
                user.name,
                user.email.toString(),
                user.hashedPassword.toString(),
                gson.toJson(user.roles),
                user.id.value!!
            )
            if (rowsAffected == 0) {
                throw NoSuchElementException("User not found")
            }
        } catch (e: DuplicateKeyException) {
            throw DuplicatedUserException("Email already exists")
        }
    }

    override fun listUsers(filter: String, paginationParams: PaginationParams): PaginatedList<User> {
        val users = jdbcTemplate.query("SELECT * FROM users WHERE name LIKE ? LIMIT ? OFFSET ?",
            userRowMapper(), "%${filter}%", paginationParams.limit, paginationParams.offset)
        val total = jdbcTemplate.queryForObject("SELECT count(*) FROM users WHERE name like ?",
            Long::class.java, "%${filter}%")
        return PaginatedList(users, total)
    }

    private fun userRowMapper() = { rs: ResultSet, _: Int ->
        User(
            UserId(rs.getLong("id")),
            rs.getString("name"), Email(rs.getString("email")),
            HashedPassword(rs.getString("password"), hashGenerator),
            gson.fromJson(rs.getString("roles"), Array<Role>::class.java).toMutableSet()
        )
    }
}