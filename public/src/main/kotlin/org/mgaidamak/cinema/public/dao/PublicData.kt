package org.mgaidamak.cinema.public.dao

/**
 * Public cinema data
 */
data class Cinema(
    val id: Int = 0,
    val name: String,
    val city: String,
    val address: String
    // TODO add timezone too
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

/**
 * Public hall data
 */
data class Hall(
    val id: Int = 0,
    val name: String
) {
    constructor(adminHall: AdminHall): this(
        id = adminHall.id,
        name = adminHall.name)
}

data class AdminHall(
    val id: Int = 0,
    val cinema: Int,
    val name: String
)

/**
 * Public session data
 */
data class Session(
    val id: Int = 0,
    val film: String,
    val hall: Int,
    val date: String,
    val price: Int
) {
    constructor(adminSession: AdminSession, film: String): this(
        id = adminSession.id,
        film = film,
        hall = adminSession.hall,
        date = adminSession.date,
        price = adminSession.price)
}

data class AdminSession(
    val id: Int = 0,
    val film: Int,
    val hall: Int,
    val date: String,
    val price: Int
)

data class AdminFilm(
    val id: Int = 0,
    val name: String
)

/**
 * Public seat/ticket data
 * Status: 0 free, 1 reserved, 2 sold
 */
data class Seat(
    val id: Int = 0,
    val x: Int,
    val y: Int,
    val status: Int
) {
    constructor(adminSeat: AdminSeat, status: Int): this(
        id = adminSeat.id,
        x = adminSeat.x,
        y = adminSeat.y,
        status = status)
}

data class AdminSeat(
    val id: Int = 0,
    val hall: Int,
    val x: Int,
    val y: Int
)

/**
 * Public bill data
 * Status: 0 new, 1 payed, 2 rejected, 3 deleted
 */
data class Bill(
    val id: Int = 0,
    val customer: Int,
    val session: Int,
    val status: Int,
    val total: Int,
    val seats: Collection<Seat>,
) {
    constructor(adminBill: AdminBill, seats: Collection<Seat>): this(
        id = adminBill.id,
        customer = adminBill.customer,
        session = adminBill.session,
        status = adminBill.status,
        total = adminBill.total,
        seats = seats)
}

data class AdminBill(
    val id: Int = 0,
    val customer: Int,
    val session: Int,
    val status: Int,
    val total: Int
)

/**
 * Status: 1 reserved, 2 sold
 */
data class AdminTicket(
    val id: Int = 0,
    val bill: Int = 0,
    val session: Int,
    val seat: Int,
    val status: Int
)

data class Page(
    val offset: Int,
    val limit: Int
)