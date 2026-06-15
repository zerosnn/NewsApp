# News App

News App adalah aplikasi Android latihan yang menampilkan berita terbaru tentang Indonesia
dari [NewsAPI](https://newsapi.org/). Aplikasi dibangun dengan arsitektur MVVM, Kotlin
Coroutines, StateFlow, Retrofit, Room, Navigation Compose, Coil, dan Material 3.

## Fitur

- Beranda berisi artikel utama dan 19 berita terbaru.
- Pull-to-refresh dengan fallback ke cache lokal ketika jaringan bermasalah.
- Pencarian berita berdasarkan kata kunci.
- Detail berita, bagikan tautan, dan buka artikel lengkap di browser.
- Bookmark persisten menggunakan Room.
- State loading, kosong, error jaringan, API key salah, dan kuota habis.
- Tema merah untuk mode terang dan gelap.

## Menyiapkan API Key

1. Buat akun dan API key di <https://newsapi.org/>.
2. Buka `local.properties` pada root project.
3. Tambahkan nilai berikut:

```properties
NEWS_API_KEY=api_key_anda
```

Jika file belum tersedia, salin struktur dari `local.properties.example` dan sesuaikan
`sdk.dir`. Tanpa API key, aplikasi tetap dapat dibangun tetapi menampilkan petunjuk
konfigurasi pada layar Beranda.

`local.properties` diabaikan oleh Git. Namun, API key yang dimasukkan ke aplikasi Android
tetap dapat diekstrak dari APK. Untuk aplikasi produksi, panggil NewsAPI melalui backend
sendiri agar kunci tidak dikirim ke perangkat pengguna.

## Menjalankan Project

Project membutuhkan Android Studio 2025.3 atau yang lebih baru, JDK 17+, serta Android SDK
36. Buka folder project di Android Studio, tunggu sinkronisasi Gradle, pilih emulator atau
perangkat, lalu tekan **Run**.

Perintah verifikasi dari terminal:

```bash
export JAVA_HOME="/Applications/Android Studio.app/Contents/jbr/Contents/Home"
./gradlew testDebugUnitTest
./gradlew lintDebug
./gradlew assembleDebug
./gradlew connectedDebugAndroidTest
```

## Struktur

```text
com.newsapp
├── data
│   ├── local        # Room database, DAO, dan entity
│   ├── mapper       # Konversi DTO/entity/domain
│   ├── remote       # Retrofit dan DTO NewsAPI
│   └── repository   # Implementasi sumber data
├── domain
│   ├── model        # Model dan hasil operasi
│   └── repository   # Kontrak repository
└── ui
    ├── components   # Komponen Compose bersama
    ├── home
    ├── search
    ├── saved
    ├── detail
    ├── navigation
    └── theme
```

Beranda memanggil `/v2/everything?q=Indonesia&sortBy=publishedAt&pageSize=20`. Pencarian
baru dijalankan saat tombol **Cari** atau tombol Search pada keyboard ditekan untuk menjaga
kuota API.

## Versi Utama

- Android Gradle Plugin 9.2.0 dan Gradle 9.4.1
- Compose BOM 2026.05.01
- Navigation Compose 2.9.8
- Lifecycle 2.10.0
- Room 2.8.4
- Retrofit 3.0.0
- Coil 3.5.0
