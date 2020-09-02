package org.mgaidamak.cinema.public.dao

import java.time.LocalDate

class RestPublicRepo(val url: String): IPublicRepo {
    override fun getCinemas(city: String?): Collection<Cinema> {
        TODO("Not yet implemented")
    }

    override fun getSessions(cinema: Int, date: LocalDate): Collection<Session> {
        TODO("Not yet implemented")
    }

    override fun getSeats(session: Int): Collection<Seat> {
        TODO("Not yet implemented")
    }

    override fun getOrder(id: Int): Order? {
        TODO("Not yet implemented")
    }

    override fun postOrder(order: Order): Order? {
        TODO("Not yet implemented")
    }

    override fun deleteOrder(order: Int): Order? {
        TODO("Not yet implemented")
    }
}