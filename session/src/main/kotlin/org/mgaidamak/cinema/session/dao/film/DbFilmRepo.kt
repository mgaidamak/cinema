package org.mgaidamak.cinema.session.dao.film

import org.mgaidamak.cinema.session.dao.DbRepo
import org.mgaidamak.cinema.session.dao.Film
import org.mgaidamak.cinema.session.dao.Page

import java.sql.DriverManager
import java.sql.ResultSet
import java.util.Properties

/**
 * Production ready repository on PostgreSQL
 */
class DbFilmRepo(url: String,
                 props: Properties = Properties()): DbRepo(url, props), IFilmRepo {

    override fun createFilm(film: Film): Film? {
        val sql = "INSERT INTO film (name) VALUES (?) RETURNING id"
        return try {
            DriverManager.getConnection(url, props).use { connection ->
                connection.prepareStatement(sql).use { ps ->
                    ps.setString(1, film.name)
                    ps.executeQuery().use { rs ->
                        if (rs.next()) rs.getInt(1) else null
                    }?.let {
                        Film(it, film.name)
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private tailrec fun ResultSet.collect(list: List<Film>): List<Film>
        = if (!next()) list else collect(list.plus(Film(getInt(1), getString(2))))

    override fun getFilms(page: Page, sort: List<String>, filter: String?): Collection<Film> {
        val sql = "SELECT id, name FROM film WHERE name ILIKE '%' || ? || '%' LIMIT ? OFFSET ?"
        return try {
            DriverManager.getConnection(url, props).use { it ->
                it.prepareStatement(sql).use { ps ->
                    ps.setString(1, filter ?: "")
                    ps.setInt(2, page.limit)
                    ps.setInt(3, page.offset)
                    ps.executeQuery().use { it.collect(emptyList()) }
                }
            }
        } catch (e: Exception) {
            println(e)
            emptyList()
        }
    }

    private fun queryFilm(id: Int, sql: String): Film? {
        return try {
            DriverManager.getConnection(url, props).use { connection ->
                connection.prepareStatement(sql).use { ps ->
                    ps.setInt(1, id)
                    ps.executeQuery().use { rs ->
                        if (rs.next()) Film(id, rs.getString(1)) else null
                    }
                }
            }
        } catch (e: Exception) {
            println(e)
            null
        }
    }

    override fun getFilmById(id: Int): Film? {
        val sql = "SELECT name FROM film WHERE id = ?"
        return queryFilm(id, sql)
    }

    override fun deleteFilmById(id: Int): Film? {
        val sql = "DELETE FROM film WHERE id = ? RETURNING name"
        return queryFilm(id, sql)
    }

    override fun clear() = clear("DELETE FROM film")

    override fun total() = total("SELECT COUNT(*) FROM film")
}