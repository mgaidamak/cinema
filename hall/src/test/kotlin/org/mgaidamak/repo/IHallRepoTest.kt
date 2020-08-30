package org.mgaidamak.repo

import org.mgaidamak.Cinema
import org.mgaidamak.Page
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * In this class we test, that all methods of repo work in concert
 */
abstract class IHallRepoTest(private val repo: IHallRepo) {
    @BeforeTest
    fun `clean up`() {
        repo.clear()
    }

    @Test
    open fun `create cinema`() {
        val cinema = Cinema(name = "Pobeda", city = "Novosibirsk",
            address = "Lenina 1", timezone = "Asia/Novosibirsk")
        val newOne = repo.createCinema(cinema)
        val newTwo = repo.createCinema(cinema)
        assertEquals(newTwo.id, newOne.id + 1)
        assertEquals(2, repo.total())
    }

    @Test
    fun `list cinemas`() {
        val cinema = Cinema(name = "Pobeda", city = "Novosibirsk",
            address = "Lenina 1", timezone = "Asia/Novosibirsk")
        val newOne = repo.createCinema(cinema)
        val newTwo = repo.createCinema(cinema)
        val expected = listOf(newOne, newTwo)
        val found = repo.getCinemas()
        assertTrue { expected.containsAll(found) }
        assertEquals(expected.size, found.size)
    }

    @Test
    fun `list cinemas page`() {
        val cinema = Cinema(name = "Pobeda", city = "Novosibirsk",
            address = "Lenina 1", timezone = "Asia/Novosibirsk")
        repo.createCinema(cinema)
        val newTwo = repo.createCinema(cinema)
        val expected = listOf(newTwo)
        val found = repo.getCinemas(page = Page(1, 2))
        assertTrue { expected.containsAll(found) }
        assertEquals(expected.size, found.size)
    }

    @Test
    fun `get cinema`() {
        val cinema = Cinema(name = "Pobeda", city = "Novosibirsk",
            address = "Lenina 1", timezone = "Asia/Novosibirsk")
        val newOne = repo.createCinema(cinema)
        assertEquals(newOne, repo.getCinemaById(newOne.id))
    }

    @Test
    fun `get cinema absent`() {
        val cinema = Cinema(name = "Pobeda", city = "Novosibirsk",
            address = "Lenina 1", timezone = "Asia/Novosibirsk")
        val newOne = repo.createCinema(cinema)
        assertNull(repo.getCinemaById(newOne.id + 1))
    }

    @Test
    fun `delete cinema`() {
        val cinema = Cinema(name = "Pobeda", city = "Novosibirsk",
            address = "Lenina 1", timezone = "Asia/Novosibirsk")
        val newOne = repo.createCinema(cinema)
        assertEquals(newOne, repo.deleteCinemaById(newOne.id))
    }

    @Test
    fun `delete cinema absent`() {
        val cinema = Cinema(name = "Pobeda", city = "Novosibirsk",
            address = "Lenina 1", timezone = "Asia/Novosibirsk")
        val newOne = repo.createCinema(cinema)
        assertNull(repo.deleteCinemaById(newOne.id + 1))
    }
}