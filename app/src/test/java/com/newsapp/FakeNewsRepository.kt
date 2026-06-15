package com.newsapp

import com.newsapp.domain.model.Article
import com.newsapp.domain.model.NewsResult
import com.newsapp.domain.repository.NewsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

class FakeNewsRepository(
    var homeResult: NewsResult<List<Article>> = NewsResult.Success(emptyList()),
    var searchResult: NewsResult<List<Article>> = NewsResult.Success(emptyList()),
) : NewsRepository {
    private val articles = MutableStateFlow<Map<String, Article>>(emptyMap())
    private val savedUrls = MutableStateFlow<Set<String>>(emptySet())

    override suspend fun loadIndonesiaNews(): NewsResult<List<Article>> {
        cache(homeResult)
        return homeResult
    }

    override suspend fun searchNews(query: String): NewsResult<List<Article>> {
        cache(searchResult)
        return searchResult
    }

    override fun observeSavedArticles(): Flow<List<Article>> =
        articles.map { values ->
            values.values.filter { it.url in savedUrls.value }.map { it.copy(isSaved = true) }
        }

    override fun observeArticle(url: String): Flow<Article?> =
        articles.map { values -> values[url]?.copy(isSaved = url in savedUrls.value) }

    override suspend fun toggleSaved(url: String) {
        savedUrls.value = if (url in savedUrls.value) savedUrls.value - url else savedUrls.value + url
        articles.value = articles.value.toMap()
    }

    private fun cache(result: NewsResult<List<Article>>) {
        if (result is NewsResult.Success) {
            articles.value = articles.value + result.data.associateBy { it.url }
        }
    }
}

fun testArticle(
    title: String = "Berita Indonesia Hari Ini",
    url: String = "https://example.com/news",
) = Article(
    title = title,
    description = "Ringkasan berita untuk pengujian.",
    content = "Isi berita.",
    author = "Reporter",
    sourceName = "News App",
    url = url,
    imageUrl = null,
    publishedAt = "2026-06-15T00:00:00Z",
)

