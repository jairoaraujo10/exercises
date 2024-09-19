package integration.infrastructure.jdbc

import liquibase.integration.spring.SpringLiquibase
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jdbc.datasource.DriverManagerDataSource
import org.testcontainers.containers.MySQLContainer
import org.testcontainers.utility.DockerImageName
import javax.sql.DataSource

@Configuration
open class DataSourceTestContext {
    private var mySQLContainer = MySQLContainer<Nothing>(
        DockerImageName.parse("mysql:8.0")
    ).apply {
        withDatabaseName("exercises")
        withUsername("root")
        withPassword("root")
        start()
    }

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
        dataSource.url = mySQLContainer.jdbcUrl + "?allowPublicKeyRetrieval=true&useSSL=false"
        dataSource.username = mySQLContainer.username
        dataSource.password = mySQLContainer.password
        return dataSource
    }

    @Bean(destroyMethod = "stop")
    open fun stopMySQLContainer(): MySQLContainer<*> {
        return mySQLContainer
    }
}