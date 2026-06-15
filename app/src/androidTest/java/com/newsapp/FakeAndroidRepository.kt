package com.newsapp

import com.newsapp.domain.model.Article
import com.newsapp.domain.model.NewsFailure
import com.newsapp.domain.model.NewsResult
import com.newsapp.domain.repository.NewsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

class FakeAndroidRepository(
    private val article: Article = androidTestArticle(),
    private val failFirstHomeLoad: Boolean = false,
) : NewsRepository {
    private val articles = MutableStateFlow(mapOf(article.url to article))
    private val saved = MutableStateFlow<Set<String>>(emptySet())
    private var homeCalls = 0

    override suspend fun loadIndonesiaNews(): NewsResult<List<Article>> {
        homeCalls++
        return if (failFirstHomeLoad && homeCalls == 1) {
            NewsResult.Error(NewsFailure.Network)
        } else {
            NewsResult.Success(listOf(article))
        }
    }

    override suspend fun searchNews(query: String): NewsResult<List<Article>> =
        NewsResult.Success(listOf(article.copy(title = "Hasil untuk $query")))

    override fun observeSavedArticles(): Flow<List<Article>> =
        combine(articles, saved) { values, savedUrls ->
            values.values.filter { it.url in savedUrls }.map { it.copy(isSaved = true) }
        }

    override fun observeArticle(url: String): Flow<Article?> =
        combine(articles, saved) { values, savedUrls ->
            values[url]?.copy(isSaved = url in savedUrls)
        }

    override suspend fun toggleSaved(url: String) {
        saved.value = if (url in saved.value) saved.value - url else saved.value + url
    }
}

fun androidTestArticle() = Article(
    title = "Berita Indonesia Hari Ini",
    description = "Ringkasan berita.",
    content = "Isi berita untuk pengujian.",
    author = "Reporter",
    sourceName = "News App",
    url = "https://example.com/news",
    imageUrl = null,
    publishedAt = "2026-06-15T00:00:00Z",
)

