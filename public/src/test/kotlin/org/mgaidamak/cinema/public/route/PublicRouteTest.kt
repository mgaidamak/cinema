package org.mgaidamak.cinema.public.route

import io.ktor.http.HttpStatusCode
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.mgaidamak.cinema.public.dao.AdminCinema
import org.mgaidamak.cinema.public.dao.AdminFilm
import org.mgaidamak.cinema.public.dao.AdminHall
import org.mgaidamak.cinema.public.dao.AdminSeat
import org.mgaidamak.cinema.public.dao.AdminSession
import org.mgaidamak.cinema.public.dao.AdminTicket
import org.mgaidamak.cinema.public.dao.Cinema
import org.mgaidamak.cinema.public.dao.IPublicRepo
import org.mgaidamak.cinema.public.dao.Seat
import org.mgaidamak.cinema.public.dao.Session
import org.mgaidamak.cinema.public.response.Response
import java.time.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

/**
 * We test how PublicRoute works.
 * Repo is pure mock.
 */
class PublicRouteTest {

    private val repo = mockk<IPublicRepo>()
    private val route = PublicRoute(repo)

    @Test
    fun `get empty cinema list`() {
        coEvery { repo.getCinemas("Tomsk") } returns Response(data = emptyList())

        val response = runBlocking { route.getCinemas("Tomsk") }
        assertEquals(HttpStatusCode.OK, response.code)
        assertNull(response.message)
        assertEquals(emptyList(), response.data)
    }

    @Test
    fun `get cinema list`() {
        coEvery { repo.getCinemas("Novosibirsk") } returns Response(data = listOf(
            AdminCinema(1, "First", "Novosibirsk", "Lenina", "Asia/Novosibirsk"),
            AdminCinema(2, "Second", "Novosibirsk", "Gogolya", "Asia/Novosibirsk")
        ))

        val response = runBlocking { route.getCinemas("Novosibirsk") }
        assertEquals(HttpStatusCode.OK, response.code)
        assertNull(response.message)
        val expected = listOf(
            Cinema(1, "First", "Novosibirsk", "Lenina"),
            Cinema(2, "Second", "Novosibirsk", "Gogolya")
        )
        assertEquals(expected, response.data)
    }

    @Test
    fun `get sessions halls failed`() {
        coEvery { repo.getHalls(1) } returns Response(HttpStatusCode.NotFound)

        val response = runBlocking { route.getSessions(1, LocalDate.now()) }
        assertEquals(HttpStatusCode.InternalServerError, response.code)
        assertEquals("Error while load halls of cinema 1", response.message)
        assertNull(response.data)
    }

    @Test
    fun `get sessions halls empty`() {
        coEvery { repo.getHalls(1) } returns Response(data = emptyList())

        val response = runBlocking { route.getSessions(1, LocalDate.now()) }
        assertEquals(HttpStatusCode.OK, response.code)
        assertEquals("No halls available at cinema 1", response.message)
        assertEquals(emptyList(), response.data)
    }

    @Test
    fun `get sessions failed`() {
        coEvery { repo.getHalls(1) } returns Response(data = listOf(
            AdminHall(1, 1, "Big")
        ))
        coEvery { repo.getSessions(listOf(1).toIntArray()) } returns Response(HttpStatusCode.NotFound)

        val response = runBlocking { route.getSessions(1, LocalDate.now()) }
        assertEquals(HttpStatusCode.InternalServerError, response.code)
        assertEquals("Error while load session at cinema 1", response.message)
        assertNull(response.data)
    }

    @Test
    fun `get sessions empty`() {
        coEvery { repo.getHalls(1) } returns Response(data = listOf(
            AdminHall(1, 1, "Big")
        ))
        coEvery { repo.getSessions(listOf(1).toIntArray()) } returns Response(data = emptyList())

        val response = runBlocking { route.getSessions(1, LocalDate.now()) }
        assertEquals(HttpStatusCode.OK, response.code)
        assertEquals("No sessions available at cinema 1", response.message)
        assertEquals(emptyList(), response.data)
    }

