package com.newsapp.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.newsapp.data.local.entity.ArticleEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ArticleDao {
    @Upsert
    suspend fun upsertAll(articles: List<ArticleEntity>)

    @Query(
        """
        SELECT a.*, CASE WHEN s.articleUrl IS NULL THEN 0 ELSE 1 END AS isSaved
        FROM articles a
        LEFT JOIN saved_articles s ON a.url = s.articleUrl
        WHERE a.url = :url
        LIMIT 1
        """,
    )
    fun observeByUrl(url: String): Flow<ArticleWithSaved?>
}

data class ArticleWithSaved(
    val url: String,
    val title: String,
    val description: String?,
    val content: String?,
    val author: String?,
    val sourceName: String,
    val imageUrl: String?,
    val publishedAt: String,
    val cachedAt: Long,
    val isSaved: Boolean,
)

