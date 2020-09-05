package org.mgaidamak.dao.seat

import org.mgaidamak.dao.Page
import org.mgaidamak.dao.Seat
import java.util.concurrent.atomic.AtomicInteger
import kotlin.streams.toList

/**
 * Not thread-safe repository for Hall application.
 */
class FileSeatRepo: ISeatRepo {

    val next = AtomicInteger()
    val map = HashMap<Int, Seat>()

    override fun createSeat(seat: Seat): Seat? {
        if (map.values.stream().anyMatch { s -> s.x == seat.x && s.y == seat.y }) {
            return null
        } else {
            val newOne = seat.copy(id = next.incrementAndGet())
            map[newOne.id] = newOne
            return newOne
        }
    }

    override fun getSeats(hall: Int, page: Page): Collection<Seat> {
        return map.values.stream()
            .filter { c -> c.hall == hall }
            .skip(page.offset.toLong())
            .limit(page.limit.toLong()).toList()
    }

    override fun getSeatById(id: Int): Seat? {
        return map[id]
    }

    override fun deleteSeatById(id: Int): Seat? {
        return map.remove(id)
    }

    override fun clear() {
        map.clear()
    }

    override fun total(): Int {
        return map.size
    }
}