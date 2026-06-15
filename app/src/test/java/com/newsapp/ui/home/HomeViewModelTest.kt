package com.newsapp.ui.home

import com.newsapp.FakeNewsRepository
import com.newsapp.MainDispatcherRule
import com.newsapp.domain.model.NewsFailure
import com.newsapp.domain.model.NewsResult
import com.newsapp.testArticle
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `initial load exposes articles`() = runTest {
        val article = testArticle()
        val viewModel = HomeViewModel(
            FakeNewsRepository(homeResult = NewsResult.Success(listOf(article))),
        )

        assertFalse(viewModel.uiState.value.isLoading)
        assertEquals(listOf(article), viewModel.uiState.value.articles)
        assertNull(viewModel.uiState.value.errorMessage)
    }

    @Test
    fun `error is exposed and retry replaces it with content`() = runTest {
        val repository = FakeNewsRepository(
            homeResult = NewsResult.Error(NewsFailure.Network),
        )
        val viewModel = HomeViewModel(repository)
        assertEquals(NewsFailure.Network.message, viewModel.uiState.value.errorMessage)

        val article = testArticle()
        repository.homeResult = NewsResult.Success(listOf(article))
        viewModel.loadNews()

        assertEquals(listOf(article), viewModel.uiState.value.articles)
        assertNull(viewModel.uiState.value.errorMessage)
    }
}

