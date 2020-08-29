package org.mgaidamak.repo

import org.mgaidamak.Cinema
import org.mgaidamak.Page

interface IHallRepo {
    fun createCinema(cinema: Cinema): Cinema
    fun getCinemas(page: Page = Page(0, 10),
                   sort: List<String> = emptyList()): Collection<Cinema>
    fun getCinemaById(id: Long): Cinema?
    fun deleteCinemaById(id: Long): Cinema?
    fun clear()
    fun total(): Int
}