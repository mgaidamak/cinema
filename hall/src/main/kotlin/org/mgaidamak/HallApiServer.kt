package org.mgaidamak

import io.ktor.application.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.http.*
import io.ktor.request.document
import io.ktor.request.receive
import io.ktor.request.uri
import org.mgaidamak.dao.Cinema
import org.mgaidamak.dao.Hall
import org.mgaidamak.dao.Page
import org.mgaidamak.dao.Seat
import org.mgaidamak.dao.cinema.ICinemaRepo
import org.mgaidamak.dao.hall.IHallRepo
import org.mgaidamak.dao.seat.ISeatRepo

class HallApiServer {
    fun Routing.registerCinema(repo: ICinemaRepo) {
        get("/") {
            call.respondText("My Hall App", ContentType.Text.Html)
        }
        route("/cinema") {
            post {
                println("Post ${call.request.uri}")
                val body: Cinema = call.receive()
                repo.createCinema(body)?.also { call.respond(it) }
                    ?: call.respond(HttpStatusCode.NotFound, "Not created")
            }
            get {
                println("Get ${call.request.uri}")
                val city = call.parameters["city"]
                val page = Page(0, 10)
                val sort = listOf("id")
                repo.getCinemas(city, page, sort).also { call.respond(it) }
            }
        }
        route("/cinema/{id}") {
            get {
                println("Get ${call.request.uri}")
                val id = call.request.document().toIntOrNull()
                if (id == null) {
                    call.respond(HttpStatusCode.BadRequest, "Invalid id")
                    return@get
                }
                repo.getCinemaById(id)?.also { call.respond(it) }
                    ?: call.respond(HttpStatusCode.NotFound, "Not found by $id")
            }
            delete {
                println("Delete ${call.request.uri}")
                val id = call.request.document().toIntOrNull()
                if (id == null) {
                    call.respond(HttpStatusCode.BadRequest, "Invalid id")
                    return@delete
                }
                repo.deleteCinemaById(id)?.also { call.respond(it) }
                    ?: call.respond(HttpStatusCode.NotFound, "Not found by $id")
            }
        }
    }

    fun Routing.registerHall(repo: IHallRepo) {
        route("/hall") {
            post {
                println("Post ${call.request.uri}")
                val body: Hall = call.receive()
                repo.createHall(body)?.also { call.respond(it) }
                    ?: call.respond(HttpStatusCode.NotFound, "Not created")
            }
            get {
                println("Get ${call.request.uri}")
                val cinema = call.parameters["cinema"]?.toIntOrNull()
                if (cinema == null) {
                    call.respond(HttpStatusCode.BadRequest, "No cinema parameter")
                    return@get
                }
                val page = Page(0, 10)
                val sort = listOf("id")
                repo.getHalls(cinema, page, sort).also { call.respond(it) }
            }
        }
        route("/hall/{id}") {
            get {
                println("Get ${call.request.uri}")
                val id = call.request.document().toIntOrNull()
                if (id == null) {
                    call.respond(HttpStatusCode.BadRequest, "Invalid id")
                    return@get
                }
                repo.getHallById(id)?.also { call.respond(it) }
                    ?: call.respond(HttpStatusCode.NotFound, "Not found by $id")
            }
            delete {
                println("Delete ${call.request.uri}")
                val id = call.request.document().toIntOrNull()
                if (id == null) {
                    call.respond(HttpStatusCode.BadRequest, "Invalid id")
                    return@delete
                }
                repo.deleteHallById(id)?.also { call.respond(it) }
                    ?: call.respond(HttpStatusCode.NotFound, "Not found by $id")
            }
        }
    }

    fun Routing.registerSeat(repo: ISeatRepo) {
        route("/seat") {
            post {
                println("Post ${call.request.uri}")
                val body: Seat = call.receive()
                repo.createSeat(body)?.also { call.respond(it) }
                    ?: call.respond(HttpStatusCode.NotFound, "Not created")
            }
            get {
                println("Get ${call.request.uri}")
                val hall = call.parameters["hall"]?.toIntOrNull()
                if (hall == null) {
                    call.respond(HttpStatusCode.BadRequest, "Invalid hall")
                    return@get
                }
                val page = Page(0, 10)
                repo.getSeats(hall, page).also { call.respond(it) }
            }
        }
        route("/seat/{id}") {
            get {
                println("Get ${call.request.uri}")
                val id = call.request.document().toIntOrNull()
                if (id == null) {
                    call.respond(HttpStatusCode.BadRequest, "Invalid id")
                    return@get
                }
                repo.getSeatById(id)?.also { call.respond(it) }
                    ?: call.respond(HttpStatusCode.NotFound, "Not found by $id")
                return@get
            }
            delete {
                println("Delete ${call.request.uri}")
                val id = call.request.document().toIntOrNull()
                if (id == null) {
                    call.respond(HttpStatusCode.BadRequest, "Invalid id")
                    return@delete
                }
                repo.deleteSeatById(id)?.also { call.respond(it) }
                    ?: call.respond(HttpStatusCode.NotFound, "Not found by $id")
            }
        }
    }
}