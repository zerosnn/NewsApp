package com.newsapp.domain.repository

import com.newsapp.domain.model.Article
import com.newsapp.domain.model.NewsResult
import kotlinx.coroutines.flow.Flow

interface NewsRepository {
    suspend fun loadIndonesiaNews(): NewsResult<List<Article>>
    suspend fun searchNews(query: String): NewsResult<List<Article>>
    fun observeSavedArticles(): Flow<List<Article>>
    fun observeArticle(url: String): Flow<Article?>
    suspend fun toggleSaved(url: String)
}

