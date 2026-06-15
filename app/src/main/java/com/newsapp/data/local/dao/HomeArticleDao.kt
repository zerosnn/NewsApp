package com.newsapp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.newsapp.data.local.entity.ArticleEntity
import com.newsapp.data.local.entity.HomeArticleEntity

@Dao
abstract class HomeArticleDao {
    @Query("DELETE FROM home_articles")
    protected abstract suspend fun clearHome()

    @Upsert
    protected abstract suspend fun insertArticles(articles: List<ArticleEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    protected abstract suspend fun insertHomeRefs(refs: List<HomeArticleEntity>)

    @Transaction
    open suspend fun replaceHome(articles: List<ArticleEntity>) {
        clearHome()
        insertArticles(articles)
        insertHomeRefs(
            articles.mapIndexed { index, article ->
                HomeArticleEntity(articleUrl = article.url, position = index)
            },
        )
    }

    @Query(
        """
        SELECT a.*, CASE WHEN s.articleUrl IS NULL THEN 0 ELSE 1 END AS isSaved
        FROM home_articles h
        INNER JOIN articles a ON h.articleUrl = a.url
        LEFT JOIN saved_articles s ON a.url = s.articleUrl
        ORDER BY h.position ASC
        """,
    )
    abstract suspend fun getHome(): List<ArticleWithSaved>
}
