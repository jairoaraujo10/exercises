package infra.contexts

import domain.security.PermissionValidator
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class AuthorizationContext {
    @Bean
    open fun permissionValidator() = PermissionValidator()
}