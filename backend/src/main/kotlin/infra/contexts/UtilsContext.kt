package infra.contexts

import com.google.gson.Gson
import domain.utils.TimeProvider
import infra.hash.Argon2HashGenerator
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class UtilsContext {
    @Bean
    open fun gson() = Gson()
    @Bean
    open fun hashGenerator() = Argon2HashGenerator()
    @Bean
    open fun timeProvider() = TimeProvider { System.currentTimeMillis() }
}