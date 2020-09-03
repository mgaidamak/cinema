package org.mgaidamak.dao.cinema

import org.mgaidamak.dao.Cinema
import org.mgaidamak.dao.Page
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
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
        assertNotNull(repo.createCinema(cinema))
        assertNotNull(repo.createCinema(cinema))
        assertEquals(2, repo.total())
    }

    @Test
    fun `list cinemas`() {
        val cinema = Cinema(name = "Pobeda", city = "Novosibirsk",
            address = "Lenina 1", timezone = "Asia/Novosibirsk")
        val first = assertNotNull(repo.createCinema(cinema))
        val second = assertNotNull(repo.createCinema(cinema))
        val expected = listOf(first, second)
        val found = repo.getCinemas()
        assertTrue { found.containsAll(expected) }
        assertEquals(expected.size, found.size)
    }

    @Test
    fun `list cinemas page`() {
        val cinema = Cinema(name = "Pobeda", city = "Novosibirsk",
            address = "Lenina 1", timezone = "Asia/Novosibirsk")
        assertNotNull(repo.createCinema(cinema))
        val second = assertNotNull(repo.createCinema(cinema))
        val expected = listOf(second)
        val found = repo.getCinemas(page = Page(1, 2))
        assertTrue { found.containsAll(expected) }
        assertEquals(expected.size, found.size)
    }

    @Test
    fun `list cinemas city`() {
        val nsk = Cinema(name = "Pobeda", city = "Novosibirsk",
            address = "Lenina 1", timezone = "Asia/Novosibirsk")
        val first = assertNotNull(repo.createCinema(nsk))
        val tmsk = Cinema(name = "Cosmos", city = "Tomsk",
            address = "Lenina 2", timezone = "Asia/Novosibirsk")
        val second = assertNotNull(repo.createCinema(tmsk))
        val expected = listOf(second)
        val found = repo.getCinemas("Tomsk")
        assertTrue { found.containsAll(expected) }
        assertEquals(expected.size, found.size)
    }

    @Test
    fun `get cinema`() {
        val cinema = Cinema(name = "Pobeda", city = "Novosibirsk",
            address = "Lenina 1", timezone = "Asia/Novosibirsk")
        val first = assertNotNull(repo.createCinema(cinema))
        assertEquals(first, repo.getCinemaById(first.id))
    }

    @Test
    fun `get cinema absent`() {
        val cinema = Cinema(name = "Pobeda", city = "Novosibirsk",
            address = "Lenina 1", timezone = "Asia/Novosibirsk")
        val first = assertNotNull(repo.createCinema(cinema))
        assertNull(repo.getCinemaById(first.id - 1))
    }

    @Test
    fun `delete cinema`() {
        val cinema = Cinema(name = "Pobeda", city = "Novosibirsk",
            address = "Lenina 1", timezone = "Asia/Novosibirsk")
        val first = assertNotNull(repo.createCinema(cinema))
        assertEquals(first, repo.deleteCinemaById(first.id))
    }

    @Test
    fun `delete cinema absent`() {
        val cinema = Cinema(name = "Pobeda", city = "Novosibirsk",
            address = "Lenina 1", timezone = "Asia/Novosibirsk")
        val first = assertNotNull(repo.createCinema(cinema))
        assertNull(repo.deleteCinemaById(first.id - 1))
    }
}