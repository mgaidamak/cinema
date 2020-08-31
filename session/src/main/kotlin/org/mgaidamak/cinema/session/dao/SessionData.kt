package org.mgaidamak.cinema.session.dao

import java.time.ZonedDateTime

data class Film(
    val id: Int = 0,
    val name: String
)

data class Session(
    val id: Int = 0,
    val film: Int,
    val hall: Int,
    val date: ZonedDateTime,
    val price: Int
)

data class Page(
    val offset: Int,
    val limit: Int
)