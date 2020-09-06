package org.mgaidamak.cinema.public.dao

import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.features.json.GsonSerializer
import io.ktor.client.features.json.JsonFeature
import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf

abstract class MockPublicRepo: IPublicRepo() {

    abstract fun hallGet(url: String): Pair<String, HttpStatusCode>

    override val hallClient: HttpClient
        get() = HttpClient(MockEngine) {
            install(JsonFeature) {
                serializer = GsonSerializer()
            }
            engine {
                addHandler { request ->
                    when (request.method) {
                        HttpMethod.Get -> {
                            val pair = hallGet(request.url.encodedPath)
                            val responseHeaders = headersOf("Content-Type" to listOf(ContentType.Application.Json.toString()))
                            respond(pair.first, pair.second, responseHeaders)
                        }
                        else -> error("Unhandled ${request.method}")
                    }
                }
            }
        }

    override val sessionClient: HttpClient
        get() = HttpClient(MockEngine) {
            engine {
                addHandler { request ->
                    when (request.url.encodedPath) {
                        else -> error("Unhandled ${request.url.encodedPath}")
                    }
                }
            }
        }

    override val ticketClient: HttpClient
        get() = HttpClient(MockEngine) {
            engine {
                addHandler { request ->
                    when (request.url.encodedPath) {
                        else -> error("Unhandled ${request.url.encodedPath}")
                    }
                }
            }
        }
}