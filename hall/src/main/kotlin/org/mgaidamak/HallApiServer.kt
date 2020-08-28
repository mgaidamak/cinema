package org.mgaidamak

import io.ktor.application.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.http.*
import io.ktor.request.document
import io.ktor.request.receive
import org.mgaidamak.repo.IHallRepo

class HallApiServer(val repo: IHallRepo) {

    fun Routing.registerCinema() {
        post("/cinema") {
            val body: Cinema = call.receive()
            val created = repo.createCinema(body)
            call.respond(created)
        }

        get("/cinema/") {
            val page = Page(0, 10)
            val sort = listOf("id")
            // val found = repo.getCinemas(page, sort)
            // call.respond(found)
            repo.getCinemas(page, sort).also { call.respond(it) }
        }

        get("/cinema/{id}") {
            val id = call.request.document().toLongOrNull()
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, "Invalid id")
                return@get
            }

            repo.getCinemaById(id)?.also { call.respond(it) }
                ?: call.respond(HttpStatusCode.NotFound, "Not found by $id")
        }

        delete("/cinema/{id}") {
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