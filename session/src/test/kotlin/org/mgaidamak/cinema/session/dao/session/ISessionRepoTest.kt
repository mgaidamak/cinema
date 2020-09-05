package org.mgaidamak.cinema.session.dao.session

import org.mgaidamak.cinema.session.dao.Film
import org.mgaidamak.cinema.session.dao.Page
import org.mgaidamak.cinema.session.dao.Session
import org.mgaidamak.cinema.session.dao.film.IFilmRepo
import java.time.ZoneId
import java.time.ZonedDateTime
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * In this class we test, that all methods of repo work in concert
 */
abstract class ISessionRepoTest(private val repo: ISessionRepo,
                                private val frepo: IFilmRepo) {
    @BeforeTest
    fun `clean up`() {
        frepo.clear()
    }

    private fun createFilm(): Int {
        val film = Film(name = "Sudba cheloveka")
        return assertNotNull(frepo.createFilm(film)).id
    }

    @Test
    open fun `create session`() {
        val filmId = createFilm()
        assertNotNull(repo.createSession(Session(film = filmId, hall = 7,
            date = ZonedDateTime.now(ZoneId.systemDefault()), price = 100)))
        assertEquals(1, repo.total())
    }

    @Test
    fun `list session`() {
        val filmId = createFilm()
        val first = assertNotNull(repo.createSession(Session(film = filmId, hall = 7,
            date = ZonedDateTime.now(ZoneId.systemDefault()), price = 100)))
        val second = assertNotNull(repo.createSession(Session(film = filmId, hall = 88,
            date = ZonedDateTime.now(ZoneId.systemDefault()), price = 100)))
        assertNotNull(repo.createSession(Session(film = filmId, hall = 9,
            date = ZonedDateTime.now(ZoneId.systemDefault()), price = 100)))
        val expected = listOf(first, second)
        val found = repo.getSessions(filmId, arrayOf(7, 88))
        assertTrue { found.containsAll(expected) }
        assertEquals(expected.size, found.size)
    }

    @Test
    fun `list session page`() {
        val filmId = createFilm()
        assertNotNull(repo.createSession(Session(film = filmId, hall = 7,
            date = ZonedDateTime.now(ZoneId.systemDefault()), price = 100)))
        val second = assertNotNull(repo.createSession(Session(film = filmId, hall = 88,
            date = ZonedDateTime.now(ZoneId.systemDefault()), price = 100)))
        assertNotNull(repo.createSession(Session(film = filmId, hall = 88,
            date = ZonedDateTime.now(ZoneId.systemDefault()), price = 100)))
        val expected = listOf(second)
        val found = repo.getSessions(filmId, arrayOf(88), Page(0, 1))
        assertTrue { found.containsAll(expected) }
        assertEquals(expected.size, found.size)
    }

    @Test
    fun `get session`() {
        val filmId = createFilm()
        val first = assertNotNull(repo.createSession(Session(film = filmId, hall = 7,
            date = ZonedDateTime.now(ZoneId.systemDefault()), price = 100)))
        assertEquals(first, repo.getSessionById(first.id))
    }

    @Test
    fun `get session absent`() {
        val filmId = createFilm()
        val first = assertNotNull(repo.createSession(Session(film = filmId, hall = 7,
            date = ZonedDateTime.now(ZoneId.systemDefault()), price = 100)))
        assertNull(repo.getSessionById(first.id - 1))
    }

    @Test
    fun `delete session`() {
        val filmId = createFilm()
        val first = assertNotNull(repo.createSession(Session(film = filmId, hall = 7,
            date = ZonedDateTime.now(ZoneId.systemDefault()), price = 100)))
        assertEquals(first, repo.deleteSessionById(first.id))
    }

    @Test
    fun `delete session absent`() {
        val filmId = createFilm()
        val first = assertNotNull(repo.createSession(Session(film = filmId, hall = 7,
            date = ZonedDateTime.now(ZoneId.systemDefault()), price = 100)))
        assertNull(repo.deleteSessionById(first.id - 1))
    }
}