    @Test
    fun `get sessions valid`() {
        coEvery { repo.getHalls(1) } returns Response(data = listOf(
            AdminHall(1, 1, "Big"),
            AdminHall(2, 1, "Small"),
        ))
        coEvery { repo.getSessions(listOf(1, 2).toIntArray()) } returns Response(data = listOf(
            AdminSession(1, 1, 1, "2020-09-10T00:00:00Z", 100),
            AdminSession(2, 1, 1, "2020-09-11T00:00:00Z", 200),
            AdminSession(3, 2, 2, "2020-09-11T00:00:00Z", 200),
        ))
        coEvery { repo.getFilm(1) } returns Response(data = AdminFilm(1, "Great race"))
        coEvery { repo.getFilm(2) } returns Response(data = AdminFilm(2, "Some like it hot"))

        // TODO verify film has been called only twice

        val expected = listOf(
            Session(1, "Great race", 1, "2020-09-10T00:00:00Z", 100),
            Session(2, "Great race", 1, "2020-09-11T00:00:00Z", 200),
            Session(3, "Some like it hot", 2, "2020-09-11T00:00:00Z", 200),
        )

        val response = runBlocking { route.getSessions(1, LocalDate.now()) }
        assertEquals(HttpStatusCode.OK, response.code)
        assertEquals(expected, response.data)
    }

    @Test
    fun `get sessions without names`() {
        coEvery { repo.getHalls(1) } returns Response(data = listOf(
            AdminHall(1, 1, "Big"),
            AdminHall(2, 1, "Small"),
        ))
        coEvery { repo.getSessions(listOf(1, 2).toIntArray()) } returns Response(data = listOf(
            AdminSession(1, 1, 1, "2020-09-10T00:00:00Z", 100),
            AdminSession(2, 1, 1, "2020-09-11T00:00:00Z", 200),
            AdminSession(3, 2, 2, "2020-09-11T00:00:00Z", 200),
        ))
        coEvery { repo.getFilm(1) } returns Response(HttpStatusCode.NotFound)
        coEvery { repo.getFilm(2) } returns Response(HttpStatusCode.NotFound)

        val expected = listOf(
            Session(1, "", 1, "2020-09-10T00:00:00Z", 100),
            Session(2, "", 1, "2020-09-11T00:00:00Z", 200),
            Session(3, "", 2, "2020-09-11T00:00:00Z", 200),
        )

        val response = runBlocking { route.getSessions(1, LocalDate.now()) }
        assertEquals(HttpStatusCode.OK, response.code)
        assertEquals(expected, response.data)
    }

    @Test
    fun `get seats session failed`() {
        coEvery { repo.getSession(1) } returns Response(HttpStatusCode.NotFound)

        val response = runBlocking { route.getSeats(1) }
        assertEquals(HttpStatusCode.InternalServerError, response.code)
        assertEquals("Error while load session 1", response.message)
        assertNull(response.data)
    }

    @Test
    fun `get seats session is null`() {
        coEvery { repo.getSession(1) } returns Response()

        val response = runBlocking { route.getSeats(1) }
        assertEquals(HttpStatusCode.InternalServerError, response.code)
        assertEquals("Error while load session 1", response.message)
        assertNull(response.data)
    }

    @Test
    fun `get seats failed`() {
        coEvery { repo.getSession(1) } returns Response(
            data = AdminSession(1, 1, 1, "2020-09-10T00:00:00Z", 100))
        coEvery { repo.getSeats(1) } returns Response(HttpStatusCode.NotFound)

        val response = runBlocking { route.getSeats(1) }
        assertEquals(HttpStatusCode.InternalServerError, response.code)
        assertEquals("Error while load hall plan for session 1", response.message)
        assertNull(response.data)
    }

    @Test
    fun `get seats null`() {
        coEvery { repo.getSession(1) } returns Response(
            data = AdminSession(1, 1, 1, "2020-09-10T00:00:00Z", 100))
        coEvery { repo.getSeats(1) } returns Response()

        val response = runBlocking { route.getSeats(1) }
        assertEquals(HttpStatusCode.OK, response.code)
        assertEquals("No seats available for session 1", response.message)
        assertNull(response.data)
    }

