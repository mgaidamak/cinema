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

fun main(args: Array<String>) {
    embeddedServer(Netty, 8080) {
        install(ContentNegotiation) {
            gson {
            }
        }
        routing {
            get("/") {
                call.respondText("My Hall App", ContentType.Text.Html)
            }
            HallApiServer(FileHallRepo()).apply {
                registerCinema()
            }
        }
    }.start(wait = true)
}