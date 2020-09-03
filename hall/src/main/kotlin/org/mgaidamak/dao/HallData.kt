package org.mgaidamak.dao

import java.sql.ResultSet

data class Cinema(
    val id: Int = 0,
    val name: String,
    val city: String,
    val address: String,
    val timezone: String
) {
    constructor(set: ResultSet): this(set.getInt(1), set.getString(2),
        set.getString(3), set.getString(4), set.getString(5))
}

data class Hall(
    val id: Int = 0,
    val cinema: Int,
    val name: String
) {
    constructor(set: ResultSet): this(set.getInt(1),
        set.getInt(2), set.getString(3))
}

data class Seat(
    val id: Int = 0,
    val hall: Int,
    val x: Int,
    val y: Int
) {
    constructor(set: ResultSet): this(set.getInt(1),
        set.getInt(2), set.getInt(3),
        set.getInt(4))
}

data class Page(
    val offset: Int,
    val limit: Int
)