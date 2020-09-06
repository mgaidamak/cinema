package org.mgaidamak.cinema.public.dao

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import java.time.LocalDate
import kotlin.streams.toList

/**
 * Stateless repository, that process all public requests to admin api
 */
abstract class IPublicRepo {

    abstract val hallClient: HttpClient
    abstract val sessionClient: HttpClient
    abstract val ticketClient: HttpClient

    suspend fun getCinemas(city: String?): Collection<Cinema> {
        val cpath = "/cinema".plus(city?.let { "?city=$city" } ?: "")
        println("Try $cpath")
        val clist = hallClient.get<List<AdminCinema>>(path = cpath)
        val result = ArrayList<Cinema>(clist.size)
        // TODO how to run suspend fun in FP-style
        for (it in clist) {
            val hpath = "/hall?cinema=${it.id}"
            println("Try $hpath")
            val hlist = hallClient.get<List<AdminHall>>(path = hpath)
            result.add(Cinema(it, hlist.stream().map { Hall(it) }.toList()))
        }
        return result
    }

    suspend fun getSessions(cinema: Int, date: LocalDate): Collection<Session> {
        TODO("Not yet implemented")
    }

    suspend fun getSeats(session: Int): Collection<Seat> {
        TODO("Not yet implemented")
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