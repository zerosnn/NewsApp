package com.newsapp.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.newsapp.data.local.entity.SavedArticleEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SavedArticleDao {
    @Upsert
    suspend fun save(savedArticle: SavedArticleEntity)

    @Delete
    suspend fun delete(savedArticle: SavedArticleEntity)

    @Query("SELECT EXISTS(SELECT 1 FROM saved_articles WHERE articleUrl = :url)")
    suspend fun isSaved(url: String): Boolean

    @Query(
        """
        SELECT a.*, 1 AS isSaved
        FROM saved_articles s
        INNER JOIN articles a ON s.articleUrl = a.url
        ORDER BY s.savedAt DESC
        """,
    )
    fun observeSaved(): Flow<List<ArticleWithSaved>>
}

