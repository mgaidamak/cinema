package org.mgaidamak.cinema.public

import io.ktor.application.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.http.*
import io.ktor.request.receive
import io.ktor.request.uri
import org.mgaidamak.cinema.public.dao.IPublicRepo
import org.mgaidamak.cinema.public.dao.Order
import java.time.LocalDate

class PublicApiServer(val repo: IPublicRepo) {

    fun Routing.public() {
        route("/public") {
            get("/cinema") {
                println("Get ${call.request.uri}")
                println("Params ${call.parameters}")
                val city = call.parameters["city"]
                repo.getCinemas(city).also { call.respond(it) }
            }
            get("/session") {
                println("Get ${call.request.uri}")
                println("Params ${call.parameters}")
                val cinema = call.parameters["cinema"]?.toIntOrNull()
                if (cinema == null) {
                    call.respond(HttpStatusCode.BadRequest, "Invalid cinema")
                    return@get
                }
                val date = call.parameters["date"]?.let { d -> LocalDate.parse(d) }
                    ?: LocalDate.now()
                repo.getSessions(cinema, date).also { call.respond(it) }
            }
            get("/seat") {
                println("Get ${call.request.uri}")
                println("Params ${call.parameters}")
                val session = call.parameters["session"]?.toIntOrNull()
                if (session == null) {
                    call.respond(HttpStatusCode.BadRequest, "Invalid session")
                    return@get
                }
                repo.getSeats(session).also { call.respond(it) }
            }
            route("/order/{id?}") {
                get {
                    println("Get ${call.request.uri}")
                    println("Params ${call.parameters}")
                    val id = call.parameters["id"]?.toIntOrNull()
                    if (id == null) {
                        call.respond(HttpStatusCode.BadRequest, "Invalid id")
                        return@get
                    }
                    repo.getOrder(id)?.also { call.respond(it) }
                        ?: call.respond(HttpStatusCode.NotFound, "Not found by $id")
                }
                post {
                    println("Post ${call.request.uri}")
                    val body: Order = call.receive()
                    repo.postOrder(body)?.also { call.respond(it) }
                        ?: call.respond(HttpStatusCode.NotFound, "Not created")
                }
                delete {
                    println("Delete ${call.request.uri}")
                    println("Params ${call.parameters}")
                    val id = call.parameters["id"]?.toIntOrNull()
                    if (id == null) {
                        call.respond(HttpStatusCode.BadRequest, "Invalid id")
                        return@delete
                    }
                    repo.deleteOrder(id)?.also { call.respond(it) }
                        ?: call.respond(HttpStatusCode.NotFound, "Not found by $id")
                }
            }
        }
    }
}