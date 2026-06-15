package com.newsapp.domain.model

sealed interface NewsFailure {
    val message: String

    data object MissingApiKey : NewsFailure {
        override val message =
            "API key belum dikonfigurasi. Tambahkan NEWS_API_KEY ke local.properties."
    }

    data object Unauthorized : NewsFailure {
        override val message = "API key NewsAPI tidak valid atau tidak memiliki akses."
    }

    data object RateLimited : NewsFailure {
        override val message = "Kuota NewsAPI telah habis. Coba lagi setelah kuota tersedia."
    }

    data object Network : NewsFailure {
        override val message = "Tidak dapat terhubung ke internet. Periksa koneksi Anda."
    }

    data object Server : NewsFailure {
        override val message = "Server berita sedang bermasalah. Silakan coba lagi."
    }

    data class Unknown(override val message: String) : NewsFailure
}

sealed interface NewsResult<out T> {
    data class Success<T>(
        val data: T,
        val fromCache: Boolean = false,
        val warning: String? = null,
    ) : NewsResult<T>

    data class Error(val failure: NewsFailure) : NewsResult<Nothing>
}

