package org.mgaidamak.dao.hall

import org.mgaidamak.dao.Hall
import org.mgaidamak.dao.Page

/**
 * DAO for Hall at Hall list
 */
interface IHallRepo {
    fun createHall(hall: Hall): Hall?
    fun getHalls(cinema: Int,
                 page: Page = Page(0, 10),
                 sort: List<String> = emptyList()): Collection<Hall>
    fun getHallById(id: Int): Hall?
    fun deleteHallById(id: Int): Hall?
    fun clear()
    fun total(): Int
}