package org.mgaidamak.cinema.session

import io.ktor.routing.*
import org.mgaidamak.cinema.session.dao.film.IFilmRepo

class SessionApiServer(val repo: IFilmRepo) {

    fun Routing.registerFilm() {
    }
}