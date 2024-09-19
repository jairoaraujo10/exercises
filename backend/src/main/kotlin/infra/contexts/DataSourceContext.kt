package infra.contexts

import configuration.DatabaseConfig
import liquibase.integration.spring.SpringLiquibase
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jdbc.datasource.DriverManagerDataSource
import javax.sql.DataSource

@Configuration
open class DataSourceContext {
    @Bean
    open fun springLiquibase(): SpringLiquibase {
        val springLiquibase = SpringLiquibase()
        springLiquibase.dataSource = dataSource()
        springLiquibase.changeLog = "classpath:db/changelog.yml"
        springLiquibase.isDropFirst = false
        return springLiquibase
    }

    @Bean
    open fun dataSource(): DataSource {
        val dataSource = DriverManagerDataSource()
        dataSource.setDriverClassName("com.mysql.jdbc.Driver")
        dataSource.url = DatabaseConfig.dbUrl()
        dataSource.schema = DatabaseConfig.dbSchema()
        dataSource.username = DatabaseConfig.dbUsername()
        dataSource.password = DatabaseConfig.dbPassword()
        return dataSource
    }
}