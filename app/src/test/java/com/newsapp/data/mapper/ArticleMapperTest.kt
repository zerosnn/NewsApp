package com.newsapp.data.mapper

import com.newsapp.data.remote.dto.ArticleDto
import com.newsapp.data.remote.dto.SourceDto
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ArticleMapperTest {
    @Test
    fun `valid article maps with source fallback`() {
        val dto = ArticleDto(
            source = SourceDto(id = null, name = null),
            author = "  Penulis  ",
            title = "  Judul berita  ",
            description = " Ringkasan ",
            url = " https://example.com/news ",
            urlToImage = "",
            publishedAt = "2026-06-15T00:00:00Z",
            content = null,
        )

        val entity = dto.toEntity(cachedAt = 123L)

        requireNotNull(entity)
        assertEquals("Judul berita", entity.title)
        assertEquals("https://example.com/news", entity.url)
        assertEquals("Sumber tidak diketahui", entity.sourceName)
        assertEquals("Penulis", entity.author)
        assertNull(entity.imageUrl)
        assertEquals(123L, entity.cachedAt)
    }

    @Test
    fun `removed blank and url-less articles are rejected`() {
        assertNull(article(title = "[Removed]").toEntity(0))
        assertNull(article(title = " ").toEntity(0))
        assertNull(article(url = "").toEntity(0))
    }

    private fun article(
        title: String? = "Berita",
        url: String? = "https://example.com",
    ) = ArticleDto(
        source = SourceDto(null, "Sumber"),
        author = null,
        title = title,
        description = null,
        url = url,
        urlToImage = null,
        publishedAt = null,
        content = null,
    )
}

