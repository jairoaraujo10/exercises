package infra.contexts

import application.auth.AuthController
import application.exercises.ExerciseController
import application.exercises.ExercisesListController
import application.users.UserController
import com.google.gson.Gson
import infra.api.AuthFilter
import infra.api.routes.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class RoutesContext {
    @Autowired
    lateinit var gson: Gson
    @Autowired
    lateinit var authFilter: AuthFilter
    @Autowired
    lateinit var authController: AuthController
    @Autowired
    lateinit var userController: UserController
    @Autowired
    lateinit var exerciseController: ExerciseController
    @Autowired
    lateinit var exercisesListController: ExercisesListController

    @Bean
    open fun authRoutesDeclaration() = AuthRoutesDeclaration(gson, authFilter, authController)

    @Bean
    open fun userRoutesDeclaration() = UserRoutesDeclaration(gson, authFilter, userController)

    @Bean
    open fun exerciseRoutesDeclaration() = ExerciseRoutesDeclaration(gson, authFilter, exerciseController)

    @Bean
    open fun exercisesListRoutesDeclaration() = ExercisesListRoutesDeclaration(gson, authFilter, exercisesListController)

    @Bean
    open fun errorsRoutesDeclaration() = ErrorsRoutesDeclaration(gson, authFilter)
}