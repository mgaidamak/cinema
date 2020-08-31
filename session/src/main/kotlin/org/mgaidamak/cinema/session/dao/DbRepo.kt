package org.mgaidamak.cinema.session.dao

import java.sql.DriverManager
import java.util.Properties

abstract class DbRepo(val url: String,
                      val props: Properties = Properties()) {

    fun clear(sql: String) {
        try {
            DriverManager.getConnection(url, props).use { connection ->
                connection.createStatement().use { it.executeUpdate(sql) }
            }
        } catch (e: Exception) {
            println(e)
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
            println(e)
            0
        }
    }
}