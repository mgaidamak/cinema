package org.mgaidamak.cinema.public.dao

import io.ktor.client.HttpClient
import io.ktor.client.request.get
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

    suspend fun getBill(id: Int): Bill? {
        TODO("Not yet implemented")
    }

    suspend fun postBill(bill: Bill): Bill? {
        TODO("Not yet implemented")
    }

    suspend fun deleteBill(id: Int): Bill? {
        TODO("Not yet implemented")
    }
}