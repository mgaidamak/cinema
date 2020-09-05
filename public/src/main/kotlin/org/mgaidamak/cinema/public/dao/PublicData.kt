package org.mgaidamak.cinema.public.dao

import java.time.ZonedDateTime

data class Cinema(
    val id: Int = 0,
    val name: String,
    val city: String,
    val address: String
) {
    constructor(adminCinema: AdminCinema): this(
        id = adminCinema.id,
        name = adminCinema.name,
        city = adminCinema.city,
        address = adminCinema.address)
}

data class AdminCinema(
    val id: Int = 0,
    val name: String,
    val city: String,
    val address: String,
    val timezone: String
)

data class Session(
    val id: Int = 0,
    val film: Int,
    val hall: Int,
    val date: ZonedDateTime,
    val price: Int
)

data class Seat(
    val id: Int = 0,
    val x: Int,
    val y: Int,
    val status: Int
)

data class Bill(
    val id: Int = 0,
    val customer: Int,
    val session: Int,
    val status: Int,
    val seats: List<Seat>
)

data class Page(
    val offset: Int,
    val limit: Int
)