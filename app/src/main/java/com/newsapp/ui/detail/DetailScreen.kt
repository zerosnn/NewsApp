package com.newsapp.ui.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.OpenInBrowser
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.newsapp.ui.components.ArticleImage
import com.newsapp.ui.components.ArticleMetadata
import com.newsapp.ui.components.LoadingState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    viewModel: DetailViewModel,
    onBack: () -> Unit,
    onShare: (String, String) -> Unit,
    onOpenArticle: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val article by viewModel.article.collectAsStateWithLifecycle()

    Scaffold(
        modifier = modifier,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            TopAppBar(
                windowInsets = WindowInsets(0, 0, 0, 0),
                title = { Text("Detail berita") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Kembali")
                    }
                },
                actions = {
                    article?.let { current ->
                        IconButton(onClick = { onShare(current.title, current.url) }) {
                            Icon(Icons.Outlined.Share, contentDescription = "Bagikan berita")
                        }
                        IconButton(onClick = viewModel::toggleSaved) {
                            Icon(
                                imageVector = if (current.isSaved) {
                                    Icons.Filled.Bookmark
                                } else {
                                    Icons.Outlined.BookmarkBorder
                                },
                                contentDescription = if (current.isSaved) {
                                    "Hapus dari tersimpan"
                                } else {
                                    "Simpan berita"
                                },
                            )
                        }
                    }
                },
            )
        },
    ) { innerPadding ->
        val current = article
        if (current == null) {
            LoadingState(Modifier.padding(innerPadding))
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState()),
            ) {
                ArticleImage(
                    imageUrl = current.imageUrl,
                    contentDescription = "Gambar ${current.title}",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(260.dp),
                )
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = current.title,
                        style = MaterialTheme.typography.headlineSmall,
                    )
                    Spacer(Modifier.height(12.dp))
                    ArticleMetadata(current)
                    current.author?.let {
                        Text(
                            text = "Oleh $it",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 6.dp),
                        )
                    }
                    Spacer(Modifier.height(22.dp))
                    Text(
                        text = current.description ?: "Ringkasan berita tidak tersedia.",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium,
                    )
                    current.content?.let {
                        Text(
                            text = it.substringBefore(" [+"),
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(top = 18.dp),
                        )
                    }
                    Spacer(Modifier.height(28.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        Button(
                            onClick = { onOpenArticle(current.url) },
                            modifier = Modifier.weight(1f),
                        ) {
                            Icon(Icons.Outlined.OpenInBrowser, contentDescription = null)
                            Text("Baca artikel lengkap", modifier = Modifier.padding(start = 8.dp))
                        }
                        FilledIconButton(onClick = viewModel::toggleSaved) {
                            Icon(
                                imageVector = if (current.isSaved) {
                                    Icons.Filled.Bookmark
                                } else {
                                    Icons.Outlined.BookmarkBorder
                                },
                                contentDescription = "Ubah status simpan",
                            )
                        }
                    }
                }
            }
        }
    }
}
