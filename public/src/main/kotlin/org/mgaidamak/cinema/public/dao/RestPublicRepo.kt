package org.mgaidamak.cinema.public.dao

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.features.defaultRequest
import io.ktor.client.features.json.GsonSerializer
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.request.get
import io.ktor.client.request.host
import io.ktor.client.request.port
import io.ktor.config.ApplicationConfig
import io.ktor.util.KtorExperimentalAPI
import java.time.LocalDate
import kotlin.streams.toList

@KtorExperimentalAPI
class RestPublicRepo constructor(config: ApplicationConfig): IPublicRepo {

    private val hallClient: HttpClient = HttpClient(CIO) {
        defaultRequest {
            host = config.property("hall.host").getString()
            port = config.property("hall.port").getString().toInt()
        }
        install(JsonFeature) {
            serializer = GsonSerializer()
        }
    }

    override suspend fun getCinemas(city: String?): Collection<Cinema> {
        val path = "/cinema"
        city.apply { path.plus("?city=$city") }
        val list = hallClient.get<List<AdminCinema>>(path = path)
        return list.stream().map { Cinema(it) }.toList()
    }

    override suspend fun getSessions(cinema: Int, date: LocalDate): Collection<Session> {
        TODO("Not yet implemented")
    }

    override suspend fun getSeats(session: Int): Collection<Seat> {
        TODO("Not yet implemented")
    }

    override suspend fun getBill(id: Int): Bill? {
        TODO("Not yet implemented")
    }

    override suspend fun postBill(bill: Bill): Bill? {
        TODO("Not yet implemented")
    }

    override suspend fun deleteBill(id: Int): Bill? {
        TODO("Not yet implemented")
    }
}