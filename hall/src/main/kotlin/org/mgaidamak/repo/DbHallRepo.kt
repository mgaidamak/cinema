package org.mgaidamak.repo

import org.mgaidamak.Cinema
import org.mgaidamak.Page

/**
 * Production ready repository on PostgreSQL
 */
class DbHallRepo: IHallRepo {
    override fun createCinema(cinema: Cinema): Cinema {
        TODO("Not yet implemented")
    }

    override fun getCinemas(page: Page, sort: List<String>): Collection<Cinema> {
        TODO("Not yet implemented")
    }

    override fun getCinemaById(id: Long): Cinema? {
        TODO("Not yet implemented")
    }

    override fun deleteCinemaById(id: Long): Cinema? {
        TODO("Not yet implemented")
    }

    override fun clear() {
        TODO("Not yet implemented")
    }

    override fun total(): Int {
        TODO("Not yet implemented")
    }
}