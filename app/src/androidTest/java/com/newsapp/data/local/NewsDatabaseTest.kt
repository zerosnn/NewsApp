package com.newsapp.data.local

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.newsapp.data.local.entity.ArticleEntity
import com.newsapp.data.local.entity.SavedArticleEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class NewsDatabaseTest {
    private lateinit var database: NewsDatabase

    @Before
    fun setUp() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            NewsDatabase::class.java,
        ).allowMainThreadQueries().build()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun savedArticlesAreOrderedAndCanBeRemoved() = runTest {
        val older = article("https://example.com/old", "Lama")
        val newer = article("https://example.com/new", "Baru")
        database.articleDao().upsertAll(listOf(older, newer))
        database.savedArticleDao().save(SavedArticleEntity(older.url, savedAt = 1))
        database.savedArticleDao().save(SavedArticleEntity(newer.url, savedAt = 2))

        val saved = database.savedArticleDao().observeSaved().first()
        assertEquals(listOf("Baru", "Lama"), saved.map { it.title })
        assertTrue(database.savedArticleDao().isSaved(newer.url))

        database.savedArticleDao().delete(SavedArticleEntity(newer.url, savedAt = 0))
        assertFalse(database.savedArticleDao().isSaved(newer.url))
    }

    @Test
    fun detailCanBeRestoredFromArticleCache() = runTest {
        val article = article("https://example.com/detail", "Detail")
        database.articleDao().upsertAll(listOf(article))

        val restored = database.articleDao().observeByUrl(article.url).first()

        assertEquals("Detail", restored?.title)
    }

    private fun article(url: String, title: String) = ArticleEntity(
        url = url,
        title = title,
        description = "Deskripsi",
        content = "Isi",
        author = "Penulis",
        sourceName = "Sumber",
        imageUrl = null,
        publishedAt = "2026-06-15T00:00:00Z",
        cachedAt = 1,
    )
}

