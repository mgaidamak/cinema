package org.mgaidamak.dao.hall

import org.mgaidamak.dao.Cinema
import org.mgaidamak.dao.Hall
import org.mgaidamak.dao.Page
import org.mgaidamak.dao.cinema.ICinemaRepo
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
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
        val first = repo.createHall(Hall(cinema = cinemaId, name = "Big"))
        val second = repo.createHall(Hall(cinema = cinemaId, name = "Small"))
        val expected = listOf(first, second)
        val found = repo.getHalls(cinemaId)
        assertTrue { found.containsAll(expected) }
        assertEquals(expected.size, found.size)
    }

    @Test
    fun `list halls page`() {
        val cinemaId = createCinema()
        repo.createHall(Hall(cinema = cinemaId, name = "Big"))
        val second = repo.createHall(Hall(cinema = cinemaId, name = "Small"))
        val expected = listOf(second)
        val found = repo.getHalls(cinemaId, page = Page(1, 2))
        assertTrue { found.containsAll(expected) }
        assertEquals(expected.size, found.size)
    }

    @Test
    fun `get hall`() {
        val cinemaId = createCinema()
        val first = assertNotNull(repo.createHall(Hall(cinema = cinemaId, name = "Big")))
        assertEquals(first, repo.getHallById(first.id))
    }

    @Test
    fun `get hall absent`() {
        val cinemaId = createCinema()
        val first = assertNotNull(repo.createHall(Hall(cinema = cinemaId, name = "Big")))
        assertNull(repo.getHallById(first.id - 1))
    }

    @Test
    fun `delete cinema`() {
        val cinemaId = createCinema()
        val first = assertNotNull(repo.createHall(Hall(cinema = cinemaId, name = "Big")))
        assertEquals(first, repo.deleteHallById(first.id))
    }

    @Test
    fun `delete cinema absent`() {
        val cinemaId = createCinema()
        val first = assertNotNull(repo.createHall(Hall(cinema = cinemaId, name = "Big")))
        assertNull(repo.deleteHallById(first.id - 1))
    }
}