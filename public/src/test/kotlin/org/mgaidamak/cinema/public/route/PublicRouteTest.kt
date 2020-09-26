package org.mgaidamak.cinema.public.route

import io.ktor.http.HttpStatusCode
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.mgaidamak.cinema.public.dao.IPublicRepo
import org.mgaidamak.cinema.public.response.Response
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class PublicRouteTest {
    val repo = mockk<IPublicRepo>()
    private val route = PublicRoute(repo)

    @Test
    fun `get cinema list`() {
        coEvery { repo.getCinemas("Tomsk") } returns Response(data = emptyList())
        val response = runBlocking { route.getCinemas("Tomsk") }
        assertEquals(HttpStatusCode.OK, response.code)
        assertNull(response.message)
        assertEquals(emptyList(), response.data)
    }
}