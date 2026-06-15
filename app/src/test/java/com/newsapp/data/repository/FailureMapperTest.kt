package com.newsapp.data.repository

import com.newsapp.data.remote.dto.NewsResponseDto
import com.newsapp.domain.model.NewsFailure
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Test
import retrofit2.Response

class FailureMapperTest {
    @Test
    fun `maps authentication and quota errors`() {
        assertSame(NewsFailure.Unauthorized, response(401).toFailure())
        assertSame(NewsFailure.Unauthorized, response(403).toFailure())
        assertSame(NewsFailure.RateLimited, response(429).toFailure())
        assertSame(NewsFailure.Server, response(503).toFailure())
    }

    @Test
    fun `keeps unknown API error body`() {
        val failure = response(400, """{"message":"bad request"}""").toFailure()

        assertEquals("""{"message":"bad request"}""", (failure as NewsFailure.Unknown).message)
    }

    private fun response(code: Int, body: String = ""): Response<NewsResponseDto> =
        Response.error(code, body.toResponseBody("application/json".toMediaType()))
}

