package org.mgaidamak.cinema.ticket

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
import org.mgaidamak.cinema.ticket.dao.bill.DbBillRepo
import org.mgaidamak.cinema.ticket.dao.bill.IBillRepo
import org.mgaidamak.cinema.ticket.dao.ticket.DbTicketRepo
import org.mgaidamak.cinema.ticket.dao.ticket.ITicketRepo
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
        module {
            common()
            bill(DbBillRepo(url, props))
            ticket(DbTicketRepo(url, props))
        }
    }
    embeddedServer(Netty, env).start(true)
}

fun Application.common() {
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
}

fun Application.bill(repo: IBillRepo) {
    routing {
        TicketApiServer().apply {
            registerBill(repo)
        }
    }
}

fun Application.ticket(repo: ITicketRepo) {
    routing {
        TicketApiServer().apply {
            registerTicket(repo)
        }
    }
}