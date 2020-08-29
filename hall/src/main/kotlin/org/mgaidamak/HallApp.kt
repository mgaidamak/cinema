package org.mgaidamak

import com.typesafe.config.ConfigFactory
import io.ktor.application.*
import io.ktor.config.HoconApplicationConfig
import io.ktor.features.ContentNegotiation
import io.ktor.http.*
import io.ktor.gson.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.util.KtorExperimentalAPI
import org.mgaidamak.repo.FileHallRepo
import org.mgaidamak.repo.IHallRepo

@KtorExperimentalAPI
fun main(args: Array<String>) {
    val env = applicationEngineEnvironment {
        module { hall() }
        config = HoconApplicationConfig(ConfigFactory.load())
        connector {
            host = config.property("ktor.deployment.host").getString()
            port = config.property("ktor.deployment.port").getString().toInt()
        }
    }
    embeddedServer(Netty, env).start(true)
}

fun Application.hall() {
    hallWithDependencies(FileHallRepo())
}

fun Application.hallWithDependencies(repo: IHallRepo) {
    install(ContentNegotiation) {
        gson {
        }
    }
    routing {
        get("/") {
            call.respondText("My Hall App", ContentType.Text.Html)
        }
        HallApiServer(repo).apply {
            registerCinema()
        }
    }
}