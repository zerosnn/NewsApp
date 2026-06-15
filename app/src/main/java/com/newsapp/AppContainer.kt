package com.newsapp

import android.content.Context
import com.newsapp.data.local.NewsDatabase
import com.newsapp.data.remote.NewsApiFactory
import com.newsapp.data.repository.DefaultNewsRepository
import com.newsapp.domain.repository.NewsRepository

class AppContainer(context: Context) {
    private val database = NewsDatabase.create(context)
    private val apiService = NewsApiFactory.create(BuildConfig.NEWS_API_KEY)

    val repository: NewsRepository = DefaultNewsRepository(
        api = apiService,
        apiKey = BuildConfig.NEWS_API_KEY,
        articleDao = database.articleDao(),
        homeArticleDao = database.homeArticleDao(),
        savedArticleDao = database.savedArticleDao(),
    )
}

