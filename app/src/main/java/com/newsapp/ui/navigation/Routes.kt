package com.newsapp.ui.navigation

import kotlinx.serialization.Serializable

@Serializable
data object HomeRoute

@Serializable
data object SearchRoute

@Serializable
data object SavedRoute

@Serializable
data class DetailRoute(val url: String)

