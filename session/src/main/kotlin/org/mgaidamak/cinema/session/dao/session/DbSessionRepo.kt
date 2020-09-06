package org.mgaidamak.cinema.session.dao.session

import org.mgaidamak.cinema.session.dao.DbRepo
import org.mgaidamak.cinema.session.dao.Page
import org.mgaidamak.cinema.session.dao.Session
import java.sql.DriverManager
import java.sql.ResultSet
import java.sql.Timestamp
import java.time.ZoneId
import java.util.Properties

/**
 * Production ready repository on PostgreSQL
 */
class DbSessionRepo(url: String,
                    props: Properties = Properties()): DbRepo(url, props), ISessionRepo {

    override fun createSession(session: Session): Session? {
        val sql = "INSERT INTO session (film, hall, date, price) VALUES (?, ?, ?, ?) RETURNING id"
        return try {
            return DriverManager.getConnection(url, props).use { connection ->
                connection.prepareStatement(sql).use { ps ->
                    ps.setInt(1, session.film)
                    ps.setInt(2, session.hall)
                    ps.setTimestamp(3, Timestamp.from(session.date.toInstant()))
                    ps.setInt(4, session.price)
                    ps.executeQuery().use { rs ->
                        if (rs.next()) rs.getInt(1) else null
                    }?.let {
                        Session(it, session.film, session.hall, session.date, session.price)
                    }
                }
            }
        } catch (e: Exception) {
            println(e)
            null
        }
    }

    private tailrec fun ResultSet.collect(list: List<Session>): List<Session>
        = if (!next()) list else collect(list.plus(Session(getInt(1), getInt(2),
        getInt(3), getTimestamp(4).toInstant().atZone(ZoneId.systemDefault()),
        getInt(5))))

    override fun getSessions(film: Int?,
                             hall: Array<Int>,
                             page: Page): Collection<Session> {
        // TODO process film
        val sql = "SELECT id, film, hall, date, price FROM session WHERE hall = ANY(?) LIMIT ? OFFSET ?"
        return try {
            DriverManager.getConnection(url, props).use { connection ->
                connection.prepareStatement(sql).use { ps ->
                    ps.setArray(1, connection.createArrayOf("integer", hall))
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

    private fun querySession(id: Int, sql: String): Session? {
        return try {
            DriverManager.getConnection(url, props).use { connection ->
                connection.prepareStatement(sql).use { ps ->
                    ps.setInt(1, id)
                    ps.executeQuery().use { rs ->
                        if (rs.next()) Session(id, rs.getInt(1), rs.getInt(2),
                            rs.getTimestamp(3).toInstant().atZone(ZoneId.systemDefault()),
                            rs.getInt(4)) else null
                    }
                }
            }
        } catch (e: Exception) {
            println(e)
            null
        }
    }

    override fun getSessionById(id: Int): Session? {
        val sql = "SELECT film, hall, date, price FROM session WHERE id = ?"
        return querySession(id, sql)
    }

    override fun deleteSessionById(id: Int): Session? {
        val sql = "DELETE FROM session WHERE id = ? RETURNING film, hall, date, price"
        return querySession(id, sql)
    }

    override fun clear() = clear("DELETE FROM session")

    override fun total() = total("SELECT COUNT(*) FROM session")
}