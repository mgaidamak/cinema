package org.mgaidamak.cinema.public.dao

import java.time.LocalDate

interface IPublicRepo {
    fun getCinemas(city: String?): Collection<Cinema>
    fun getSessions(cinema: Int, date: LocalDate): Collection<Session>
    fun getSeats(session: Int): Collection<Seat>
    fun getBill(id: Int): Bill?
    fun postBill(bill: Bill): Bill?
    fun deleteBill(id: Int): Bill?
}