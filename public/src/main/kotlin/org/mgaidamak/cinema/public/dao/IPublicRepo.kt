package org.mgaidamak.cinema.public.dao

import io.ktor.client.HttpClient
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders

/**
 * Stateless repository, that process all public requests to admin api
 * TODO make it CRUD only
 * return object/code only
 * all chains move to api routes
 */
abstract class IPublicRepo {

    abstract val hallClient: HttpClient
    abstract val sessionClient: HttpClient
    abstract val ticketClient: HttpClient

    /**
     * Get available cinema at my city
     */
    suspend fun getCinemas(city: String?): Collection<AdminCinema> {
        // Get available cinema at my city
        val path = "/cinema".plus(city?.let { "?city=$city" } ?: "")
        println("Try $path")
        return hallClient.get<List<AdminCinema>>(path = path)
    }

    suspend fun getHalls(cinema: Int): Collection<AdminHall> {
        // Get hall list
        val hpath = "/hall?cinema=$cinema"
        println("Try $hpath")
        return hallClient.get<List<AdminHall>>(path = hpath)
    }

    suspend fun getSessions(halls: IntArray): Collection<AdminSession> {
        val harg = halls.joinToString(separator = "&") { "hall=$it" }
        val spath = "/session?$harg"
        println("Try $spath")
        return sessionClient.get<List<AdminSession>>(path = spath)
    }

    suspend fun getFilm(film: Int): AdminFilm {
        val fpath = "/film/$film"
        println("Try $fpath")
        return sessionClient.get(path = fpath)
    }

    suspend fun getSession(session: Int): AdminSession {
        val path = "/session/$session"
        println("Try $path")
        return sessionClient.get<AdminSession>(path = path)
    }

    suspend fun getSeats(hall: Int): Collection<AdminSeat> {
        val path = "/seat?hall=$hall"
        println("Try $path")
        return hallClient.get<List<AdminSeat>>(path = path)
    }

    suspend fun getTockets(session: Int): Collection<AdminTicket> {
        val ticketPath = "/ticket?session=$session"
        println("Try $ticketPath")
        return ticketClient.get<List<AdminTicket>>(path = ticketPath)
    }

    /**
     * User try to create bill and buy tickets
     */
    suspend fun postBill(bill: Bill): Bill? {
        // Post new bill
        val billPath = "/bill"
        println("Try $billPath")
        val billBody = AdminBill(customer = bill.customer, session = bill.session,
        status = 0, total = bill.total)
        val newBill = ticketClient.post<AdminBill>(path = billPath, body = billBody) {
            header(HttpHeaders.ContentType, ContentType.Application.Json.toString())
        }
        // Post tickets
        val soldSeats = ArrayList<Seat>(bill.seats.size)
        val ticketPath = "/ticket"
        println("Try $ticketPath")
        for (seat in bill.seats) {
            val ticketBody = AdminTicket(bill = newBill.id, session = bill.session,
            seat = seat.id, status = 2)
            ticketClient.post<AdminTicket>(path = ticketPath, body = ticketBody) {
                header(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            }
            soldSeats.add(seat.copy(status = 2))
        }
        // Confirm bill status
        val patchPath = "/bill/${newBill.id}"
        println("Try $patchPath")
        val patchBody = newBill.copy(status = 1)
        val patchBill = ticketClient.patch<AdminBill>(path = patchPath, body = patchBody) {
            header(HttpHeaders.ContentType, ContentType.Application.Json.toString())
        }
        return Bill(patchBill, soldSeats)
    }

    /**
     * User looking for bill status some time later
     */
    suspend fun getBill(id: Int): Bill? {
        // Show bill status
        val billPath = "/bill/$id"
        println("Try $billPath")
        val adminBill = ticketClient.get<AdminBill>(path = billPath)
        // Show payed tickets
        val ticketPath = "/ticket?bill=$id"
        println("Try $ticketPath")
        val adminTickets = ticketClient.get<List<AdminTicket>>(path = ticketPath)
        val seats = ArrayList<Seat>(adminTickets.size)
        for (adminTicket in adminTickets) {
            // Get seat information
            val seatPath = "/seat/${adminTicket.seat}"
            println("Try $seatPath")
            val adminSeat = hallClient.get<AdminSeat>(path = seatPath)
            seats.add(Seat(adminSeat, adminTicket.status))
        }
        return Bill(adminBill, seats)
    }

    /**
     * User wants to return money for tickets
     */
    suspend fun deleteBill(id: Int): Bill? {
        // Show bill status
        val billPath = "/bill/$id"
        println("Try $billPath")
        val adminBill = ticketClient.get<AdminBill>(path = billPath)
        // Delete payed tickets
        val ticketPath = "/ticket?bill=$id"
        println("Try $ticketPath")
        val adminTickets = ticketClient.delete<List<AdminTicket>>(path = ticketPath)
        val seats = adminTickets.map { Seat(it.seat, 0, 0, 0) }
        // Patch bill status
        val patchPath = "/bill/$id"
        println("Try $patchPath")
        val patchBody = adminBill.copy(status = 3)
        val patchBill = ticketClient.patch<AdminBill>(path = patchPath, body = patchBody) {
            header(HttpHeaders.ContentType, ContentType.Application.Json.toString())
        }
        return Bill(patchBill, seats)
    }
}