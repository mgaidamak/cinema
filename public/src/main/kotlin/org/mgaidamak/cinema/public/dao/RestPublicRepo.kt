package org.mgaidamak.cinema.public.dao

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.features.defaultRequest
import io.ktor.client.features.json.GsonSerializer
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.request.host
import io.ktor.client.request.port
import io.ktor.config.ApplicationConfig
import io.ktor.util.KtorExperimentalAPI

@KtorExperimentalAPI
class RestPublicRepo(private val config: ApplicationConfig): IPublicRepo() {

    override val hallClient: HttpClient
        get() = HttpClient (CIO) {
            defaultRequest {
                host = config.property("hall.host").getString()
                port = config.property("hall.port").getString().toInt()
            }
            install(JsonFeature) {
                serializer = GsonSerializer()
            }
        }

    override val sessionClient: HttpClient
        get() = HttpClient (CIO) {
            defaultRequest {
                host = config.property("session.host").getString()
                port = config.property("session.port").getString().toInt()
            }
            install(JsonFeature) {
                serializer = GsonSerializer()
            }
        }

    override val ticketClient: HttpClient
        get() = HttpClient (CIO) {
            defaultRequest {
                host = config.property("ticket.host").getString()
                port = config.property("ticket.port").getString().toInt()
            }
            install(JsonFeature) {
                serializer = GsonSerializer()
            }
        }
}