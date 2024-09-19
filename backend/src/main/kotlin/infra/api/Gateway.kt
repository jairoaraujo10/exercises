package infra.api

import spark.Service


class Gateway(val port: Int) {
    private val service: Service = Service.ignite()
    private val routesDeclarations: MutableList<AbstractRoutesDeclaration> = mutableListOf()

    fun addRoutes(routesDeclaration: AbstractRoutesDeclaration) {
        this.routesDeclarations.add(routesDeclaration)
    }

    fun start() {
        service.port(this.port)
        enableCORS("*", "OPTIONS, GET, POST, PUT, DELETE", "Authorization,Content-Type", true)
        routesDeclarations.forEach { routeDeclaration -> routeDeclaration.declareRoutes(service) }
    }

    private fun enableCORS(origin: String, methods: String, headers: String, allowCredentials: Boolean) {
        service.options("/*") { request, response ->
            val accessControlRequestHeaders: String = request.headers("Access-Control-Request-Headers")
            response.header("Access-Control-Allow-Headers", accessControlRequestHeaders)

            val accessControlRequestMethod: String = request.headers("Access-Control-Request-Method")
            response.header("Access-Control-Allow-Methods", accessControlRequestMethod)
            "OK"
        }

        service.before { _, response ->
            response.header("Access-Control-Allow-Origin", origin)
            response.header("Access-Control-Request-Method", methods)
            response.header("Access-Control-Allow-Headers", headers)
            response.header("Access-Control-Allow-Credentials", allowCredentials.toString())
        }
    }

    fun stop() {
        service.stop()
    }
}