package org.mgaidamak.cinema.session.dao.film

import org.mgaidamak.cinema.session.dao.Film
import org.mgaidamak.cinema.session.dao.Page
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * In this class we test, that all methods of repo work in concert
 */
abstract class IFilmRepoTest(private val repo: IFilmRepo) {
    @BeforeTest
    fun `clean up`() {
        repo.clear()
    }

    @Test
    open fun `create film`() {
        val film = Film(name = "Sudba cheloveka")
        assertNotNull(repo.createFilm(film))
        assertNotNull(repo.createFilm(film))
        assertEquals(2, repo.total())
    }

    @Test
    fun `list films`() {
        val film = Film(name = "Sudba cheloveka")
        val first = assertNotNull(repo.createFilm(film))
        val second = assertNotNull(repo.createFilm(film))
        val expected = listOf(first, second)
        val found = repo.getFilms()
        assertTrue { found.containsAll(expected) }
        assertEquals(expected.size, found.size)
    }

    @Test
    fun `list films page`() {
        val film = Film(name = "Sudba cheloveka")
        assertNotNull(repo.createFilm(film))
        val second = assertNotNull(repo.createFilm(film))
        val expected = listOf(second)
        val found = repo.getFilms(page = Page(1, 2), name = "chelovek")
        assertTrue { found.containsAll(expected) }
        assertEquals(expected.size, found.size)
    }

    @Test
    fun `list films filter`() {
        val film1 = Film(name = "Sudba cheloveka")
        val first = assertNotNull(repo.createFilm(film1))
        val film2 = Film(name = "17 mgnovenii")
        assertNotNull(repo.createFilm(film2))
        val film3 = Film(name = "Zhizn i sudba")
        val third = assertNotNull(repo.createFilm(film3))
        val expected = listOf(first, third)
        val found = repo.getFilms(name = "sudba")
        assertTrue { found.containsAll(expected) }
        assertEquals(expected.size, found.size)
    }

    @Test
    fun `get film by id`() {
        val film = Film(name = "Sudba cheloveka")
        val first = assertNotNull(repo.createFilm(film))
        assertEquals(first, repo.getFilmById(first.id))
    }

    @Test
    fun `get film by id absent`() {
        val film = Film(name = "Sudba cheloveka")
        val first = assertNotNull(repo.createFilm(film))
        assertNull(repo.getFilmById(first.id - 1))
    }

    @Test
    fun `delete film`() {
        val film = Film(name = "Sudba cheloveka")
        val first = assertNotNull(repo.createFilm(film))
        assertEquals(first, repo.deleteFilmById(first.id))
    }

    @Test
    fun `delete film absent`() {
        val film = Film(name = "Sudba cheloveka")
        val first = assertNotNull(repo.createFilm(film))
        assertNull(repo.deleteFilmById(first.id - 1))
    }
}