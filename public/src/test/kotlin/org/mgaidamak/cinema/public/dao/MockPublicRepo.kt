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
                           deleter: (String) -> Pair<String, HttpStatusCode>,
                           patcher: (String) -> Pair<String, HttpStatusCode>) = HttpClient(MockEngine) {
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
                        HttpMethod.Patch -> {
                            val pair = deleter(request.url.encodedPath)
                            val responseHeaders = headersOf("Content-Type" to listOf(ContentType.Application.Json.toString()))
                            respond(pair.first, pair.second, responseHeaders)
                        }
                        else -> error("Unhandled ${request.method}")
                    }
                }
            }
        }

    open fun hallGet(url: String): Pair<String, HttpStatusCode> = "" to HttpStatusCode.NotFound
    open fun sessionGet(url: String): Pair<String, HttpStatusCode> = "" to HttpStatusCode.NotFound
    open fun ticketGet(url: String): Pair<String, HttpStatusCode> = "" to HttpStatusCode.NotFound

    open fun hallPost(url: String): Pair<String, HttpStatusCode> = "" to HttpStatusCode.NotFound
    open fun sessionPost(url: String): Pair<String, HttpStatusCode> = "" to HttpStatusCode.NotFound
    open fun ticketPost(url: String): Pair<String, HttpStatusCode> = "" to HttpStatusCode.NotFound

    open fun hallDelete(url: String): Pair<String, HttpStatusCode> = "" to HttpStatusCode.NotFound
    open fun sessionDelete(url: String): Pair<String, HttpStatusCode> = "" to HttpStatusCode.NotFound
    open fun ticketDelete(url: String): Pair<String, HttpStatusCode> = "" to HttpStatusCode.NotFound

    open fun hallPatch(url: String): Pair<String, HttpStatusCode> = "" to HttpStatusCode.NotFound
    open fun sessionPatch(url: String): Pair<String, HttpStatusCode> = "" to HttpStatusCode.NotFound
    open fun ticketPatch(url: String): Pair<String, HttpStatusCode> = "" to HttpStatusCode.NotFound

    override val hallClient: HttpClient
        get() = mockClient(this::hallGet, this::hallPost, this::hallDelete, this::hallPatch)

    override val sessionClient: HttpClient
        get() = mockClient(this::sessionGet, this::sessionPost, this::sessionDelete, this::sessionPatch)

    override val ticketClient: HttpClient
        get() = mockClient(this::ticketGet, this::ticketPost, this::ticketDelete, this::ticketPatch)
}