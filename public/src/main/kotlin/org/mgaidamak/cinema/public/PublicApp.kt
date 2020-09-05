package org.mgaidamak.cinema.public

import com.typesafe.config.ConfigFactory
import io.ktor.application.*
import io.ktor.config.HoconApplicationConfig
import io.ktor.features.ContentNegotiation
import io.ktor.features.StatusPages
import io.ktor.http.*
import io.ktor.gson.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.util.KtorExperimentalAPI
import org.mgaidamak.cinema.public.dao.IPublicRepo
import org.mgaidamak.cinema.public.dao.RestPublicRepo

@KtorExperimentalAPI
fun main(args: Array<String>) {
    val env = applicationEngineEnvironment {
        config = HoconApplicationConfig(ConfigFactory.load())
        connector {
            host = config.property("ktor.deployment.host").getString()
            port = config.property("ktor.deployment.port").getString().toInt()
        }
        module { public(RestPublicRepo(config)) }
    }
    embeddedServer(Netty, env).start(true)
}

fun Application.public(repo: IPublicRepo) {
    install(ContentNegotiation) {
        gson {
        }
    }
    // TODO install(CallLogging)
    install(StatusPages) {
        exception<Throwable> { cause ->
            call.respond(HttpStatusCode.InternalServerError, cause.message ?: "Unknown error")
        }
    }
    routing {
        get("/") {
            call.respondText("My Public App", ContentType.Text.Html)
        }
        PublicApiServer(repo).apply {
            public()
        }
    }
}