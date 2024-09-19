import infra.api.AbstractRoutesDeclaration
import infra.api.Gateway
import infra.api.routes.*
import infra.contexts.*
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.springframework.context.annotation.AnnotationConfigApplicationContext

class Application(
    private val context: AnnotationConfigApplicationContext = AnnotationConfigApplicationContext(),
    private val gateway: Gateway = Gateway(8080),
    dataSourceContext: Class<*>
) {
    private var logger: Logger = LogManager.getLogger(Application::class.java)
    private val routesDeclarationClasses: ArrayList<Class<out AbstractRoutesDeclaration>> = ArrayList()

    init {
        this.withContext(dataSourceContext)
            .withContext(UtilsContext::class.java)
            .withContext(JdbcContext::class.java)
            .withContext(AuthorizationContext::class.java)
            .withContext(UserContext::class.java)
            .withContext(AuthContext::class.java)
            .withContext(ExerciseContext::class.java)
            .withContext(ExercisesListContext::class.java)
            .withContext(RoutesContext::class.java)
            .withRoutes(AuthRoutesDeclaration::class.java)
            .withRoutes(UserRoutesDeclaration::class.java)
            .withRoutes(ExerciseRoutesDeclaration::class.java)
            .withRoutes(ExercisesListRoutesDeclaration::class.java)
            .withRoutes(ErrorsRoutesDeclaration::class.java)
    }

    private fun withContext(clazz: Class<*>): Application {
        context.register(clazz)
        return this
    }
    
    private fun withRoutes(routesDeclaration: Class<out AbstractRoutesDeclaration>): Application {
        routesDeclarationClasses.add(routesDeclaration)
        return this
    }

    fun start() {
        logger.info("Starting application...")
        context.refresh()
        routesDeclarationClasses.forEach { abstractRoutes -> gateway.addRoutes(context.getBean(abstractRoutes))}
        gateway.start()
        logger.info("Application started.")
    }

    fun stop() {
        logger.info("Stopping application...")
        context.stop()
        gateway.stop()
        logger.info("Application stopped.")
    }
}


fun main() {
    val application = Application(dataSourceContext = DataSourceContext::class.java)

    application.start()

    Runtime.getRuntime().addShutdownHook(Thread {
        application.stop()
    })
}