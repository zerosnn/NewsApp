package com.newsapp

import android.app.Application

class NewsApplication : Application() {
    val container by lazy { AppContainer(this) }
}
