package com.newsapp.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.newsapp.FakeAndroidRepository
import com.newsapp.ui.navigation.NewsApp
import com.newsapp.ui.theme.NewsAppTheme
import org.junit.Rule
import org.junit.Test

class NewsAppComposeTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun articleCanBeOpenedSavedAndFoundInSavedTab() {
        setApp(FakeAndroidRepository())

        composeRule.onNodeWithText("Berita Indonesia Hari Ini").performClick()
        composeRule.onNodeWithText("Detail berita").assertIsDisplayed()
        composeRule.onNodeWithContentDescription("Simpan berita").performClick()
        composeRule.onNodeWithContentDescription("Kembali").performClick()
        composeRule.onNodeWithText("Tersimpan").performClick()

        composeRule.onNodeWithText("Berita Indonesia Hari Ini").assertIsDisplayed()
    }

    @Test
    fun searchRunsOnlyAfterButtonClick() {
        setApp(FakeAndroidRepository())

        composeRule.onNodeWithText("Cari").performClick()
        composeRule.onNodeWithText("Kata kunci").performTextInput("teknologi")
        composeRule.onNodeWithText("Temukan berita").assertIsDisplayed()
        composeRule.onNodeWithTag("search_button").performClick()

        composeRule.onNodeWithText("Hasil untuk teknologi").assertIsDisplayed()
    }

    @Test
    fun homeErrorCanBeRetried() {
        setApp(FakeAndroidRepository(failFirstHomeLoad = true))

        composeRule.onNodeWithText("Gagal memuat berita").assertIsDisplayed()
        composeRule.onNodeWithText("Coba lagi").performClick()

        composeRule.onNodeWithText("Berita Indonesia Hari Ini").assertIsDisplayed()
    }

    private fun setApp(repository: FakeAndroidRepository) {
        composeRule.setContent {
            NewsAppTheme {
                NewsApp(repository)
            }
        }
        composeRule.waitForIdle()
    }
}
