package org.mgaidamak.repo

import org.mgaidamak.Cinema
import org.mgaidamak.Page

interface IHallRepo {
    fun createCinema(cinema: Cinema): Cinema
    fun getCinemas(page: Page, sort: List<String>): Collection<Cinema>
    fun getCinemaById(id: Long): Cinema?
    fun deleteCinemaById(id: Long): Cinema?
}