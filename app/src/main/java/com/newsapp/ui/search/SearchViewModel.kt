package com.newsapp.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.newsapp.domain.model.Article
import com.newsapp.domain.model.NewsResult
import com.newsapp.domain.repository.NewsRepository
import com.newsapp.ui.home.viewModelFactory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SearchUiState(
    val query: String = "",
    val hasSearched: Boolean = false,
    val isLoading: Boolean = false,
    val articles: List<Article> = emptyList(),
    val errorMessage: String? = null,
)

class SearchViewModel(
    private val repository: NewsRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    fun updateQuery(query: String) {
        _uiState.update { it.copy(query = query, errorMessage = null) }
    }

    fun search() {
        val query = _uiState.value.query.trim()
        if (query.isBlank() || _uiState.value.isLoading) return

        _uiState.update {
            it.copy(isLoading = true, hasSearched = true, articles = emptyList(), errorMessage = null)
        }
        viewModelScope.launch {
            when (val result = repository.searchNews(query)) {
                is NewsResult.Success -> _uiState.update {
                    it.copy(isLoading = false, articles = result.data)
                }

                is NewsResult.Error -> _uiState.update {
                    it.copy(isLoading = false, errorMessage = result.failure.message)
                }
            }
        }
    }

    companion object {
        fun factory(repository: NewsRepository) = viewModelFactory { SearchViewModel(repository) }
    }
}

