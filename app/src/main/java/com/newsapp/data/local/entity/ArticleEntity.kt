package com.newsapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "articles")
data class ArticleEntity(
    @PrimaryKey val url: String,
    val title: String,
    val description: String?,
    val content: String?,
    val author: String?,
    val sourceName: String,
    val imageUrl: String?,
    val publishedAt: String,
    val cachedAt: Long,
)

