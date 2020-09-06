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

    private fun mockClient(getter: (String) -> Pair<String, HttpStatusCode>,
                           poster: (String) -> Pair<String, HttpStatusCode>,
                           deleter: (String) -> Pair<String, HttpStatusCode>) = HttpClient(MockEngine) {
            install(JsonFeature) {
                serializer = GsonSerializer()
            }
            engine {
                addHandler { request ->
                    when (request.method) {
                        HttpMethod.Get -> {
                            val pair = getter(request.url.encodedPath)
                            val responseHeaders = headersOf("Content-Type" to listOf(ContentType.Application.Json.toString()))
                            respond(pair.first, pair.second, responseHeaders)
                        }
                        HttpMethod.Post -> {
                            val pair = poster(request.url.encodedPath)
                            val responseHeaders = headersOf("Content-Type" to listOf(ContentType.Application.Json.toString()))
                            respond(pair.first, pair.second, responseHeaders)
                        }
                        HttpMethod.Delete -> {
                            val pair = deleter(request.url.encodedPath)
                            val responseHeaders = headersOf("Content-Type" to listOf(ContentType.Application.Json.toString()))
                            respond(pair.first, pair.second, responseHeaders)
                        }
                        else -> error("Unhandled ${request.method}")
                    }
                }
            }
        }

    abstract fun hallGet(url: String): Pair<String, HttpStatusCode>
    abstract fun sessionGet(url: String): Pair<String, HttpStatusCode>
    abstract fun ticketGet(url: String): Pair<String, HttpStatusCode>

    abstract fun hallPost(url: String): Pair<String, HttpStatusCode>
    abstract fun sessionPost(url: String): Pair<String, HttpStatusCode>
    abstract fun ticketPost(url: String): Pair<String, HttpStatusCode>

    abstract fun hallDelete(url: String): Pair<String, HttpStatusCode>
    abstract fun sessionDelete(url: String): Pair<String, HttpStatusCode>
    abstract fun ticketDelete(url: String): Pair<String, HttpStatusCode>

    override val hallClient: HttpClient
        get() = mockClient(this::hallGet, this::hallPost, this::hallDelete)

    override val sessionClient: HttpClient
        get() = mockClient(this::sessionGet, this::sessionPost, this::sessionDelete)

    override val ticketClient: HttpClient
        get() = mockClient(this::ticketGet, this::ticketPost, this::ticketDelete)
}