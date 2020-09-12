package org.mgaidamak.cinema.public.dao

import io.ktor.client.HttpClient
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import java.time.LocalDate

/**
 * Stateless repository, that process all public requests to admin api
 */
abstract class IPublicRepo {

    abstract val hallClient: HttpClient
    abstract val sessionClient: HttpClient
    abstract val ticketClient: HttpClient

    /**
     * Get available cinema at my city
     */
    suspend fun getCinemas(city: String?): Collection<Cinema> {
        // Get available cinema at my city
        val path = "/cinema".plus(city?.let { "?city=$city" } ?: "")
        println("Try $path")
        val list = hallClient.get<List<AdminCinema>>(path = path)
        return list.map { Cinema(it) }
    }

    /**
     * Get available session at cinema
     */
    suspend fun getSessions(cinema: Int, date: LocalDate): Collection<Session> {
        // Get hall list
        val hpath = "/hall?cinema=$cinema"
        println("Try $hpath")
        val hlist = hallClient.get<List<AdminHall>>(path = hpath)
        // Get session list
        // TODO process date
        val harg = hlist.joinToString(separator = "&") { "hall=${it.id}" }
        val spath = "/session?$harg"
        println("Try $spath")
        val slist = sessionClient.get<List<AdminSession>>(path = spath)
        val result = ArrayList<Session>(slist.size)
        // it can be global application hashmap
        val films = HashMap<Int, String>()
        // Enrich with film name
        for (it in slist) {
            val id = it.film
            var film = films[id]
            if (film == null) {
                val fpath = "/film/$id"
                println("Try $fpath")
                // TODO call suspend fun in FP style
                film = sessionClient.get<AdminFilm>(path = fpath).let {
                    films[id] = it.name
                    it.name
                }
            }
            result.add(Session(it, film))
        }
        return result
    }

    /**
     * Get available seats at hall
     */
    suspend fun getSeats(session: Int): Collection<Seat> {
        // Get full session info
        val sessionPath = "/session/$session"
        println("Try $sessionPath")
        val adminSession = sessionClient.get<AdminSession>(path = sessionPath)
        // Get seats
        val seatPath = "/seat?hall=${adminSession.hall}"
        println("Try $seatPath")
        val seatList = hallClient.get<List<AdminSeat>>(path = seatPath)
        // Get sold tickets
        val ticketPath = "/ticket?session=${adminSession.id}"
        println("Try $ticketPath")
        val ticketList = ticketClient.get<List<AdminTicket>>(path = ticketPath)
        val soldMap = ticketList.map { it.seat to it.status }.toMap()
        // Collect to hall map with status
        return seatList.map { Seat(it, soldMap.getOrDefault(it.id, 0)) }
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
        val seats = ArrayList<Seat>()
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