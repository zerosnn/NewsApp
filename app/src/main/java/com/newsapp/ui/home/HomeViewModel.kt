package com.newsapp.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.newsapp.domain.model.Article
import com.newsapp.domain.model.NewsResult
import com.newsapp.domain.repository.NewsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class HomeUiState(
    val articles: List<Article> = emptyList(),
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val errorMessage: String? = null,
    val infoMessage: String? = null,
)

class HomeViewModel(
    private val repository: NewsRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadNews()
    }

    fun loadNews(isRefresh: Boolean = false) {
        if (_uiState.value.isRefreshing) return
        _uiState.update {
            it.copy(
                isLoading = !isRefresh && it.articles.isEmpty(),
                isRefreshing = isRefresh,
                errorMessage = null,
                infoMessage = null,
            )
        }
        viewModelScope.launch {
            when (val result = repository.loadIndonesiaNews()) {
                is NewsResult.Success -> _uiState.update {
                    it.copy(
                        articles = result.data,
                        isLoading = false,
                        isRefreshing = false,
                        infoMessage = result.warning,
                    )
                }

                is NewsResult.Error -> _uiState.update {
                    it.copy(
                        isLoading = false,
                        isRefreshing = false,
                        errorMessage = result.failure.message,
                    )
                }
            }
        }
    }

    companion object {
        fun factory(repository: NewsRepository) = viewModelFactory { HomeViewModel(repository) }
    }
}

internal inline fun <reified T : ViewModel> viewModelFactory(crossinline create: () -> T) =
    object : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <VM : ViewModel> create(modelClass: Class<VM>): VM = create() as VM
    }

