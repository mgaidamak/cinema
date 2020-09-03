package org.mgaidamak.dao.cinema

import org.mgaidamak.dao.Cinema
import org.mgaidamak.dao.Page
import java.util.concurrent.atomic.AtomicInteger
import kotlin.streams.toList

/**
 * Not thread-safe repository for Hall application.
 */
class FileCinemaRepo: ICinemaRepo {

    val next = AtomicInteger()
    val map = HashMap<Int, Cinema>()

    override fun createCinema(cinema: Cinema): Cinema? {
        val newOne = cinema.copy(id = next.incrementAndGet())
        map[newOne.id] = newOne
        return newOne
    }

    override fun getCinemas(city: String?, page: Page, sort: List<String>): Collection<Cinema> {
        // TODO sorting
        return map.values.stream()
            .filter { c -> city?.let { c.city.contains(city, true) } ?: true }
            .skip(page.offset.toLong())
            .limit(page.limit.toLong()).toList()
    }

    override fun getCinemaById(id: Int): Cinema? {
        return map[id]
    }

    override fun deleteCinemaById(id: Int): Cinema? {
        return map.remove(id)
    }

    override fun clear() {
        map.clear()
    }

    override fun total(): Int {
        return map.size
    }
}