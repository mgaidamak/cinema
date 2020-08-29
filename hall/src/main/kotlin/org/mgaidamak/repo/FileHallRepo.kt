package org.mgaidamak.repo

import org.mgaidamak.Cinema
import org.mgaidamak.Page
import java.util.concurrent.atomic.AtomicLong
import kotlin.streams.toList

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
        return map.values.stream()
            .skip(page.offset)
            .limit(page.limit).toList()
    }

    override fun getCinemaById(id: Long): Cinema? {
        return map[id]
    }

    override fun deleteCinemaById(id: Long): Cinema? {
        return map.remove(id)
    }

    override fun clear() {
        map.clear()
    }

    override fun total(): Int {
        return map.size
    }
}