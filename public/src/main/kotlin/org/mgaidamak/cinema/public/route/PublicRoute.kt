package org.mgaidamak.cinema.public.route

import org.mgaidamak.cinema.public.dao.Bill
import org.mgaidamak.cinema.public.dao.Cinema
import org.mgaidamak.cinema.public.dao.IPublicRepo
import org.mgaidamak.cinema.public.dao.Seat
import org.mgaidamak.cinema.public.dao.Session
import java.time.LocalDate

class PublicRoute(val repo: IPublicRepo): IPublicRoute {
    override suspend fun getCinemas(city: String?): Collection<Cinema> {
        return repo.getCinemas(city).map { Cinema(it) }
    }

    override suspend fun getSessions(cinema: Int, date: LocalDate): Collection<Session> {
        val hlist = repo.getHalls(cinema)
        val harray = hlist.map{ it.id }.toIntArray()
        val sessions = repo.getSessions(harray)
        val result = ArrayList<Session>(sessions.size)
        // it can be global application hashmap
        val films = HashMap<Int, String>()
        // Enrich with film name
        for (session in sessions) {
            val id = session.film
            var film = films[id]
            if (film == null) {
                // TODO call suspend fun in FP style
                film = repo.getFilm(id).let {
                    films[id] = it.name
                    it.name
                }
            }
            result.add(Session(session, film))
        }
        return result
    }

    override suspend fun getSeats(session: Int): Collection<Seat> {
        val sessionObj = repo.getSession(session)
        val seats = repo.getSeats(sessionObj.hall)
        val sold = repo.getTockets(session).map { it.seat to it.status }.toMap()
        // Collect to hall map with status
        return seats.map { Seat(it, sold.getOrDefault(it.id, 0)) }
    }

    override suspend fun postBill(bill: Bill): Bill? {
        TODO("Not yet implemented")
    }

    override suspend fun getBill(id: Int): Bill? {
        TODO("Not yet implemented")
    }

    override suspend fun deleteBill(id: Int): Bill? {
        TODO("Not yet implemented")
    }
}