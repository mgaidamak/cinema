package org.mgaidamak.dao.seat

import org.mgaidamak.dao.Cinema
import org.mgaidamak.dao.Hall
import org.mgaidamak.dao.Page
import org.mgaidamak.dao.Seat
import org.mgaidamak.dao.cinema.ICinemaRepo
import org.mgaidamak.dao.hall.IHallRepo
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * In this class we test, that all methods of repo work in concert
 */
abstract class ISeatRepoTest(private val repo: ISeatRepo,
                             private val hrepo: IHallRepo,
                             private val crepo: ICinemaRepo) {
    @BeforeTest
    fun `clean up`() {
        crepo.clear()
    }

    private fun createCinema(): Int {
        val cinema = Cinema(name = "Pobeda", city = "Novosibirsk",
            address = "Lenina 1", timezone = "Asia/Novosibirsk")
        return assertNotNull(crepo.createCinema(cinema)).id
    }

    private fun createHall(cinema: Int): Int {
        val hall = Hall(cinema = cinema, name = "Big")
        return assertNotNull(hrepo.createHall(hall)).id
    }

    @Test
    open fun `create seat`() {
        val cinemaId = createCinema()
        val hallId = createHall(cinemaId)
        assertNotNull(repo.createSeat(Seat(hall = hallId, x = 1, y = 1)))
        assertNotNull(repo.createSeat(Seat(hall = hallId, x = 1, y = 2)))
        assertEquals(2, repo.total())
    }

    @Test
    open fun `create seat unique failed`() {
        val cinemaId = createCinema()
        val hallId = createHall(cinemaId)
        assertNotNull(repo.createSeat(Seat(hall = hallId, x = 1, y = 1)))
        assertNull(repo.createSeat(Seat(hall = hallId, x = 1, y = 1)))
        assertEquals(1, repo.total())
    }

    @Test
    fun `list seats`() {
        val cinemaId = createCinema()
        val hallId = createHall(cinemaId)
        val first = assertNotNull(repo.createSeat(Seat(hall = hallId, x = 1, y = 1)))
        val second = assertNotNull(repo.createSeat(Seat(hall = hallId, x = 1, y = 2)))
        val expected = listOf(first, second)
        val found = repo.getSeats(hallId)
        assertTrue { found.containsAll(expected) }
        assertEquals(expected.size, found.size)
    }

    @Test
    fun `list seats page`() {
        val cinemaId = createCinema()
        val hallId = createHall(cinemaId)
        assertNotNull(repo.createSeat(Seat(hall = hallId, x = 1, y = 1)))
        val second = assertNotNull(repo.createSeat(Seat(hall = hallId, x = 1, y = 2)))
        val expected = listOf(second)
        val found = repo.getSeats(hallId, page = Page(1, 2))
        assertTrue { found.containsAll(expected) }
        assertEquals(expected.size, found.size)
    }

    @Test
    fun `delete seat`() {
        val cinemaId = createCinema()
        val hallId = createHall(cinemaId)
        val first = assertNotNull(repo.createSeat(Seat(hall = hallId, x = 1, y = 1)))
        assertEquals(first, repo.deleteSeatById(first.id))
    }

    @Test
    fun `delete seat absent`() {
        val cinemaId = createCinema()
        val hallId = createHall(cinemaId)
        val first = assertNotNull(repo.createSeat(Seat(hall = hallId, x = 1, y = 1)))
        assertNull(repo.deleteSeatById(first.id - 1))
    }
}