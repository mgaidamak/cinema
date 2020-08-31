package org.mgaidamak.cinema.session.dao.film

import org.mgaidamak.cinema.session.dao.Film
import org.mgaidamak.cinema.session.dao.Page

/**
 * DAO for Film list
 */
interface IFilmRepo {
    fun createFilm(film: Film): Film?
    fun getFilms(page: Page = Page(0, 10),
                 sort: List<String> = emptyList(),
                 filter: String? = null): Collection<Film>
    fun getFilmById(id: Int): Film?
    fun deleteFilmById(id: Int): Film?
    fun clear()
    fun total(): Int
}