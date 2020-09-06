package org.mgaidamak.dao

import java.sql.DriverManager
import java.sql.ResultSet
import java.util.Properties

abstract class DbRepo<T>(val url: String,
                         val props: Properties = Properties()) {

    protected abstract fun form(rs: ResultSet): T

    protected tailrec fun ResultSet.collect(list: List<T>): List<T>
        = if (!next()) list else collect(list.plus(form(this)))

    fun clear(sql: String) {
        try {
            DriverManager.getConnection(url, props).use { connection ->
                connection.createStatement().use { it.executeUpdate(sql) }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun total(sql: String): Int {
        return try {
            DriverManager.getConnection(url, props).use { connection ->
                connection.createStatement().use { st ->
                    st.executeQuery(sql)
                }.use { rs ->
                    if (rs.next()) rs.getInt(1) else 0
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            0
        }
    }

    protected fun single(id: Int, sql: String): T? {
        return try {
            DriverManager.getConnection(url, props).use { connection ->
                connection.prepareStatement(sql).use { ps ->
                    ps.setInt(1, id)
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
}