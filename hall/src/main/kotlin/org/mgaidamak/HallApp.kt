package org.mgaidamak

import io.ktor.application.*
import io.ktor.features.ContentNegotiation
import io.ktor.http.*
import io.ktor.gson.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import org.mgaidamak.repo.FileHallRepo
import org.mgaidamak.repo.IHallRepo

fun main(args: Array<String>) {
    embeddedServer(Netty, 8080) {
        hall()
    }.start(wait = true)
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