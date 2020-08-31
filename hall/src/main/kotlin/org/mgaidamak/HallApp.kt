package org.mgaidamak

import com.typesafe.config.ConfigFactory
import io.ktor.application.*
import io.ktor.config.HoconApplicationConfig
import io.ktor.features.CallLogging
import io.ktor.features.ContentNegotiation
import io.ktor.features.StatusPages
import io.ktor.http.*
import io.ktor.gson.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.util.KtorExperimentalAPI
import org.mgaidamak.dao.cinema.DbCinemaRepo
import org.mgaidamak.dao.cinema.ICinemaRepo
import java.util.Properties

@KtorExperimentalAPI
fun main(args: Array<String>) {
    val env = applicationEngineEnvironment {
        config = HoconApplicationConfig(ConfigFactory.load())
        connector {
            host = config.property("ktor.deployment.host").getString()
            port = config.property("ktor.deployment.port").getString().toInt()
        }

        val url = config.property("postgres.url").getString()
        val props = Properties().apply {
            setProperty("user", config.property("postgres.user").getString())
            setProperty("password", config.property("postgres.password").getString())
        }
        module { cinema(DbCinemaRepo(url, props)) }
    }
    embeddedServer(Netty, env).start(true)
}

fun Application.cinema(repo: ICinemaRepo) {
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
            call.respondText("My Hall App", ContentType.Text.Html)
        }
        HallApiServer(repo).apply {
            registerCinema()
        }
    }
}