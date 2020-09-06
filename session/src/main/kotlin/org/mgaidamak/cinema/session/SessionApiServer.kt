package org.mgaidamak.cinema.session

import io.ktor.application.call
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.request.document
import io.ktor.request.receive
import io.ktor.request.uri
import io.ktor.response.respond
import io.ktor.response.respondText
import io.ktor.routing.*
import org.mgaidamak.cinema.session.dao.Film
import org.mgaidamak.cinema.session.dao.Page
import org.mgaidamak.cinema.session.dao.Session
import org.mgaidamak.cinema.session.dao.film.IFilmRepo
import org.mgaidamak.cinema.session.dao.session.ISessionRepo

class SessionApiServer {
    fun Routing.registerFilm(repo: IFilmRepo) {
        get("/") {
            call.respondText("My Session App", ContentType.Text.Html)
        }
        route("/film") {
            post {
                println("Post ${call.request.uri}")
                val body: Film = call.receive()
                repo.createFilm(body)?.also { call.respond(it) }
                    ?: call.respond(HttpStatusCode.NotFound, "Not created")
            }
            get {
                println("Get ${call.request.uri}")
                val name = call.parameters["name"]
                val page = Page(0, 10)
                repo.getFilms(page, name).also { call.respond(it) }
            }
        }
        route("/film/{id}") {
            get {
                println("Get ${call.request.uri}")
                val id = call.request.document().toIntOrNull()
                if (id == null) {
                    call.respond(HttpStatusCode.BadRequest, "Invalid id")
                    return@get
                }
                repo.getFilmById(id)?.also { call.respond(it) }
                    ?: call.respond(HttpStatusCode.NotFound, "Not found by $id")
            }
            delete {
                println("Delete ${call.request.uri}")
                val id = call.request.document().toIntOrNull()
                if (id == null) {
                    call.respond(HttpStatusCode.BadRequest, "Invalid id")
                    return@delete
                }
                repo.deleteFilmById(id)?.also { call.respond(it) }
                    ?: call.respond(HttpStatusCode.NotFound, "Not found by $id")
            }
        }
    }

    fun Routing.registerSession(repo: ISessionRepo) {
        route("/session") {
            post {
                println("Post ${call.request.uri}")
                val body: Session = call.receive()
                repo.createSession(body)?.also { call.respond(it) }
                    ?: call.respond(HttpStatusCode.NotFound, "Not created")
            }
            get {
                println("Get ${call.request.uri}")
                val film = call.parameters["film"]?.toIntOrNull()
                val hall = call.parameters.getAll("hall")
                    ?.stream()?.mapToInt { s -> s.toInt(10) }?.toArray()?.toTypedArray()
                    ?: emptyArray()
                val page = Page(0, 10)
                repo.getSessions(film, hall, page).also { call.respond(it) }
            }
        }
        route("/session/{id}") {
            get {
                println("Get ${call.request.uri}")
                val id = call.request.document().toIntOrNull()
                if (id == null) {
                    call.respond(HttpStatusCode.BadRequest, "Invalid id")
                    return@get
                }
                repo.getSessionById(id)?.also { call.respond(it) }
                    ?: call.respond(HttpStatusCode.NotFound, "Not found by $id")
            }
            delete {
                println("Delete ${call.request.uri}")
                val id = call.request.document().toIntOrNull()
                if (id == null) {
                    call.respond(HttpStatusCode.BadRequest, "Invalid id")
                    return@delete
                }
                repo.deleteSessionById(id)?.also { call.respond(it) }
                    ?: call.respond(HttpStatusCode.NotFound, "Not found by $id")
            }
        }
    }
}