    @Test
    fun `get seats empty`() {
        coEvery { repo.getSession(1) } returns Response(
            data = AdminSession(1, 1, 1, "2020-09-10T00:00:00Z", 100))
        coEvery { repo.getSeats(1) } returns Response(data = emptyList())

        val response = runBlocking { route.getSeats(1) }
        assertEquals(HttpStatusCode.OK, response.code)
        assertEquals("No seats available for session 1", response.message)
        assertNull(response.data)
    }

    @Test
    fun `get seats sold failed`() {
        coEvery { repo.getSession(1) } returns Response(
            data = AdminSession(1, 1, 1, "2020-09-10T00:00:00Z", 100))
        coEvery { repo.getSeats(1) } returns Response(data = listOf(
            AdminSeat(1, 1, 1, 1),
            AdminSeat(1, 1, 1, 2),
            AdminSeat(1, 1, 2, 1),
            AdminSeat(1, 1, 2, 2),
        ))
        coEvery { repo.getTickets(1) } returns Response(HttpStatusCode.NotFound)

        val response = runBlocking { route.getSeats(1) }
        assertEquals(HttpStatusCode.InternalServerError, response.code)
        assertEquals("Error while load sold tickets for session 1", response.message)
        assertNull(response.data)
    }

    @Test
    fun `get seats sold null`() {
        coEvery { repo.getSession(1) } returns Response(
            data = AdminSession(1, 1, 1, "2020-09-10T00:00:00Z", 100))
        coEvery { repo.getSeats(1) } returns Response(data = listOf(
            AdminSeat(1, 1, 1, 1),
            AdminSeat(2, 1, 1, 2),
            AdminSeat(3, 1, 2, 1),
            AdminSeat(4, 1, 2, 2),
        ))
        coEvery { repo.getTickets(1) } returns Response()

        val expected = listOf(
            Seat(1, 1, 1, 0),
            Seat(2, 1, 2, 0),
            Seat(3, 2, 1, 0),
            Seat(4, 2, 2, 0),
        )

        val response = runBlocking { route.getSeats(1) }
        assertEquals(HttpStatusCode.OK, response.code)
        assertEquals(expected, response.data)
    }

    @Test
    fun `get seats sold empty`() {
        coEvery { repo.getSession(1) } returns Response(
            data = AdminSession(1, 1, 1, "2020-09-10T00:00:00Z", 100))
        coEvery { repo.getSeats(1) } returns Response(data = listOf(
            AdminSeat(1, 1, 1, 1),
            AdminSeat(2, 1, 1, 2),
            AdminSeat(3, 1, 2, 1),
            AdminSeat(4, 1, 2, 2),
        ))
        coEvery { repo.getTickets(1) } returns Response(data = emptyList())

        val expected = listOf(
            Seat(1, 1, 1, 0),
            Seat(2, 1, 2, 0),
            Seat(3, 2, 1, 0),
            Seat(4, 2, 2, 0),
        )

        val response = runBlocking { route.getSeats(1) }
        assertEquals(HttpStatusCode.OK, response.code)
        assertEquals(expected, response.data)
    }

    @Test
    fun `get seats valid`() {
        coEvery { repo.getSession(1) } returns Response(
            data = AdminSession(1, 1, 1, "2020-09-10T00:00:00Z", 100))
        coEvery { repo.getSeats(1) } returns Response(data = listOf(
            AdminSeat(1, 1, 1, 1),
            AdminSeat(2, 1, 1, 2),
            AdminSeat(3, 1, 2, 1),
            AdminSeat(4, 1, 2, 2),
        ))
        coEvery { repo.getTickets(1) } returns Response(data = listOf(
            AdminTicket(1, 1, 1, 1, 1),
            AdminTicket(1, 1, 1, 2, 2),
        ))

        val expected = listOf(
            Seat(1, 1, 1, 1),
            Seat(2, 1, 2, 2),
            Seat(3, 2, 1, 0),
            Seat(4, 2, 2, 0),
        )

        val response = runBlocking { route.getSeats(1) }
        assertEquals(HttpStatusCode.OK, response.code)
        assertEquals(expected, response.data)
    }
}