package org.mgaidamak.cinema.public.dao

import java.time.LocalDate

interface IPublicRepo {
    fun getCinemas(city: String?): Collection<Cinema>
    fun getSessions(cinema: Int, date: LocalDate): Collection<Session>
    fun getSeats(session: Int): Collection<Seat>
    fun getOrder(id: Int): Order?
    fun postOrder(order: Order): Order?
    fun deleteOrder(order: Int): Order?
}