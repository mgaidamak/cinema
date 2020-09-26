package org.mgaidamak.cinema.public.response

import io.ktor.http.HttpStatusCode

open class Response<T>(val code: HttpStatusCode = HttpStatusCode.OK,
                       val message: String? = null,
                       val data: T? = null) {

    fun succeed() = HttpStatusCode.OK == code
}
