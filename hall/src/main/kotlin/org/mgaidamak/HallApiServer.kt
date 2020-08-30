package org.mgaidamak

import io.ktor.application.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.http.*
import io.ktor.request.document
import io.ktor.request.receive
import io.ktor.request.uri
import org.mgaidamak.repo.IHallRepo

class HallApiServer(val repo: IHallRepo) {

    fun Routing.registerCinema() {
        post("/cinema") {
            println("Post ${call.request.uri}")
            val body: Cinema = call.receive()
            val created = repo.createCinema(body)
            call.respond(created)
        }

        get("/cinema/") {
            println("Get ${call.request.uri}")
            val page = Page(0, 10)
            val sort = listOf("id")
            repo.getCinemas(page, sort).also { call.respond(it) }
        }

        get("/cinema/{id}") {
            println("Get ${call.request.uri}")
            val id = call.request.document().toLongOrNull()
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, "Invalid id")
                return@get
            }

            repo.getCinemaById(id)?.also { call.respond(it) }
                ?: call.respond(HttpStatusCode.NotFound, "Not found by $id")
        }

        delete("/cinema/{id}") {
            println("Delete ${call.request.uri}")
            val id = call.request.document().toLongOrNull()
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, "Invalid id")
                return@delete
            }

            repo.deleteCinemaById(id)?.also { call.respond(it) }
                ?: call.respond(HttpStatusCode.NotFound, "Not found by $id")
        }
    }
}