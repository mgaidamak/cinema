package org.mgaidamak.cinema.ticket

import io.ktor.application.call
import io.ktor.http.ContentType
import io.ktor.response.respondText
import io.ktor.routing.Routing
import io.ktor.routing.get
import org.mgaidamak.cinema.ticket.dao.bill.IBillRepo
import org.mgaidamak.cinema.ticket.dao.ticket.ITicketRepo

class TicketApiServer {
    fun Routing.registerBill(repo: IBillRepo) {
        get("/") {
            call.respondText("My Ticket App", ContentType.Text.Html)
        }
    }

    fun Routing.registerTicket(repo: ITicketRepo) {
    }
}