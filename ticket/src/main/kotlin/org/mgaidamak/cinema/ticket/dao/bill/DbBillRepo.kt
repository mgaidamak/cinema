package org.mgaidamak.cinema.ticket.dao.bill

import org.mgaidamak.cinema.ticket.dao.DbRepo
import org.mgaidamak.cinema.ticket.dao.Bill
import org.mgaidamak.cinema.ticket.dao.Page

import java.sql.DriverManager
import java.sql.ResultSet
import java.util.Properties

/**
 * Production ready repository on PostgreSQL
 */
class DbBillRepo(url: String,
                 props: Properties = Properties()): DbRepo<Bill>(url, props), IBillRepo {

    override fun form(rs: ResultSet) = Bill(rs)

    override fun createBill(bill: Bill): Bill? {
        val sql = """INSERT INTO bill (customer, session, status, total)
                     VALUES (?, ?, ?, ?) RETURNING id, customer, session, status, total"""
        return try {
            DriverManager.getConnection(url, props).use { connection ->
                connection.prepareStatement(sql).use { ps ->
                    ps.setInt(1, bill.customer)
                    ps.setInt(2, bill.session)
                    ps.setInt(3, bill.status)
                    ps.setInt(4, bill.total)
                    ps.executeQuery().use { rs ->
                        if (rs.next()) Bill(rs) else null
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    override fun updateBill(bill: Bill): Bill? {
        val sql = """UPDATE bill SET status = ? WHERE id = ?
                     RETURNING id, customer, session, status, total"""
        return try {
            DriverManager.getConnection(url, props).use { connection ->
                connection.prepareStatement(sql).use { ps ->
                    ps.setInt(1, bill.status)
                    ps.setInt(2, bill.id)
                    ps.executeQuery().use { rs ->
                        if (rs.next()) Bill(rs) else null
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    override fun getBills(page: Page, sort: List<String>): Collection<Bill> {
        val sql = "SELECT id, customer, session, status, total FROM bill LIMIT ? OFFSET ?"
        return try {
            DriverManager.getConnection(url, props).use { connection ->
                connection.prepareStatement(sql).use { ps ->
                    ps.setInt(1, page.limit)
                    ps.setInt(2, page.offset)
                    ps.executeQuery().use { it.collect(emptyList()) }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    override fun getBillById(id: Int): Bill? {
        val sql = "SELECT id, customer, session, status, total FROM bill WHERE id = ?"
        return single(id, sql)
    }

    override fun deleteBillById(id: Int): Bill? {
        val sql = "DELETE FROM bill WHERE id = ? RETURNING id, customer, session, status, total"
        return single(id, sql)
    }

    override fun clear() = clear("DELETE FROM bill")

    override fun total() = total("SELECT COUNT(*) FROM bill")
}