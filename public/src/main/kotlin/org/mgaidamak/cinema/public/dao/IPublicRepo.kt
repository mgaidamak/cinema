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
 * It makes CRUD only
 * TODO return object/code only
 */
abstract class IPublicRepo {

    abstract val hallClient: HttpClient
    abstract val sessionClient: HttpClient
    abstract val ticketClient: HttpClient

    // Hall service

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

    suspend fun getSeats(hall: Int): Collection<AdminSeat> {
        val path = "/seat?hall=$hall"
        println("Try $path")
        return hallClient.get<List<AdminSeat>>(path = path)
    }

    suspend fun getSeat(id: Int): AdminSeat {
        val seatPath = "/seat/$id"
        println("Try $seatPath")
        return hallClient.get(path = seatPath)
    }

    // Session service

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
        return sessionClient.get(path = path)
    }

    // Ticket service

    suspend fun getTickets(session: Int): Collection<AdminTicket> {
        val ticketPath = "/ticket?session=$session"
        println("Try $ticketPath")
        return ticketClient.get<List<AdminTicket>>(path = ticketPath)
    }

    suspend fun createBill(bill: AdminBill): AdminBill {
        // Post new bill
        val billPath = "/bill"
        println("Try $billPath")
        return ticketClient.post(path = billPath, body = bill) {
            header(HttpHeaders.ContentType, ContentType.Application.Json.toString())
        }
    }

    suspend fun patchBill(bill: AdminBill): AdminBill {
        // Post new bill
        val patchPath = "/bill/${bill.id}"
        println("Try $patchPath")
        return ticketClient.patch(path = patchPath, body = bill) {
            header(HttpHeaders.ContentType, ContentType.Application.Json.toString())
        }
    }

    suspend fun createTicket(ticket: AdminTicket): AdminTicket {
        val ticketPath = "/ticket"
        println("Try $ticketPath")
        return ticketClient.post(path = ticketPath, body = ticket) {
            header(HttpHeaders.ContentType, ContentType.Application.Json.toString())
        }
    }

    suspend fun getBill(id: Int): AdminBill {
        val billPath = "/bill/$id"
        println("Try $billPath")
        return ticketClient.get(path = billPath)
    }

    suspend fun getBillTickets(id: Int): Collection<AdminTicket> {
        val ticketPath = "/ticket?bill=$id"
        println("Try $ticketPath")
        return ticketClient.get<List<AdminTicket>>(path = ticketPath)
    }

    suspend fun deleteBillTickets(id: Int): Collection<AdminTicket> {
        val ticketPath = "/ticket?bill=$id"
        println("Try $ticketPath")
        return ticketClient.delete<List<AdminTicket>>(path = ticketPath)
    }
}