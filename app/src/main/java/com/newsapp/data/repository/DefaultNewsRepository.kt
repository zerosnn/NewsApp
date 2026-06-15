package com.newsapp.data.repository

import com.newsapp.data.local.dao.ArticleDao
import com.newsapp.data.local.dao.HomeArticleDao
import com.newsapp.data.local.dao.SavedArticleDao
import com.newsapp.data.local.entity.SavedArticleEntity
import com.newsapp.data.mapper.toDomain
import com.newsapp.data.mapper.toEntity
import com.newsapp.data.remote.NewsApiService
import com.newsapp.data.remote.dto.NewsResponseDto
import com.newsapp.domain.model.Article
import com.newsapp.domain.model.NewsFailure
import com.newsapp.domain.model.NewsResult
import com.newsapp.domain.repository.NewsRepository
import java.io.IOException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import retrofit2.Response

class DefaultNewsRepository(
    private val api: NewsApiService,
    private val apiKey: String,
    private val articleDao: ArticleDao,
    private val homeArticleDao: HomeArticleDao,
    private val savedArticleDao: SavedArticleDao,
    private val clock: () -> Long = System::currentTimeMillis,
) : NewsRepository {

    override suspend fun loadIndonesiaNews(): NewsResult<List<Article>> {
        if (apiKey.isBlank()) return NewsResult.Error(NewsFailure.MissingApiKey)

        return try {
            val result = api.getEverything(query = "Indonesia")
            if (result.isSuccessful) {
                val entities = result.body().orEmptyArticles(clock())
                homeArticleDao.replaceHome(entities)
                NewsResult.Success(homeArticleDao.getHome().map { it.toDomain() })
            } else {
                cachedHomeOrError(result.toFailure())
            }
        } catch (_: IOException) {
            cachedHomeOrError(NewsFailure.Network)
        } catch (error: Exception) {
            cachedHomeOrError(
                NewsFailure.Unknown(error.message ?: "Terjadi kesalahan yang tidak diketahui."),
            )
        }
    }

    override suspend fun searchNews(query: String): NewsResult<List<Article>> {
        if (apiKey.isBlank()) return NewsResult.Error(NewsFailure.MissingApiKey)

        return try {
            val result = api.getEverything(query = query)
            if (result.isSuccessful) {
                val entities = result.body().orEmptyArticles(clock())
                articleDao.upsertAll(entities)
                NewsResult.Success(entities.map { it.toDomain() })
            } else {
                NewsResult.Error(result.toFailure())
            }
        } catch (_: IOException) {
            NewsResult.Error(NewsFailure.Network)
        } catch (error: Exception) {
            NewsResult.Error(
                NewsFailure.Unknown(error.message ?: "Terjadi kesalahan yang tidak diketahui."),
            )
        }
    }

    override fun observeSavedArticles(): Flow<List<Article>> =
        savedArticleDao.observeSaved().map { articles -> articles.map { it.toDomain() } }

    override fun observeArticle(url: String): Flow<Article?> =
        articleDao.observeByUrl(url).map { it?.toDomain() }

    override suspend fun toggleSaved(url: String) {
        if (savedArticleDao.isSaved(url)) {
            savedArticleDao.delete(SavedArticleEntity(articleUrl = url, savedAt = 0))
        } else {
            savedArticleDao.save(SavedArticleEntity(articleUrl = url, savedAt = clock()))
        }
    }

    private suspend fun cachedHomeOrError(failure: NewsFailure): NewsResult<List<Article>> {
        val cached = homeArticleDao.getHome().map { it.toDomain() }
        return if (cached.isNotEmpty()) {
            NewsResult.Success(
                data = cached,
                fromCache = true,
                warning = "${failure.message} Menampilkan berita tersimpan sementara.",
            )
        } else {
            NewsResult.Error(failure)
        }
    }
}

internal fun NewsResponseDto?.orEmptyArticles(cachedAt: Long) =
    this?.articles.orEmpty().mapNotNull { it.toEntity(cachedAt) }

internal fun Response<NewsResponseDto>.toFailure(): NewsFailure = when (code()) {
    401, 403 -> NewsFailure.Unauthorized
    429 -> NewsFailure.RateLimited
    in 500..599 -> NewsFailure.Server
    else -> {
        val apiMessage = errorBody()?.string()?.takeIf(String::isNotBlank)
        NewsFailure.Unknown(apiMessage ?: "Permintaan berita gagal (${code()}).")
    }
}

