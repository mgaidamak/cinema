package org.mgaidamak.cinema.ticket.dao.seat

import org.mgaidamak.cinema.ticket.dao.Seat

/**
 * DAO for Seat list
 */
interface ISeatRepo {
    fun createSeat(order: Seat): Seat?
    fun getSeats(session: Int): Collection<Seat>
    fun getSeat(session: Int, seat: Int): Seat?
    fun deleteSeatById(id: Int): Seat?
    fun deleteSeatByOrder(order: Int): Collection<Seat>
    fun clear()
    fun total(): Int
}