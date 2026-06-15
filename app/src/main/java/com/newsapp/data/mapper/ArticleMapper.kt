package com.newsapp.data.mapper

import com.newsapp.data.local.dao.ArticleWithSaved
import com.newsapp.data.local.entity.ArticleEntity
import com.newsapp.data.remote.dto.ArticleDto
import com.newsapp.domain.model.Article

fun ArticleDto.toEntity(cachedAt: Long): ArticleEntity? {
    val safeTitle = title?.trim().orEmpty()
    val safeUrl = url?.trim().orEmpty()
    if (safeTitle.isBlank() || safeUrl.isBlank() || safeTitle.equals("[Removed]", true)) {
        return null
    }

    return ArticleEntity(
        url = safeUrl,
        title = safeTitle,
        description = description?.trim()?.takeIf(String::isNotBlank),
        content = content?.trim()?.takeIf(String::isNotBlank),
        author = author?.trim()?.takeIf(String::isNotBlank),
        sourceName = source?.name?.trim()?.takeIf(String::isNotBlank) ?: "Sumber tidak diketahui",
        imageUrl = urlToImage?.trim()?.takeIf(String::isNotBlank),
        publishedAt = publishedAt?.trim().orEmpty(),
        cachedAt = cachedAt,
    )
}

fun ArticleEntity.toDomain(isSaved: Boolean = false) = Article(
    title = title,
    description = description,
    content = content,
    author = author,
    sourceName = sourceName,
    url = url,
    imageUrl = imageUrl,
    publishedAt = publishedAt,
    isSaved = isSaved,
)

fun ArticleWithSaved.toDomain() = Article(
    title = title,
    description = description,
    content = content,
    author = author,
    sourceName = sourceName,
    url = url,
    imageUrl = imageUrl,
    publishedAt = publishedAt,
    isSaved = isSaved,
)

