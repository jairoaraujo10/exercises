package infra.contexts

import application.users.UserController
import com.google.gson.Gson
import configuration.FirstAdminUserConfig
import domain.security.PermissionValidator
import domain.security.Role
import domain.users.DuplicatedUserException
import domain.users.UserFactory
import domain.utils.HashGenerator
import infra.jdbc.JdbcUserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.event.ContextRefreshedEvent
import org.springframework.context.event.EventListener
import org.springframework.jdbc.core.JdbcTemplate

@Configuration
open class UserContext {
    @Autowired
    lateinit var jdbcTemplate: JdbcTemplate
    @Autowired
    lateinit var gson: Gson
    @Autowired
    lateinit var hashGenerator: HashGenerator
    @Autowired
    lateinit var permissionValidator: PermissionValidator

    @Bean
    open fun userRepository() = JdbcUserRepository(jdbcTemplate, gson, hashGenerator)

    @Bean
    open fun userFactory() = UserFactory(hashGenerator)

    @Bean
    open fun userController() = UserController(
        permissionValidator, userFactory(), userRepository()
    )

    @EventListener(ContextRefreshedEvent::class)
    fun setAdminUser(event: ContextRefreshedEvent) {
        val admin = userFactory().createBasicUserWith(FirstAdminUserConfig.username(), FirstAdminUserConfig.email())
        admin.roles.add(Role.ADMIN)
        admin.hashedPassword.update(FirstAdminUserConfig.password())
        try {
            userRepository().add(admin)
        } catch (e: DuplicatedUserException) {
            // Ignore if the user already exists
        }
    }
}