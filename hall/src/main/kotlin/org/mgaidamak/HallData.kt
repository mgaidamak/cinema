package org.mgaidamak

data class Cinema(
    val id: Long = 0,
    val name: String,
    val city: String,
    val address: String,
    val timezone: String
)

data class Hall(
    val id: Long,
    val cinema: Long,
    val name: String
)

data class Seat(
    val id: Long,
    val hall: Long,
    val x: Int,
    val y: Int,
    val sector: Int
)

data class Tariff(
    val id: Long,
    val cinema: Long,
    val name: String
)

data class Price(
    val id: Long,
    val tariff: Long,
    val name: String,
    val sector: Int,
    val price: Int
)

data class Page(
    val offset: Long,
    val limit: Long
)