package org.mgaidamak.cinema.public.route

import org.mgaidamak.cinema.public.dao.AdminBill
import org.mgaidamak.cinema.public.dao.AdminTicket
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
        val sold = repo.getTickets(session).map { it.seat to it.status }.toMap()
        // Collect to hall map with status
        return seats.map { Seat(it, sold.getOrDefault(it.id, 0)) }
    }

    override suspend fun postBill(bill: Bill): Bill? {
        // Post new bill
        val billBody = AdminBill(customer = bill.customer, session = bill.session,
            status = 0, total = bill.total)
        val newBill = repo.createBill(billBody)
        // Post tickets
        val soldSeats = ArrayList<Seat>(bill.seats.size)
        for (seat in bill.seats) {
            val ticketBody = AdminTicket(bill = newBill.id, session = bill.session,
                seat = seat.id, status = 2)
            repo.createTicket(ticketBody)
            soldSeats.add(seat.copy(status = 2))
        }
        // Confirm bill status
        val patchBody = newBill.copy(status = 1)
        val patchBill = repo.patchBill(patchBody)
        return Bill(patchBill, soldSeats)
    }

    override suspend fun getBill(id: Int): Bill? {
        // Show bill status
        val bill = repo.getBill(id)
        // Show payed tickets
        val tickets = repo.getBillTickets(id)
        val seats = ArrayList<Seat>(tickets.size)
        for (adminTicket in tickets) {
            // Get seat information
            repo.getSeat(adminTicket.seat).data?.apply {
                seats.add(Seat(this, adminTicket.status))
            }
        }
        return Bill(bill, seats)
    }

    override suspend fun deleteBill(id: Int): Bill? {
        val bill = repo.getBill(id)
        val adminTickets = repo.deleteBillTickets(id)
        val seats = adminTickets.map { Seat(it.seat, 0, 0, 0) }
        val patchBody = bill.copy(status = 3)
        val patchBill = repo.patchBill(patchBody)
        return Bill(patchBill, seats)
    }
}