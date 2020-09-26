package org.mgaidamak.cinema.public.dao

import io.ktor.client.HttpClient
import io.ktor.client.features.ClientRequestException
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.statement.readText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import org.mgaidamak.cinema.public.response.Response

/**
 * Stateless repository, that process all public requests to admin api
 * It makes CRUD only
 * TODO return object/code only
 */
abstract class IPublicRepo {

    abstract val hallClient: HttpClient
    abstract val sessionClient: HttpClient
    abstract val ticketClient: HttpClient

    // Common

    private suspend fun <T> call(block: suspend () -> T): Response<T> {
        return try {
            Response(data = block())
        } catch (e: ClientRequestException) {
            Response(code = e.response?.status ?: HttpStatusCode.NotFound,
                descr = e.response?.readText())
        }
    }

    // Hall service

    /**
     * Get available cinema at my city
     */
    suspend fun getCinemas(city: String?) = call {
        // Get available cinema at my city
        val path = "/cinema".plus(city?.let { "?city=$city" } ?: "")
        println("Try $path")
        hallClient.get<List<AdminCinema>>(path = path)
    }

    suspend fun getHalls(cinema: Int) = call {
        // Get hall list
        val hpath = "/hall?cinema=$cinema"
        println("Try $hpath")
        hallClient.get<List<AdminHall>>(path = hpath)
    }

    suspend fun getSeats(hall: Int) = call {
        val path = "/seat?hall=$hall"
        println("Try $path")
        hallClient.get<Collection<AdminSeat>>(path = path)
    }

    suspend fun getSeat(id: Int) = call {
        val seatPath = "/seat/$id"
        println("Try $seatPath")
        hallClient.get<AdminSeat>(path = seatPath)
    }

    // Session service

    suspend fun getSessions(halls: IntArray) = call {
        val harg = halls.joinToString(separator = "&") { "hall=$it" }
        val spath = "/session?$harg"
        println("Try $spath")
        sessionClient.get<Collection<AdminSession>>(path = spath)
    }

    suspend fun getFilm(film: Int) = call {
        val fpath = "/film/$film"
        println("Try $fpath")
        sessionClient.get<AdminFilm>(path = fpath)
    }

    suspend fun getSession(session: Int) = call {
        val path = "/session/$session"
        println("Try $path")
        sessionClient.get<AdminSession>(path = path)
    }

    // Ticket service

    suspend fun getTickets(session: Int) = call {
        val ticketPath = "/ticket?session=$session"
        println("Try $ticketPath")
        ticketClient.get<Collection<AdminTicket>>(path = ticketPath)
    }

    suspend fun createBill(bill: AdminBill) = call {
        // Post new bill
        val billPath = "/bill"
        println("Try $billPath")
        ticketClient.post<AdminBill>(path = billPath, body = bill) {
            header(HttpHeaders.ContentType, ContentType.Application.Json.toString())
        }
    }

    suspend fun patchBill(bill: AdminBill) = call {
        // Post new bill
        val patchPath = "/bill/${bill.id}"
        println("Try $patchPath")
        ticketClient.patch<AdminBill>(path = patchPath, body = bill) {
            header(HttpHeaders.ContentType, ContentType.Application.Json.toString())
        }
    }

    suspend fun createTicket(ticket: AdminTicket) = call {
        val ticketPath = "/ticket"
        println("Try $ticketPath")
        ticketClient.post<AdminTicket>(path = ticketPath, body = ticket) {
            header(HttpHeaders.ContentType, ContentType.Application.Json.toString())
        }
    }

    suspend fun getBill(id: Int) = call {
        val billPath = "/bill/$id"
        println("Try $billPath")
        ticketClient.get<AdminBill>(path = billPath)
    }

    suspend fun getBillTickets(id: Int) = call {
        val ticketPath = "/ticket?bill=$id"
        println("Try $ticketPath")
        ticketClient.get<Collection<AdminTicket>>(path = ticketPath)
    }

    suspend fun deleteBillTickets(id: Int) = call {
        val ticketPath = "/ticket?bill=$id"
        println("Try $ticketPath")
        ticketClient.delete<Collection<AdminTicket>>(path = ticketPath)
    }
}