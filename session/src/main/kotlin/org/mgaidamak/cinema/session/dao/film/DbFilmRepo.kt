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
                 props: Properties = Properties()): DbRepo<Film>(url, props), IFilmRepo {

    override fun form(rs: ResultSet) = Film(rs)

    override fun createFilm(film: Film): Film? {
        val sql = "INSERT INTO film (name) VALUES (?) RETURNING id, name"
        return try {
            DriverManager.getConnection(url, props).use { connection ->
                connection.prepareStatement(sql).use { ps ->
                    ps.setString(1, film.name)
                    ps.executeQuery().use { rs ->
                        if (rs.next()) form(rs) else null
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    override fun getFilms(page: Page, name: String?): Collection<Film> {
        val sql = "SELECT id, name FROM film WHERE name ILIKE '%' || ? || '%' LIMIT ? OFFSET ?"
        return try {
            DriverManager.getConnection(url, props).use { it ->
                it.prepareStatement(sql).use { ps ->
                    ps.setString(1, name ?: "")
                    ps.setInt(2, page.limit)
                    ps.setInt(3, page.offset)
                    ps.executeQuery().use { it.collect(emptyList()) }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    override fun getFilmById(id: Int): Film? {
        val sql = "SELECT id, name FROM film WHERE id = ?"
        return single(id, sql)
    }

    override fun deleteFilmById(id: Int): Film? {
        val sql = "DELETE FROM film WHERE id = ? RETURNING id, name"
        return single(id, sql)
    }

    override fun clear() = clear("DELETE FROM film")

    override fun total() = total("SELECT COUNT(*) FROM film")
}