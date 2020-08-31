package org.mgaidamak.cinema.session.dao.film

import org.mgaidamak.cinema.session.dao.Film
import org.mgaidamak.cinema.session.dao.Page

import java.sql.DriverManager
import java.sql.ResultSet
import java.util.Properties

/**
 * Production ready repository on PostgreSQL
 */
class DbFilmRepo(private val url: String,
                 private val props: Properties = Properties()): IFilmRepo {

    override fun createFilm(film: Film): Film? {
        val sql = "INSERT INTO film (name) VALUES (?) RETURNING id"
        return try {
            DriverManager.getConnection(url, props).use { connection ->
                connection.prepareStatement(sql).apply {
                    setString(1, film.name)
                }.executeQuery().use { rs ->
                    if (rs.next()) rs.getInt(1) else null
                }?.let {
                    Film(it, film.name)
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
            DriverManager.getConnection(url, props).use {
                it.prepareStatement(sql).apply {
                    setString(1, filter ?: "")
                    setInt(2, page.limit)
                    setInt(3, page.offset)
                }.executeQuery().collect(emptyList())
            }
        } catch (e: Exception) {
            println(e)
            emptyList()
        }
    }

    private fun queryFilm(id: Int, sql: String): Film? {
        return try {
            DriverManager.getConnection(url, props).use { it ->
                it.prepareStatement(sql).apply {
                    setInt(1, id)
                }.executeQuery().let {
                    if (it.next()) Film(id, it.getString(1)) else null
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

    override fun clear() {
        val sql = "DELETE FROM film"
        try {
            DriverManager.getConnection(url, props).use {
                it.createStatement().executeUpdate(sql)
            }
        } catch (e: Exception) {
            println(e)
        }
    }

    override fun total(): Int {
        val sql = "SELECT COUNT(*) FROM film"
        return try {
            DriverManager.getConnection(url, props).use { connection ->
                connection.createStatement().executeQuery(sql).let { rs ->
                    if (rs.next()) rs.getInt(1) else 0
                }
            }
        } catch (e: Exception) {
            println(e)
            0
        }
    }
}