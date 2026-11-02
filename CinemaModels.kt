package com.example.data.model

data class User(
    val id: String,
    val name: String,
    val email: String,
    val phone: String,
    val password: String,
    val avatarUrl: String = "",
    val isPremium: Boolean = false,
    val subscriptionTier: String = "Alfa Bepul",
    val premiumExpiry: Long = 0L, // timestamp millis
    val isAdmin: Boolean = false,
    val isBanned: Boolean = false
) {
    fun isPremiumActive(): Boolean {
        if (!isPremium) return false
        if (isAdmin) return true
        return premiumExpiry > System.currentTimeMillis()
    }
}

data class Movie(
    val id: String,
    val title: String,
    val originalTitle: String,
    val description: String,
    val backdropUrl: String,
    val posterUrl: String,
    val videoUrl: String = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4",
    val genres: List<String>,
    val year: Int,
    val rating: Double,
    val ageRating: String, // "12+", "16+", "18+"
    val durationMinutes: Int,
    val director: String,
    val cast: List<String>,
    val isTrending: Boolean = false,
    val isTop10: Boolean = false,
    val top10Rank: Int? = null,
    val isPremier2026: Boolean = false,
    val isUzbek: Boolean = false,
    val is4K: Boolean = true,
    val audioTracks: List<String> = listOf("O'zbekcha (Professional)", "Ruscha", "Inglizcha (Original)"),
    val subtitles: List<String> = listOf("O'zbekcha", "Ruscha", "Inglizcha"),
    val watchProgressMinutes: Int = 0
) {
    val durationFormatted: String
        get() {
            val h = durationMinutes / 60
            val m = durationMinutes % 60
            return if (h > 0) "${h}s ${m}d" else "${m} daq"
        }
}

data class PromoCode(
    val code: String,
    val title: String,
    val durationDays: Int,
    val tier: String = "Alfa VIP 4K",
    val maxUses: Int = 1000,
    val usedCount: Int = 0,
    val isActive: Boolean = true
)

data class Review(
    val id: String,
    val movieId: String,
    val userName: String,
    val userAvatar: String = "",
    val rating: Int,
    val comment: String,
    val date: String
)

data class DownloadItem(
    val id: String,
    val movieId: String,
    val title: String,
    val posterUrl: String,
    val sizeMb: Int,
    val progress: Float = 1.0f,
    val status: String = "Yuklab olingan" // "Yuklab olingan", "Yuklanmoqda"
)

enum class SubscriptionPlan(
    val title: String,
    val priceText: String,
    val durationDays: Int,
    val features: List<String>,
    val badge: String? = null
) {
    START(
        title = "Alfa Start",
        priceText = "29 000 so'm / oy",
        durationDays = 30,
        features = listOf(
            "Full HD 1080p sifat",
            "Barcha kinolar va seriallar",
            "1 ta qurilma",
            "Reklamasiz tomosha"
        )
    ),
    PRO(
        title = "Alfa PRO 4K",
        priceText = "79 000 so'm / 3 oy",
        durationDays = 90,
        features = listOf(
            "Ultra HD 4K & HDR sifat",
            "Dolby Atmos audio",
            "O'zbekcha eksklyuziv premyeralar",
            "3 ta qurilmada bir vaqtda",
            "Oflayn yuklab olish"
        ),
        badge = "ENG MASHHUR"
    ),
    VIP_FAMILY(
        title = "Alfa VIP Premium Oila",
        priceText = "199 000 so'm / yil",
        durationDays = 365,
        features = listOf(
            "Barcha 4K kinolar va TV kanallar",
            "5 ta qurilma (Smart TV + Telefon)",
            "Bolalar rejimi va PIN kod",
            "Cheksiz oflayn yuklash",
            "24/7 VIP qo'llab-quvvatlash"
        ),
        badge = "ENG FOYDALI"
    )
}
