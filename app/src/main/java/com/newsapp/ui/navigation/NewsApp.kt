package com.newsapp.ui.navigation

import android.content.Intent
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.core.net.toUri
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.newsapp.domain.repository.NewsRepository
import com.newsapp.ui.detail.DetailScreen
import com.newsapp.ui.detail.DetailViewModel
import com.newsapp.ui.home.HomeScreen
import com.newsapp.ui.home.HomeViewModel
import com.newsapp.ui.saved.SavedScreen
import com.newsapp.ui.saved.SavedViewModel
import com.newsapp.ui.search.SearchScreen
import com.newsapp.ui.search.SearchViewModel

private data class BottomDestination(
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val route: Any,
)

private val bottomDestinations = listOf(
    BottomDestination("Beranda", Icons.Filled.Home, Icons.Outlined.Home, HomeRoute),
    BottomDestination("Cari", Icons.Filled.Search, Icons.Outlined.Search, SearchRoute),
    BottomDestination("Tersimpan", Icons.Filled.Bookmark, Icons.Outlined.BookmarkBorder, SavedRoute),
)

@Composable
fun NewsApp(repository: NewsRepository) {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = backStackEntry?.destination
    val showBottomBar = bottomDestinations.any { destination ->
        when (destination.route) {
            HomeRoute -> currentDestination?.hasRoute<HomeRoute>() == true
            SearchRoute -> currentDestination?.hasRoute<SearchRoute>() == true
            SavedRoute -> currentDestination?.hasRoute<SavedRoute>() == true
            else -> false
        }
    }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    bottomDestinations.forEach { destination ->
                        val selected = when (destination.route) {
                            HomeRoute -> currentDestination?.hasRoute<HomeRoute>() == true
                            SearchRoute -> currentDestination?.hasRoute<SearchRoute>() == true
                            SavedRoute -> currentDestination?.hasRoute<SavedRoute>() == true
                            else -> false
                        }
                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                navController.navigate(destination.route) {
                                    popUpTo(HomeRoute) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = {
                                Icon(
                                    imageVector = if (selected) {
                                        destination.selectedIcon
                                    } else {
                                        destination.unselectedIcon
                                    },
                                    contentDescription = destination.label,
                                )
                            },
                            label = { Text(destination.label) },
                        )
                    }
                }
            }
        },
    ) { appPadding ->
        NavHost(
            navController = navController,
            startDestination = HomeRoute,
            modifier = androidx.compose.ui.Modifier.padding(appPadding),
        ) {
            composable<HomeRoute> {
                val homeViewModel: HomeViewModel = viewModel(
                    factory = HomeViewModel.factory(repository),
                )
                HomeScreen(
                    viewModel = homeViewModel,
                    onArticleClick = { navController.navigate(DetailRoute(it)) },
                )
            }
            composable<SearchRoute> {
                val searchViewModel: SearchViewModel = viewModel(
                    factory = SearchViewModel.factory(repository),
                )
                SearchScreen(
                    viewModel = searchViewModel,
                    onArticleClick = { navController.navigate(DetailRoute(it)) },
                )
            }
            composable<SavedRoute> {
                val savedViewModel: SavedViewModel = viewModel(
                    factory = SavedViewModel.factory(repository),
                )
                SavedScreen(
                    viewModel = savedViewModel,
                    onArticleClick = { navController.navigate(DetailRoute(it)) },
                )
            }
            composable<DetailRoute> { backStack ->
                val route = backStack.toRoute<DetailRoute>()
                val detailViewModel: DetailViewModel = viewModel(
                    key = route.url,
                    factory = DetailViewModel.factory(repository, route.url),
                )
                val context = LocalContext.current
                DetailScreen(
                    viewModel = detailViewModel,
                    onBack = navController::navigateUp,
                    onShare = { title, url ->
                        val sendIntent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_SUBJECT, title)
                            putExtra(Intent.EXTRA_TEXT, "$title\n$url")
                        }
                        context.startActivity(
                            Intent.createChooser(sendIntent, "Bagikan berita"),
                        )
                    },
                    onOpenArticle = { url ->
                        context.startActivity(Intent(Intent.ACTION_VIEW, url.toUri()))
                    },
                )
            }
        }
    }
}
