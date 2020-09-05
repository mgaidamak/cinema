package org.mgaidamak.cinema.public.dao

import java.time.LocalDate

interface IPublicRepo {
    suspend fun getCinemas(city: String?): Collection<Cinema>
    suspend fun getSessions(cinema: Int, date: LocalDate): Collection<Session>
    suspend fun getSeats(session: Int): Collection<Seat>
    suspend fun getBill(id: Int): Bill?
    suspend fun postBill(bill: Bill): Bill?
    suspend fun deleteBill(id: Int): Bill?
}