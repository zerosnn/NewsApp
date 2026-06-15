package com.newsapp.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.newsapp.domain.model.Article
import com.newsapp.domain.repository.NewsRepository
import com.newsapp.ui.home.viewModelFactory
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class DetailViewModel(
    private val repository: NewsRepository,
    private val articleUrl: String,
) : ViewModel() {
    val article: StateFlow<Article?> = repository.observeArticle(articleUrl)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    fun toggleSaved() {
        viewModelScope.launch {
            repository.toggleSaved(articleUrl)
        }
    }

    companion object {
        fun factory(repository: NewsRepository, articleUrl: String) =
            viewModelFactory { DetailViewModel(repository, articleUrl) }
    }
}

