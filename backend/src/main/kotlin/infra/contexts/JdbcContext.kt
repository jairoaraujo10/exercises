package infra.contexts

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jdbc.core.JdbcTemplate
import javax.sql.DataSource

@Configuration
open class JdbcContext {
    @Autowired
    lateinit var dataSource: DataSource
    @Bean
    open fun jdbcTemplate(): JdbcTemplate = JdbcTemplate(dataSource)
}