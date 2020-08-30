package org.mgaidamak.dao.cinema

import org.mgaidamak.dao.Cinema
import org.mgaidamak.dao.Page

/**
 * DAO for Cinema list
 */
interface ICinemaRepo {
    fun createCinema(cinema: Cinema): Cinema?
    fun getCinemas(page: Page = Page(0, 10),
                   sort: List<String> = emptyList()): Collection<Cinema>
    fun getCinemaById(id: Int): Cinema?
    fun deleteCinemaById(id: Int): Cinema?
    fun clear()
    fun total(): Int
}