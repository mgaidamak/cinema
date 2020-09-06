package org.mgaidamak.cinema.session.dao.session

import org.mgaidamak.cinema.session.dao.DbRepo
import org.mgaidamak.cinema.session.dao.Page
import org.mgaidamak.cinema.session.dao.Session
import java.sql.DriverManager
import java.sql.ResultSet
import java.sql.Timestamp
import java.time.Instant
import java.time.ZoneId
import java.util.Properties

/**
 * Production ready repository on PostgreSQL
 */
class DbSessionRepo(url: String,
                    props: Properties = Properties()): DbRepo<Session>(url, props), ISessionRepo {

    override fun form(rs: ResultSet) = Session(rs)

    override fun createSession(session: Session): Session? {
        val sql = "INSERT INTO session (film, hall, date, price) VALUES (?, ?, ?, ?) RETURNING id, film, hall, date, price"
        return try {
            return DriverManager.getConnection(url, props).use { connection ->
                connection.prepareStatement(sql).use { ps ->
                    ps.setInt(1, session.film)
                    ps.setInt(2, session.hall)
                    ps.setTimestamp(3, Timestamp.from(Instant.parse(session.date)))
                    ps.setInt(4, session.price)
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
            e.printStackTrace()
            emptyList()
        }
    }

    override fun getSessionById(id: Int): Session? {
        val sql = "SELECT id, film, hall, date, price FROM session WHERE id = ?"
        return single(id, sql)
    }

    override fun deleteSessionById(id: Int): Session? {
        val sql = "DELETE FROM session WHERE id = ? RETURNING id, film, hall, date, price"
        return single(id, sql)
    }

    override fun clear() = clear("DELETE FROM session")

    override fun total() = total("SELECT COUNT(*) FROM session")
}