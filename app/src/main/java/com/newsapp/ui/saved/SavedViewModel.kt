package com.newsapp.ui.saved

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.newsapp.domain.model.Article
import com.newsapp.domain.repository.NewsRepository
import com.newsapp.ui.home.viewModelFactory
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class SavedViewModel(
    repository: NewsRepository,
) : ViewModel() {
    val articles: StateFlow<List<Article>> = repository.observeSavedArticles()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    companion object {
        fun factory(repository: NewsRepository) = viewModelFactory { SavedViewModel(repository) }
    }
}

