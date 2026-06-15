package com.newsapp.ui.search

import com.newsapp.FakeNewsRepository
import com.newsapp.MainDispatcherRule
import com.newsapp.domain.model.NewsResult
import com.newsapp.testArticle
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SearchViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `blank query does not start a search`() = runTest {
        val viewModel = SearchViewModel(FakeNewsRepository())

        viewModel.updateQuery("   ")
        viewModel.search()

        assertFalse(viewModel.uiState.value.hasSearched)
    }

    @Test
    fun `explicit search returns repository articles`() = runTest {
        val article = testArticle()
        val viewModel = SearchViewModel(
            FakeNewsRepository(searchResult = NewsResult.Success(listOf(article))),
        )

        viewModel.updateQuery("Indonesia")
        viewModel.search()

        assertEquals(listOf(article), viewModel.uiState.value.articles)
        assertEquals("Indonesia", viewModel.uiState.value.query)
    }
}

