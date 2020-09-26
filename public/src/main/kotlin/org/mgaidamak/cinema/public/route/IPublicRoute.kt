package org.mgaidamak.cinema.public.route

import org.mgaidamak.cinema.public.dao.Bill
import org.mgaidamak.cinema.public.dao.Cinema
import org.mgaidamak.cinema.public.dao.Seat
import org.mgaidamak.cinema.public.dao.Session
import org.mgaidamak.cinema.public.response.Response
import java.time.LocalDate

interface IPublicRoute {
    suspend fun getCinemas(city: String?): Response<Collection<Cinema>>
    suspend fun getSessions(cinema: Int, date: LocalDate): Response<Collection<Session>>
    suspend fun getSeats(session: Int): Response<Collection<Seat>>
    suspend fun postBill(bill: Bill): Response<Bill>
    suspend fun getBill(id: Int): Response<Bill>
    suspend fun deleteBill(id: Int): Response<Bill>
}