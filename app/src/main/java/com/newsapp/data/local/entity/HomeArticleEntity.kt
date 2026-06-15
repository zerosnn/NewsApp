package com.newsapp.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "home_articles",
    primaryKeys = ["articleUrl"],
    foreignKeys = [
        ForeignKey(
            entity = ArticleEntity::class,
            parentColumns = ["url"],
            childColumns = ["articleUrl"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index("articleUrl")],
)
data class HomeArticleEntity(
    val articleUrl: String,
    val position: Int,
)

