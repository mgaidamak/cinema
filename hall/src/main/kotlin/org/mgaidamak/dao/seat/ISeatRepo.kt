package org.mgaidamak.dao.seat

import org.mgaidamak.dao.Seat
import org.mgaidamak.dao.Page

/**
 * DAO for Seat at Hall list
 */
interface ISeatRepo {
    fun createSeat(seat: Seat): Seat?
    fun getSeats(hall: Int,
                 page: Page = Page(0, 10)): Collection<Seat>
    fun deleteSeatById(id: Int): Seat?
    fun clear()
    fun total(): Int
}