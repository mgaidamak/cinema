package org.mgaidamak.dao.cinema

import org.mgaidamak.dao.Cinema
import org.mgaidamak.dao.Page
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * In this class we test, that all methods of repo work in concert
 */
abstract class ICinemaRepoTest(private val repo: ICinemaRepo) {
    @BeforeTest
    fun `clean up`() {
        repo.clear()
    }

    @Test
    open fun `create cinema`() {
        val cinema = Cinema(name = "Pobeda", city = "Novosibirsk",
            address = "Lenina 1", timezone = "Asia/Novosibirsk")
        repo.createCinema(cinema)
        repo.createCinema(cinema)
        assertEquals(2, repo.total())
    }

    @Test
    fun `list cinemas`() {
        val cinema = Cinema(name = "Pobeda", city = "Novosibirsk",
            address = "Lenina 1", timezone = "Asia/Novosibirsk")
        val first = repo.createCinema(cinema)
        println("Created first $first")
        val second = repo.createCinema(cinema)
        println("Created second $second")
        val expected = listOf(first, second)
        val found = repo.getCinemas()
        println("Found list $found")
        assertTrue { found.containsAll(expected) }
        assertEquals(expected.size, found.size)
    }

    @Test
    fun `list cinemas page`() {
        val cinema = Cinema(name = "Pobeda", city = "Novosibirsk",
            address = "Lenina 1", timezone = "Asia/Novosibirsk")
        val first = repo.createCinema(cinema)
        println("Created first $first")
        val second = repo.createCinema(cinema)
        println("Created first $second")
        val expected = listOf(second)
        val found = repo.getCinemas(page = Page(1, 2))
        assertTrue { found.containsAll(expected) }
        assertEquals(expected.size, found.size)
    }

    @Test
    fun `get cinema`() {
        val cinema = Cinema(name = "Pobeda", city = "Novosibirsk",
            address = "Lenina 1", timezone = "Asia/Novosibirsk")
        val newOne = repo.createCinema(cinema)
        assertEquals(newOne, repo.getCinemaById(newOne?.id ?: 0))
    }

    @Test
    fun `get cinema absent`() {
        val cinema = Cinema(name = "Pobeda", city = "Novosibirsk",
            address = "Lenina 1", timezone = "Asia/Novosibirsk")
        val newOne = repo.createCinema(cinema)?.id ?: 0
        assertNull(repo.getCinemaById(newOne - 1))
    }

    @Test
    fun `delete cinema`() {
        val cinema = Cinema(name = "Pobeda", city = "Novosibirsk",
            address = "Lenina 1", timezone = "Asia/Novosibirsk")
        val newOne = repo.createCinema(cinema)
        assertEquals(newOne, repo.deleteCinemaById(newOne?.id ?: 0))
    }

    @Test
    fun `delete cinema absent`() {
        val cinema = Cinema(name = "Pobeda", city = "Novosibirsk",
            address = "Lenina 1", timezone = "Asia/Novosibirsk")
        val newOne = repo.createCinema(cinema)?.id ?: 0
        assertNull(repo.deleteCinemaById(newOne - 1))
    }
}