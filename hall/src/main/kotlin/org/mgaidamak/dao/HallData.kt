package org.mgaidamak.dao

data class Cinema(
    val id: Int = 0,
    val name: String,
    val city: String,
    val address: String,
    val timezone: String
)

data class Hall(
    val id: Int = 0,
    val cinema: Int,
    val name: String
)

data class Seat(
    val id: Int = 0,
    val hall: Int,
    val x: Int,
    val y: Int
)

data class Page(
    val offset: Int,
    val limit: Int
)