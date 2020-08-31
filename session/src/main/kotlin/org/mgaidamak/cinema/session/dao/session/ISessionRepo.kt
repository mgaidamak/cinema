package org.mgaidamak.cinema.session.dao.session

import org.mgaidamak.cinema.session.dao.Page
import org.mgaidamak.cinema.session.dao.Session

/**
 * DAO for Hall at Cinema list
 */
interface ISessionRepo {
    fun createSession(session: Session): Session?
    fun getSessions(film: Int,
                    page: Page = Page(0, 10),
                    sort: List<String> = emptyList()): Collection<Session>
    fun getSessionById(id: Int): Session?
    fun deleteSessionById(id: Int): Session?
    fun clear()
    fun total(): Int
}