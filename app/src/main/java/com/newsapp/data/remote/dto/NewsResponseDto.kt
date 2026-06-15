package com.newsapp.data.remote.dto

data class NewsResponseDto(
    val status: String,
    val totalResults: Int = 0,
    val articles: List<ArticleDto> = emptyList(),
    val code: String? = null,
    val message: String? = null,
)

data class ArticleDto(
    val source: SourceDto?,
    val author: String?,
    val title: String?,
    val description: String?,
    val url: String?,
    val urlToImage: String?,
    val publishedAt: String?,
    val content: String?,
)

data class SourceDto(
    val id: String?,
    val name: String?,
)

