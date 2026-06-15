package com.newsapp.domain.model

data class Article(
    val title: String,
    val description: String?,
    val content: String?,
    val author: String?,
    val sourceName: String,
    val url: String,
    val imageUrl: String?,
    val publishedAt: String,
    val isSaved: Boolean = false,
)

