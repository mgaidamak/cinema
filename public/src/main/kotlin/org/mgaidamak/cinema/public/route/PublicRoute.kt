package org.mgaidamak.cinema.public.route

import io.ktor.http.HttpStatusCode
import org.mgaidamak.cinema.public.dao.AdminBill
import org.mgaidamak.cinema.public.dao.AdminTicket
import org.mgaidamak.cinema.public.dao.Bill
import org.mgaidamak.cinema.public.dao.Cinema
import org.mgaidamak.cinema.public.dao.IPublicRepo
import org.mgaidamak.cinema.public.dao.Seat
import org.mgaidamak.cinema.public.dao.Session
import org.mgaidamak.cinema.public.response.Response
import java.time.LocalDate

/**
 * Route collect data from different APIs and return data for front-end
 */
class PublicRoute(private val repo: IPublicRepo): IPublicRoute {

    /**
     * 1. Get cinema list from admin API.
     * 2. Collect to user-friendly Cinema
     * @param city City
     * @return Response with Cinema's
     */
    override suspend fun getCinemas(city: String?): Response<Collection<Cinema>> {
        val r = repo.getCinemas(city)
        // TODO don't transfer encapsulated data to public
        return Response(r.code, r.message, r.data?.map { Cinema(it) })
    }

    /**
     * 1. Get halls of cinema
     * 2. Get sessions at halls
     * 3. Load film name to session
     * 4. Collect to user-friendly Session
     * @param cinema Cinema ID
     * @param date Date of session
     * @return Response with Session's
     */
    override suspend fun getSessions(cinema: Int, date: LocalDate): Response<Collection<Session>> {
        val hallsRes = repo.getHalls(cinema)
        if (!hallsRes.succeed()) {
            println("Error while load halls of cinema $cinema: ${hallsRes.code}:${hallsRes.message}")
            return Response(HttpStatusCode.InternalServerError,
                "Error while load halls of cinema $cinema")
        }

        val halls = hallsRes.data?.map{ it.id }?.toIntArray() ?: IntArray(0)
        if (halls.isEmpty()) {
            return Response(HttpStatusCode.OK,
                "No halls available at cinema $cinema",
                emptyList())
        }

        // TODO process date
        val sessions = repo.getSessions(halls)
        if (!sessions.succeed()) {
            println("Error while load session of hall $halls: ${sessions.code}:${sessions.message}")
            return Response(HttpStatusCode.InternalServerError,
                "Error while load session at cinema $cinema")
        }

        if (sessions.data == null || sessions.data.isEmpty()) {
            return Response(HttpStatusCode.OK,
                "No sessions available at cinema $cinema",
                emptyList())
        }

        val result = ArrayList<Session>(sessions.data.size)
        // it can be global application hashmap
        val films = HashMap<Int, String>()
        // Enrich with film name
        for (session in sessions.data) {
            val id = session.film
            var film = films[id]
            if (film == null) {
                // TODO call suspend fun in FP style
                // TODO don't use 'var'
                val filmRes = repo.getFilm(id)
                film = filmRes.data?.let {
                    films[id] = it.name
                    it.name
                } ?: let {
                    println("Error while get name of film $id: ${filmRes.code}:${filmRes.message}")
                    ""
                }
            }
            result.add(Session(session, film))
        }
        return Response(data = result)
    }

    override suspend fun getSeats(session: Int): Response<Collection<Seat>> {
        val sesRes = repo.getSession(session)
        if (!sesRes.succeed() || sesRes.data == null) {
            return Response(HttpStatusCode.InternalServerError,
                "Error while load session $session")
        }

        val seatsRes = repo.getSeats(sesRes.data.hall)
        if (!seatsRes.succeed()) {
            return Response(HttpStatusCode.InternalServerError,
                "Error while load hall plan for session $session")
        }

        if (seatsRes.data == null || seatsRes.data.isEmpty()) {
            return Response(HttpStatusCode.OK,
                "No seats available for session $session")
        }

        val soldRes = repo.getTickets(session)
        if (!soldRes.succeed()) {
            return Response(HttpStatusCode.InternalServerError,
                "Error while load sold tickets for session $session")
        }

        val sold = soldRes.data?.map { it.seat to it.status }?.toMap() ?: emptyMap()
        // Collect to hall map with status
        return Response(data = seatsRes.data.map { Seat(it, sold.getOrDefault(it.id, 0)) })
    }

