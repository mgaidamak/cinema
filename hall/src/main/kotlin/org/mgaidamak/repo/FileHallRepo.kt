package org.mgaidamak.repo

import org.mgaidamak.Cinema
import org.mgaidamak.Page
import java.util.concurrent.atomic.AtomicLong

/**
 * Not thread-safe repository for Hall application.
 */
class FileHallRepo: IHallRepo {

    val next = AtomicLong()
    val map = HashMap<Long, Cinema>()

    override fun createCinema(cinema: Cinema): Cinema {
        val newOne = cinema.copy(id = next.incrementAndGet())
        map[newOne.id] = newOne
        return newOne
    }

    override fun getCinemas(page: Page, sort: List<String>): Collection<Cinema> {
        // TODO sorting
        // TODO pagination
        return map.values
    }

    override fun getCinemaById(id: Long): Cinema? {
        return map[id]
    }

    override fun deleteCinemaById(id: Long): Cinema? {
        return map.remove(id)
    }
}