package org.mgaidamak.cinema.ticket.dao

import java.sql.ResultSet

data class Bill(
    val id: Int = 0,
    val customer: Int,
    val session: Int,
    val status: Int,
    val total: Int
) {
    constructor(set: ResultSet): this(set.getInt(1), set.getInt(2),
        set.getInt(3), set.getInt(4), set.getInt(5))
}

data class Seat(
    val id: Int = 0,
    val bill: Int = 0,
    val session: Int,
    val seat: Int,
    val status: Int
) {
    constructor(set: ResultSet): this(set.getInt(1), set.getInt(2),
        set.getInt(3), set.getInt(4), set.getInt(5))
}

data class Page(
    val offset: Int,
    val limit: Int
)