    override suspend fun postBill(bill: Bill): Response<Bill> {
        // Post new bill
        val billBody = AdminBill(customer = bill.customer, session = bill.session,
            status = 0, total = bill.total)
        val newBillRes = repo.createBill(billBody)
        if (!newBillRes.succeed()) {
            return Response(newBillRes.code, newBillRes.message)
        }
        if (newBillRes.data == null) {
            return Response(HttpStatusCode.InternalServerError, "Sorry, bill has not been created!")
        }

        // Post tickets
        val soldSeats = ArrayList<Seat>(bill.seats.size)
        for (seat in bill.seats) {
            val ticketBody = AdminTicket(bill = newBillRes.data.id, session = bill.session,
                seat = seat.id, status = 2)
            val ticketRes = repo.createTicket(ticketBody)
            if (!ticketRes.succeed()) {
                return Response(newBillRes.code, newBillRes.message)
            }

            soldSeats.add(seat.copy(status = 2))
        }
        // Confirm bill status
        val patchBody = newBillRes.data.copy(status = 1)
        val patchBillRes = repo.patchBill(patchBody)
        if (!patchBillRes.succeed()) {
            return Response(patchBillRes.code, patchBillRes.message)
        }
        if (patchBillRes.data == null) {
            return Response(HttpStatusCode.InternalServerError, "Sorry, unknown bill status")
        }

        return Response(data = Bill(patchBillRes.data, soldSeats))
    }

    override suspend fun getBill(id: Int): Response<Bill> {
        // Show bill status
        val billRes = repo.getBill(id)
        if (!billRes.succeed()) {
            return Response(billRes.code, billRes.message)
        }
        if (billRes.data == null) {
            return Response(HttpStatusCode.InternalServerError, "Bill not found by $id")
        }

        // Show payed tickets
        val ticketsRes = repo.getBillTickets(id)
        if (!ticketsRes.succeed()) {
            return Response(ticketsRes.code, ticketsRes.message)
        }
        if (ticketsRes.data == null) {
            return Response(HttpStatusCode.InternalServerError, "Sorry, tickets are not available!")
        }

        val seats = ArrayList<Seat>(ticketsRes.data.size)
        for (adminTicket in ticketsRes.data) {
            // Get seat information
            val seatRes = repo.getSeat(adminTicket.seat)
            val seat = seatRes.data?.let {
                Seat(it, adminTicket.status)
            } ?: let {
                println("Error while get seat $id: ${seatRes.code}:${seatRes.message}")
                Seat(adminTicket.seat, 0, 0, adminTicket.status)
            }
            seats.add(seat)
        }

        return Response(data = Bill(billRes.data, seats))
    }

    override suspend fun deleteBill(id: Int): Response<Bill> {
        val billRes = repo.getBill(id)
        if (!billRes.succeed()) {
            return Response(billRes.code, billRes.message)
        }
        if (billRes.data == null) {
            return Response(HttpStatusCode.InternalServerError, "Bill not found by $id")
        }

        val adminTicketsRes = repo.deleteBillTickets(id)
        if (!adminTicketsRes.succeed()) {
            return Response(adminTicketsRes.code, adminTicketsRes.message)
        }

        val seats = adminTicketsRes.data?.map { Seat(it.seat, 0, 0, 0) } ?: let {
            println("Error while get removed tickets by bill $id")
            emptyList()
        }
        val patchBody = billRes.data.copy(status = 3)
        val patchBillRes = repo.patchBill(patchBody)
        if (!patchBillRes.succeed()) {
            return Response(patchBillRes.code, patchBillRes.message)
        }
        if (patchBillRes.data == null) {
            return Response(HttpStatusCode.InternalServerError, "Sorry, unknown bill status")
        }
        return Response(data = Bill(patchBillRes.data, seats))
    }
}