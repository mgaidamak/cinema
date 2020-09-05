package org.mgaidamak.dao.hall

import org.mgaidamak.dao.Hall
import org.mgaidamak.dao.Page
import java.util.concurrent.atomic.AtomicInteger
import kotlin.streams.toList

/**
 * Not thread-safe repository for Hall application.
 */
class FileHallRepo: IHallRepo {

    val next = AtomicInteger()
    val map = HashMap<Int, Hall>()

    override fun createHall(hall: Hall): Hall? {
        val newOne = hall.copy(id = next.incrementAndGet())
        map[newOne.id] = newOne
        return newOne
    }

    override fun getHalls(cinema: Int, page: Page, sort: List<String>): Collection<Hall> {
        return map.values.stream()
            .filter { c -> c.cinema == cinema }
            .skip(page.offset.toLong())
            .limit(page.limit.toLong()).toList()
    }

    override fun getHallById(id: Int): Hall? {
        return map[id]
    }

    override fun deleteHallById(id: Int): Hall? {
        return map.remove(id)
    }

    override fun clear() {
        map.clear()
    }

    override fun total(): Int {
        return map.size
    }
}