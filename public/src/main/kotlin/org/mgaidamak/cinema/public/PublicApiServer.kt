package org.mgaidamak.cinema.public

import io.ktor.application.*
import io.ktor.client.features.ClientRequestException
import io.ktor.client.statement.readText
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.http.*
import io.ktor.request.receive
import io.ktor.request.uri
import org.mgaidamak.cinema.public.dao.Bill
import org.mgaidamak.cinema.public.response.Response
import org.mgaidamak.cinema.public.route.IPublicRoute
import java.time.LocalDate

class PublicApiServer(private val route: IPublicRoute) {

    private suspend fun <T> ApplicationCall.respond(block: suspend () -> Response<T>) {
        val res = block()
        if (res.succeed() && res.data != null) {
            respond(res.data)
        } else {
            respond(res.code, res.message ?: "")
        }
    }

    fun Routing.public() {
        route("/public") {
            get("/cinema") {
                println("Get ${call.request.uri}")
                println("Params ${call.parameters}")
                val city = call.parameters["city"]
                call.respond { route.getCinemas(city) }
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
                call.respond { route.getSessions(cinema, date) }
            }
            get("/seat") {
                println("Get ${call.request.uri}")
                println("Params ${call.parameters}")
                val session = call.parameters["session"]?.toIntOrNull()
                if (session == null) {
                    call.respond(HttpStatusCode.BadRequest, "Invalid session")
                    return@get
                }
                call.respond { route.getSeats(session) }
            }
            post("/bill") {
                println("Post ${call.request.uri}")
                val body: Bill = call.receive()
                println("Body $body")
                call.respond { route.postBill(body) }
            }
            route("/bill/{id}") {
                get {
                    println("Get ${call.request.uri}")
                    println("Params ${call.parameters}")
                    val id = call.parameters["id"]?.toIntOrNull()
                    if (id == null) {
                        call.respond(HttpStatusCode.BadRequest, "Invalid id")
                        return@get
                    }
                    call.respond { route.getBill(id) }
                }
                delete {
                    println("Delete ${call.request.uri}")
                    println("Params ${call.parameters}")
                    val id = call.parameters["id"]?.toIntOrNull()
                    if (id == null) {
                        call.respond(HttpStatusCode.BadRequest, "Invalid id")
                        return@delete
                    }
                    call.respond { route.deleteBill(id) }
                }
            }
        }
    }
}