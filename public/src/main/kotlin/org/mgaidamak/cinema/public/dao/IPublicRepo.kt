package org.mgaidamak.cinema.public.dao

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import java.time.LocalDate
import kotlin.streams.toList

abstract class IPublicRepo() {

    abstract val hallClient: HttpClient
    abstract val sessionClient: HttpClient
    abstract val ticketClient: HttpClient

    suspend fun getCinemas(city: String?): Collection<Cinema> {
        val path = "/cinema".plus(city?.let { "?city=$city" } ?: "")
        println("Try $path")
        val list = hallClient.get<List<AdminCinema>>(path = path)
        return list.stream().map { Cinema(it) }.toList()
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