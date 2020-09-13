package org.mgaidamak.cinema.public.route

import org.mgaidamak.cinema.public.dao.Bill
import org.mgaidamak.cinema.public.dao.Cinema
import org.mgaidamak.cinema.public.dao.Seat
import org.mgaidamak.cinema.public.dao.Session
import java.time.LocalDate

interface IPublicRoute {
    suspend fun getCinemas(city: String?): Collection<Cinema>
    suspend fun getSessions(cinema: Int, date: LocalDate): Collection<Session>
    suspend fun getSeats(session: Int): Collection<Seat>
    suspend fun postBill(bill: Bill): Bill?
    suspend fun getBill(id: Int): Bill?
    suspend fun deleteBill(id: Int): Bill?
}