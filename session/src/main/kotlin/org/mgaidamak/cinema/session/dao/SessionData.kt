package org.mgaidamak.cinema.session.dao

import java.sql.ResultSet

data class Film(
    val id: Int = 0,
    val name: String
) {
    constructor(set: ResultSet): this(set.getInt(1), set.getString(2))
}

data class Session(
    val id: Int = 0,
    val film: Int,
    val hall: Int,
    val date: String,
    val price: Int
) {
    constructor(set: ResultSet): this(set.getInt(1), set.getInt(2),
        set.getInt(3),
        set.getTimestamp(4).toInstant().toString(), set.getInt(5))
}

data class Page(
    val offset: Int,
    val limit: Int
)