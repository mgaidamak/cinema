package org.mgaidamak.cinema.ticket.dao.ticket

import org.mgaidamak.cinema.ticket.dao.Ticket

/**
 * DAO for Seat list
 */
interface ITicketRepo {
    fun createTicket(ticket: Ticket): Ticket?
    fun getTickets(session: Int?, bill: Int?): Collection<Ticket>
    fun getTicket(session: Int, seat: Int): Ticket?
    fun deleteTicketById(id: Int): Ticket?
    fun deleteTicketByBill(bill: Int): Collection<Ticket>
    fun clear()
    fun total(): Int
}