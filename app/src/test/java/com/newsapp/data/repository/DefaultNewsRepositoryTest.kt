package com.newsapp.data.repository

import com.newsapp.data.local.dao.ArticleDao
import com.newsapp.data.local.dao.ArticleWithSaved
import com.newsapp.data.local.dao.HomeArticleDao
import com.newsapp.data.local.dao.SavedArticleDao
import com.newsapp.data.local.entity.ArticleEntity
import com.newsapp.data.local.entity.HomeArticleEntity
import com.newsapp.data.local.entity.SavedArticleEntity
import com.newsapp.data.remote.NewsApiService
import com.newsapp.data.remote.dto.ArticleDto
import com.newsapp.data.remote.dto.NewsResponseDto
import com.newsapp.data.remote.dto.SourceDto
import com.newsapp.domain.model.NewsResult
import java.io.IOException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import retrofit2.Response

class DefaultNewsRepositoryTest {
    @Test
    fun `home filters invalid responses and stores valid articles`() = runTest {
        val api = FakeApi(
            Response.success(
                NewsResponseDto(
                    status = "ok",
                    articles = listOf(
                        dto("Berita valid", "https://example.com/valid"),
                        dto("[Removed]", "https://example.com/removed"),
                        dto("Tanpa URL", ""),
                    ),
                ),
            ),
        )
        val homeDao = FakeHomeDao()
        val repository = repository(api, homeDao)

        val result = repository.loadIndonesiaNews() as NewsResult.Success

        assertEquals(listOf("Berita valid"), result.data.map { it.title })
        assertEquals(listOf("https://example.com/valid"), homeDao.articles.map { it.url })
    }

    @Test
    fun `network failure returns cached home with warning`() = runTest {
        val api = FakeApi(
            Response.success(
                NewsResponseDto(
                    status = "ok",
                    articles = listOf(dto("Berita cache", "https://example.com/cache")),
                ),
            ),
        )
        val homeDao = FakeHomeDao()
        val repository = repository(api, homeDao)
        repository.loadIndonesiaNews()
        api.failure = IOException("offline")

        val result = repository.loadIndonesiaNews() as NewsResult.Success

        assertTrue(result.fromCache)
        assertTrue(result.warning.orEmpty().contains("koneksi", ignoreCase = true))
        assertEquals("Berita cache", result.data.single().title)
    }

    private fun repository(api: FakeApi, homeDao: FakeHomeDao) = DefaultNewsRepository(
        api = api,
        apiKey = "test-key",
        articleDao = FakeArticleDao(),
        homeArticleDao = homeDao,
        savedArticleDao = FakeSavedDao(),
        clock = { 100L },
    )

    private fun dto(title: String, url: String) = ArticleDto(
        source = SourceDto(null, "Sumber"),
        author = null,
        title = title,
        description = "Deskripsi",
        url = url,
        urlToImage = null,
        publishedAt = "2026-06-15T00:00:00Z",
        content = "Isi",
    )
}

private class FakeApi(
    var response: Response<NewsResponseDto>,
) : NewsApiService {
    var failure: IOException? = null

    override suspend fun getEverything(
        query: String,
        sortBy: String,
        pageSize: Int,
    ): Response<NewsResponseDto> {
        failure?.let { throw it }
        return response
    }
}

private class FakeArticleDao : ArticleDao {
    private val articles = MutableStateFlow<Map<String, ArticleEntity>>(emptyMap())

    override suspend fun upsertAll(articles: List<ArticleEntity>) {
        this.articles.value += articles.associateBy { it.url }
    }

    override fun observeByUrl(url: String): Flow<ArticleWithSaved?> =
        articles.map { values -> values[url]?.withSaved(false) }
}

private class FakeHomeDao : HomeArticleDao() {
    val articles = mutableListOf<ArticleEntity>()
    private val refs = mutableListOf<HomeArticleEntity>()

    override suspend fun clearHome() {
        refs.clear()
    }

    override suspend fun insertArticles(articles: List<ArticleEntity>) {
        this.articles.removeAll { old -> articles.any { it.url == old.url } }
        this.articles += articles
    }

    override suspend fun insertHomeRefs(refs: List<HomeArticleEntity>) {
        this.refs += refs
    }

    override suspend fun getHome(): List<ArticleWithSaved> =
        refs.sortedBy { it.position }.mapNotNull { ref ->
            articles.find { it.url == ref.articleUrl }?.withSaved(false)
        }
}

private class FakeSavedDao : SavedArticleDao {
    private val saved = MutableStateFlow<Set<String>>(emptySet())

    override suspend fun save(savedArticle: SavedArticleEntity) {
        saved.value += savedArticle.articleUrl
    }

    override suspend fun delete(savedArticle: SavedArticleEntity) {
        saved.value -= savedArticle.articleUrl
    }

    override suspend fun isSaved(url: String): Boolean = url in saved.value

    override fun observeSaved(): Flow<List<ArticleWithSaved>> =
        MutableStateFlow(emptyList())
}

private fun ArticleEntity.withSaved(isSaved: Boolean) = ArticleWithSaved(
    url = url,
    title = title,
    description = description,
    content = content,
    author = author,
    sourceName = sourceName,
    imageUrl = imageUrl,
    publishedAt = publishedAt,
    cachedAt = cachedAt,
    isSaved = isSaved,
)
