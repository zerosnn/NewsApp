package com.newsapp.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.newsapp.ui.components.ArticleCard
import com.newsapp.ui.components.EmptyState
import com.newsapp.ui.components.ErrorState
import com.newsapp.ui.components.FeaturedArticleCard
import com.newsapp.ui.components.LoadingState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onArticleClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.infoMessage) {
        state.infoMessage?.let { snackbarHostState.showSnackbar(it) }
    }
    LaunchedEffect(state.errorMessage, state.articles) {
        if (state.articles.isNotEmpty()) {
            state.errorMessage?.let { snackbarHostState.showSnackbar(it) }
        }
    }

    Scaffold(
        modifier = modifier,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                windowInsets = WindowInsets(0, 0, 0, 0),
                title = {
                    Text(
                        text = "Berita Hari Ini",
                        fontSize = 23.sp,
                        fontWeight = FontWeight.Bold,
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                ),
            )
        },
    ) { innerPadding ->
        when {
            state.isLoading -> LoadingState(Modifier.padding(innerPadding))
            state.errorMessage != null && state.articles.isEmpty() -> ErrorState(
                message = state.errorMessage.orEmpty(),
                onRetry = { viewModel.loadNews() },
                modifier = Modifier.padding(innerPadding),
            )

            state.articles.isEmpty() -> EmptyState(
                title = "Belum ada berita",
                message = "Tarik ke bawah atau coba lagi beberapa saat.",
                modifier = Modifier.padding(innerPadding),
            )

            else -> PullToRefreshBox(
                isRefreshing = state.isRefreshing,
                onRefresh = { viewModel.loadNews(isRefresh = true) },
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
            ) {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                ) {
                    item {
                        FeaturedArticleCard(
                            article = state.articles.first(),
                            onClick = { onArticleClick(state.articles.first().url) },
                        )
                    }
                    item {
                        Text(
                            text = "Berita terbaru",
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier.padding(top = 8.dp),
                        )
                    }
                    items(state.articles.drop(1), key = { it.url }) { article ->
                        ArticleCard(
                            article = article,
                            onClick = { onArticleClick(article.url) },
                        )
                    }
                }
            }
        }
    }
}
