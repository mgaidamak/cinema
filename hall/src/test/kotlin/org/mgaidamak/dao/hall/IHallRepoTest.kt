package org.mgaidamak.dao.hall

import org.mgaidamak.dao.Cinema
import org.mgaidamak.dao.Hall
import org.mgaidamak.dao.Page
import org.mgaidamak.dao.cinema.ICinemaRepo
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * In this class we test, that all methods of repo work in concert
 */
abstract class IHallRepoTest(private val repo: IHallRepo,
                             private val crepo: ICinemaRepo) {
    @BeforeTest
    fun `clean up`() {
        crepo.clear()
        repo.clear()
    }

    private fun createCinema(): Int {
        val cinema = Cinema(name = "Pobeda", city = "Novosibirsk",
            address = "Lenina 1", timezone = "Asia/Novosibirsk")
        return crepo.createCinema(cinema)?.id ?: 0
    }

    @Test
    open fun `create hall`() {
        val cinemaId = createCinema()
        repo.createHall(Hall(cinema = cinemaId, name = "Big"))
        assertEquals(1, repo.total())
    }

    @Test
    fun `list halls`() {
        val cinemaId = createCinema()
        val newOne = repo.createHall(Hall(cinema = cinemaId, name = "Big"))
        val newTwo = repo.createHall(Hall(cinema = cinemaId, name = "Small"))
        val expected = listOf(newOne, newTwo)
        println(expected)
        val found = repo.getHalls(cinemaId)
        println(found)
        assertTrue { found.containsAll(expected) }
        assertEquals(expected.size, found.size)
    }

    @Test
    fun `list halls page`() {
        val cinemaId = createCinema()
        val hall1 = Hall(cinema = cinemaId, name = "Big")
        val hall2 = Hall(cinema = cinemaId, name = "Small")
        repo.createHall(hall1)
        val newTwo = repo.createHall(hall2)
        val expected = listOf(newTwo)
        val found = repo.getHalls(cinemaId, page = Page(1, 2))
        assertTrue { found.containsAll(expected) }
        assertEquals(expected.size, found.size)
    }

    @Test
    fun `get hall`() {
        val cinemaId = createCinema()
        val newOne = repo.createHall(Hall(cinema = cinemaId, name = "Big"))
        assertEquals(newOne, repo.getHallById(newOne?.id ?: -1))
    }

    @Test
    fun `get hall absent`() {
        val cinemaId = createCinema()
        val newOne = repo.createHall(Hall(cinema = cinemaId, name = "Big"))
        assertNull(repo.getHallById(newOne?.let { it.id - 1 } ?: -1))
    }

    @Test
    fun `delete cinema`() {
        val cinemaId = createCinema()
        val newOne = repo.createHall(Hall(cinema = cinemaId, name = "Big"))
        assertEquals(newOne, repo.deleteHallById(newOne?.id ?: -1))
    }

    @Test
    fun `delete cinema absent`() {
        val cinemaId = createCinema()
        val newOne = repo.createHall(Hall(cinema = cinemaId, name = "Big"))
        assertNull(repo.deleteHallById(newOne?.let { it.id - 1 } ?: -1))
    }
}