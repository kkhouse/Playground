package org.koin.sample

import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.application.log
import io.ktor.features.CallLogging
import io.ktor.features.DefaultHeaders
import io.ktor.response.respondText
import io.ktor.routing.Routing
import io.ktor.routing.get
import io.ktor.routing.routing
import io.ktor.server.engine.commandLineEnvironment
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import org.koin.dsl.bind
import org.koin.logger.slf4jLogger
import org.koin.dsl.module
import org.koin.dsl.single
import org.koin.ktor.ext.*

fun Application.main() {
    install(DefaultHeaders)
    install(CallLogging)

    // Install Ktor features
    environment.monitor.subscribe(KoinApplicationStarted) {
        log.info("Koin started.")
    }
    install(Koin) {
        slf4jLogger()
        modules(helloAppModule)
    }
    environment.monitor.subscribe(KoinApplicationStopPreparing) {
        log.info("Koin stopping...")
    }
    environment.monitor.subscribe(KoinApplicationStopped) {
        log.info("Koin stopped.")
    }

    //
    val helloService by inject<HelloService>()
    // Routing section
    routing {
        get("/hello") {
            call.respondText(helloService.sayHello())
        }
        declareRoutes()
    }
}

private fun Routing.declareRoutes() {
    v1()
    bye()
    respondWithHello()
}

val helloAppModule = module {
    single<HelloServiceImpl>(createOnStart = true) bind HelloService::class
    // also singleBy<HelloService, HelloServiceImpl>()
    single<HelloRepository>(createOnStart = true)
}

fun main(args: Array<String>) {
    // Start Ktor
    embeddedServer(Netty, commandLineEnvironment(args)).start()
}
