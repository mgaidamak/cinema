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

    override fun getBill(id: Int): Bill? {
        TODO("Not yet implemented")
    }

    override fun postBill(bill: Bill): Bill? {
        TODO("Not yet implemented")
    }

    override fun deleteBill(id: Int): Bill? {
        TODO("Not yet implemented")
    }
}