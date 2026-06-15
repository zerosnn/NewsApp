package com.newsapp.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.newsapp.data.local.dao.ArticleDao
import com.newsapp.data.local.dao.HomeArticleDao
import com.newsapp.data.local.dao.SavedArticleDao
import com.newsapp.data.local.entity.ArticleEntity
import com.newsapp.data.local.entity.HomeArticleEntity
import com.newsapp.data.local.entity.SavedArticleEntity

@Database(
    entities = [ArticleEntity::class, SavedArticleEntity::class, HomeArticleEntity::class],
    version = 1,
    exportSchema = true,
)
abstract class NewsDatabase : RoomDatabase() {
    abstract fun articleDao(): ArticleDao
    abstract fun homeArticleDao(): HomeArticleDao
    abstract fun savedArticleDao(): SavedArticleDao

    companion object {
        fun create(context: Context): NewsDatabase =
            Room.databaseBuilder(
                context.applicationContext,
                NewsDatabase::class.java,
                "news.db",
            ).build()
    }
}

