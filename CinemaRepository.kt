package com.example.data.repository

import com.example.data.model.DownloadItem
import com.example.data.model.Movie
import com.example.data.model.PromoCode
import com.example.data.model.Review
import com.example.data.model.SubscriptionPlan
import com.example.data.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class CinemaRepository {

    private val initialMovies = listOf(
        Movie(
            id = "m1",
            title = "Dune: 2-Qism (Sahro Saltanati)",
            originalTitle = "Dune: Part Two",
            description = "Pol Atreydes Chani va Fremenlar bilan birlashib, uning oilasini yo'q qilgan fitnachilardan o'ch olish yo'lida yuradi. U hayotidagi sevgisi va koinot taqdiri o'rtasida tanlov qilishi kerak.",
            backdropUrl = "https://images.unsplash.com/photo-1534447677768-be436bb09401?q=80&w=1200&auto=format&fit=crop",
            posterUrl = "https://images.unsplash.com/photo-1518709268805-4e9042af9f23?q=80&w=600&auto=format&fit=crop",
            genres = listOf("Fantastika", "Jangari", "Sarguzasht"),
            year = 2024,
            rating = 8.9,
            ageRating = "16+",
            durationMinutes = 166,
            director = "Deni Vilnyov",
            cast = listOf("Timoti Shalame", "Zendeya", "Rebekka Fergyuson", "Xavier Bardem"),
            isTrending = true,
            isTop10 = true,
            top10Rank = 1,
            isPremier2026 = false,
            is4K = true,
            watchProgressMinutes = 45
        ),
        Movie(
            id = "m2",
            title = "Mendirman Jaloliddin: Buyuk G'alaba",
            originalTitle = "Mendirman Jaloliddin",
            description = "Xorazmshohlar davlatining so'nggi hukmdori va mard bahodiri Jaloliddin Manguberdi Chingizxon boshchiligidagi mo'g'ul bosqinchilariga qarshi shiddatli kurash olib boradi.",
            backdropUrl = "https://images.unsplash.com/photo-1578632767115-351597cf2477?q=80&w=1200&auto=format&fit=crop",
            posterUrl = "https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?q=80&w=600&auto=format&fit=crop",
            genres = listOf("Tarixiy", "Jangari", "Drama", "O'zbek kinolari"),
            year = 2025,
            rating = 9.2,
            ageRating = "16+",
            durationMinutes = 140,
            director = "Metin Gunay",
            cast = listOf("Emre Kiviljim", "Yulduz Rajabova", "Javohir Zokirov", "Toxir Saidov"),
            isTrending = true,
            isTop10 = true,
            top10Rank = 2,
            isUzbek = true,
            is4K = true,
            watchProgressMinutes = 80
        ),
        Movie(
            id = "m3",
            title = "Oppenheimer",
            originalTitle = "Oppenheimer",
            description = "Nazariy fizik J. Robert Oppenxaymerning ikkinchi jahon urushi paytida atom bombasini yaratish loyihasiga boshchilik qilgan mashaqqatli hayot yo'li.",
            backdropUrl = "https://images.unsplash.com/photo-1509198397868-475647b2a1e5?q=80&w=1200&auto=format&fit=crop",
            posterUrl = "https://images.unsplash.com/photo-1440404653325-ab127d49abc1?q=80&w=600&auto=format&fit=crop",
            genres = listOf("Biografiya", "Drama", "Tarixiy"),
            year = 2023,
            rating = 8.9,
            ageRating = "18+",
            durationMinutes = 180,
            director = "Kristofer Nolan",
            cast = listOf("Killian Myorfi", "Emili Blant", "Mett Deymon", "Robert Dauni Jr."),
            isTrending = true,
            isTop10 = true,
            top10Rank = 3,
            is4K = true
        ),
        Movie(
            id = "m4",
            title = "Avatar: Olov va Kul (2026 Premyera)",
            originalTitle = "Avatar: Fire and Ash",
            description = "Pandora sayyorasining sirli vulqon hududlarida yashovchi shafqatsiz Kul qabilasi bilan to'qnashuv. Jeyk Salli va Neytiri oilasi yangi xavf-xatarga duch keladi.",
            backdropUrl = "https://images.unsplash.com/photo-1518709268805-4e9042af9f23?q=80&w=1200&auto=format&fit=crop",
            posterUrl = "https://images.unsplash.com/photo-1534447677768-be436bb09401?q=80&w=600&auto=format&fit=crop",
            genres = listOf("Fantastika", "Sarguzasht", "Jangari"),
            year = 2026,
            rating = 9.4,
            ageRating = "12+",
            durationMinutes = 190,
            director = "Jeyms Kemeron",
            cast = listOf("Sem Uortington", "Zoi Saldana", "Sigurni Uiver"),
            isTrending = true,
            isTop10 = true,
            top10Rank = 4,
            isPremier2026 = true,
            is4K = true
        ),
        Movie(
            id = "m5",
            title = "Baron 2: Qaytish",
            originalTitle = "Baron 2: Safar",
            description = "Xalqaro giyohvandlik kartellari va maxsus xizmatlarning shafqatsiz to'qnashuvi. Agentlarning yashirin xavfli operatsiyasi.",
            backdropUrl = "https://images.unsplash.com/photo-1508739773434-c26b3d09e071?q=80&w=1200&auto=format&fit=crop",
            posterUrl = "https://images.unsplash.com/photo-1579783900882-c0d3dad7b119?q=80&w=600&auto=format&fit=crop",
            genres = listOf("Jangari", "Kriminal", "O'zbek kinolari"),
            year = 2024,
            rating = 8.6,
            ageRating = "16+",
            durationMinutes = 135,
            director = "Rustam Sa'diyev",
            cast = listOf("Ulug'bek Qodirov", "Farhod Mahmudov", "Yulduz Rajabova"),
            isTrending = false,
            isTop10 = true,
            top10Rank = 5,
            isUzbek = true,
            is4K = true
        ),
        Movie(
            id = "m6",
            title = "O'rgimchak Odam: Borliqlar Aro",
            originalTitle = "Spider-Man: Across the Spider-Verse",
            description = "Mayls Morales multikoinot bo'ylab sayohat qilib, butun koinot mavjudligini himoya qiluvchi O'rgimchaklar jamoasi bilan uchrashadi.",
            backdropUrl = "https://images.unsplash.com/photo-1635805737707-575885ab0820?q=80&w=1200&auto=format&fit=crop",
            posterUrl = "https://images.unsplash.com/photo-1607604276583-eef5d076aa5f?q=80&w=600&auto=format&fit=crop",
            genres = listOf("Multfilm", "Jangari", "Fantastika"),
            year = 2023,
            rating = 9.0,
            ageRating = "6+",
            durationMinutes = 140,
            director = "Xoakim Dos Santos",
            cast = listOf("Shamik Mur", "Xeyli Staynfeld", "Oskar Ayzek"),
            isTrending = true,
            isTop10 = true,
            top10Rank = 6,
            is4K = true
        ),
        Movie(
            id = "m7",
            title = "Interstellar (Yulduzlararo)",
            originalTitle = "Interstellar",
            description = "Insoniyat Yer yuzida halokat yoqasida turganda, bir guruh fazogirlar yangi yashash uchun qulay sayyora topish maqsadida fazo tuynugidan o'tadi.",
            backdropUrl = "https://images.unsplash.com/photo-1451187580459-43490279c0fa?q=80&w=1200&auto=format&fit=crop",
            posterUrl = "https://images.unsplash.com/photo-1446776811953-b23d57bd21aa?q=80&w=600&auto=format&fit=crop",
            genres = listOf("Fantastika", "Drama", "Sarguzasht"),
            year = 2014,
            rating = 8.7,
            ageRating = "12+",
            durationMinutes = 169,
            director = "Kristofer Nolan",
            cast = listOf("Metyu Makkonahi", "Enn Xeteuey", "Jessica Chesteyn"),
            isTrending = false,
            isTop10 = true,
            top10Rank = 7,
            is4K = true
        ),
        Movie(
            id = "m8",
            title = "Qora Ritsar (The Dark Knight)",
            originalTitle = "The Dark Knight",
            description = "Betmen va leytenant Jeyms Gordon Gotem shahrida uyushgan jinoyatchilikka qarshi kurashadi, ammo shahar kutilmaganda shafqatsiz Joker tomonidan xaosga tushadi.",
            backdropUrl = "https://images.unsplash.com/photo-1509281373149-e957c6296406?q=80&w=1200&auto=format&fit=crop",
            posterUrl = "https://images.unsplash.com/photo-1509281373149-e957c6296406?q=80&w=600&auto=format&fit=crop",
            genres = listOf("Jangari", "Kriminal", "Drama"),
            year = 2008,
            rating = 9.1,
            ageRating = "16+",
            durationMinutes = 152,
            director = "Kristofer Nolan",
            cast = listOf("Kristian Beyl", "Xit Lejer", "Aaron Exart"),
            isTrending = false,
            isTop10 = true,
            top10Rank = 8,
            is4K = true
        ),
        Movie(
            id = "m9",
            title = "Qish Quyoshi (Turk Seriali)",
            originalTitle = "Kis Gunesi",
            description = "Egizak aka-ukaning sirli taqdiri va oilaviy qasos haqida hayajonli turk dramasi.",
            backdropUrl = "https://images.unsplash.com/photo-1517604931442-7e0c8ed2963c?q=80&w=1200&auto=format&fit=crop",
            posterUrl = "https://images.unsplash.com/photo-1485846234645-a62644f84728?q=80&w=600&auto=format&fit=crop",
            genres = listOf("Drama", "Turk seriallari", "Melodrama"),
            year = 2024,
            rating = 8.4,
            ageRating = "16+",
            durationMinutes = 120,
            director = "Murat Onbul",
            cast = listOf("Shukru Ozyildiz", "Asli Enver", "Shenay Gurler"),
            isTrending = true,
            isTop10 = true,
            top10Rank = 9,
            is4K = false
        ),
        Movie(
            id = "m10",
            title = "Kung Fu Panda 4",
            originalTitle = "Kung Fu Panda 4",
            description = "Po Ma'naviy Yetakchi bo'lish arafasida yangi Ajdaho Jangchisini tayyorlashi va o'z qiyofasini o'zgartiruvchi Xameleon jodugariga qarshi kurashishi kerak.",
            backdropUrl = "https://images.unsplash.com/photo-1563089145-599997674d42?q=80&w=1200&auto=format&fit=crop",
            posterUrl = "https://images.unsplash.com/photo-1534447677768-be436bb09401?q=80&w=600&auto=format&fit=crop",
            genres = listOf("Multfilm", "Komediya", "Sarguzasht"),
            year = 2024,
            rating = 7.9,
            ageRating = "6+",
            durationMinutes = 94,
            director = "Mayk Mitchell",
            cast = listOf("Jek Blek", "Avkvafina", "Viola Devis"),
            isTrending = false,
            isTop10 = true,
            top10Rank = 10,
            is4K = true
        ),
        Movie(
            id = "m11",
            title = "Scorpion: Maxsus Operatsiya",
            originalTitle = "Scorpion",
            description = "O'zbekiston maxsus xizmatlari agenti xalqaro terrorizm xavfini bartaraf qilish uchun xorijiy davlatlarda shiddatli reyd o'tkazadi.",
            backdropUrl = "https://images.unsplash.com/photo-1509198397868-475647b2a1e5?q=80&w=1200&auto=format&fit=crop",
            posterUrl = "https://images.unsplash.com/photo-1579783900882-c0d3dad7b119?q=80&w=600&auto=format&fit=crop",
            genres = listOf("Jangari", "Triller", "O'zbek kinolari"),
            year = 2023,
            rating = 8.1,
            ageRating = "16+",
            durationMinutes = 110,
            director = "Muxlisa Azizova",
            cast = listOf("Farhod Mahmudov", "Karim Mirxodiyev", "Yulduz Rajabova"),
            isUzbek = true,
            is4K = true
        ),
        Movie(
            id = "m12",
            title = "Gladiator II (2025)",
            originalTitle = "Gladiator II",
            description = "Maksimusning o'limidan yillar o'tib, uning o'g'li Lyutsius Rim kolizeyida erkinlik va shon-sharaf uchun kurashishga majbur bo'ladi.",
            backdropUrl = "https://images.unsplash.com/photo-1518709268805-4e9042af9f23?q=80&w=1200&auto=format&fit=crop",
            posterUrl = "https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?q=80&w=600&auto=format&fit=crop",
            genres = listOf("Tarixiy", "Jangari", "Drama"),
            year = 2024,
            rating = 8.5,
            ageRating = "18+",
            durationMinutes = 148,
            director = "Ridli Skott",
            cast = listOf("Pol Meskal", "Pedro Paskal", "Denzel Vashington"),
            isTrending = true,
            is4K = true
        )
    )

    private val initialPromoCodes = listOf(
        PromoCode("ALFAPLAY", "VIP Premium 30 Kun Bepul", 30, "Alfa VIP 4K", 5000, 142, true),
        PromoCode("CINEMA2026", "2026 Premyeralar uchun 60 Kun", 60, "Alfa VIP Premium Oila", 2000, 89, true),
        PromoCode("TELECOMTV", "Telecom TV hamkorlik bonusi", 45, "Alfa PRO 4K", 1000, 310, true),
        PromoCode("PROVIP", "Eksklyuziv Pro Obuna (90 kun)", 90, "Alfa VIP 4K", 500, 77, true),
        PromoCode("BEPUL7", "1 haftalik sinov premium", 7, "Alfa Start", 10000, 1200, true)
    )

    private val defaultAdmin = User(
        id = "admin_1",
        name = "AlfaPlay Bosh Administrator",
        email = "admin@alfaplay.uz",
        phone = "+998901234567",
        password = "admin",
        avatarUrl = "",
        isPremium = true,
        subscriptionTier = "Alfa VIP Admin Ultimate",
        premiumExpiry = System.currentTimeMillis() + (1000L * 60 * 60 * 24 * 365 * 10), // 10 years
        isAdmin = true
    )

    private val sampleUser = User(
        id = "user_1",
        name = "Alisher Navoiy",
        email = "alisher@gmail.com",
        phone = "+998977654321",
        password = "123",
        avatarUrl = "",
        isPremium = true,
        subscriptionTier = "Alfa PRO 4K",
        premiumExpiry = System.currentTimeMillis() + (1000L * 60 * 60 * 24 * 30),
        isAdmin = false
    )

    // State flows
    private val _users = MutableStateFlow<List<User>>(listOf(defaultAdmin, sampleUser))
    val users: StateFlow<List<User>> = _users.asStateFlow()

    private val _currentUser = MutableStateFlow<User?>(defaultAdmin) // Start logged in as Admin for instant testability
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    private val _movies = MutableStateFlow<List<Movie>>(initialMovies)
    val movies: StateFlow<List<Movie>> = _movies.asStateFlow()

    private val _promoCodes = MutableStateFlow<List<PromoCode>>(initialPromoCodes)
    val promoCodes: StateFlow<List<PromoCode>> = _promoCodes.asStateFlow()

    private val _watchlistIds = MutableStateFlow<Set<String>>(setOf("m1", "m2", "m6"))
    val watchlistIds: StateFlow<Set<String>> = _watchlistIds.asStateFlow()

    private val _downloads = MutableStateFlow<List<DownloadItem>>(
        listOf(
            DownloadItem("d1", "m1", "Dune: 2-Qism", "https://images.unsplash.com/photo-1518709268805-4e9042af9f23?q=80&w=600&auto=format&fit=crop", 2400, 1.0f, "Yuklab olingan"),
            DownloadItem("d2", "m2", "Mendirman Jaloliddin", "https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?q=80&w=600&auto=format&fit=crop", 1800, 0.65f, "Yuklanmoqda (65%)")
        )
    )
    val downloads: StateFlow<List<DownloadItem>> = _downloads.asStateFlow()

    private val _reviews = MutableStateFlow<List<Review>>(
        listOf(
            Review("r1", "m1", "Bobur Mirzo", "", 5, "Kinoteatr darajasidagi dublyaj va 4K sifat! Allplay va Telecom TV dan ham ancha tez yuklanyapti.", "Kecha"),
            Review("r2", "m1", "Malika Karimova", "", 5, "Timoti Shalame roli vau! Ovoz sifati Dolby Atmosda juda ajoyib yangraydi.", "2 kun oldin"),
            Review("r3", "m2", "Jasur Rashidov", "", 5, "Milliy kinomizning eng zo'r namunasi. Jaloliddin Manguberdi jasorati qoyilmaqom tasvirlangan.", "3 kun oldin")
        )
    )
    val reviews: StateFlow<List<Review>> = _reviews.asStateFlow()

    // Auth methods
    fun register(name: String, emailOrPhone: String, password: String):Result<User> {
        val trimmed = emailOrPhone.trim()
        val isEmail = trimmed.contains("@")
        val email = if (isEmail) trimmed else "$trimmed@alfaplay.uz"
        val phone = if (!isEmail) trimmed else "+998900000000"

        if (_users.value.any { it.email.equals(email, ignoreCase = true) || it.phone == phone }) {
            return Result.failure(Exception("Bu email yoki telefon bilan allaqachon ro'yxatdan o'tilgan"))
        }

        val newUser = User(
            id = UUID.randomUUID().toString(),
            name = name.ifBlank { "Alfa Foydalanuvchi" },
            email = email,
            phone = phone,
            password = password,
            isPremium = false, // Must subscribe or enter promo code!
            subscriptionTier = "Alfa Bepul",
            premiumExpiry = 0L,
            isAdmin = false
        )

        _users.update { it + newUser }
        _currentUser.value = newUser
        return Result.success(newUser)
    }

    fun login(emailOrPhone: String, password: String): Result<User> {
        val trimmed = emailOrPhone.trim()
        val user = _users.value.find {
            (it.email.equals(trimmed, ignoreCase = true) || it.phone == trimmed) && it.password == password
        } ?: return Result.failure(Exception("Login yoki parol noto'g'ri kiritildi"))

        if (user.isBanned) {
            return Result.failure(Exception("Ushbu akkaunt administrator tomonidan bloklangan"))
        }

        _currentUser.value = user
        return Result.success(user)
    }

    fun logout() {
        _currentUser.value = null
    }

    fun switchUserToAdminQuick() {
        _currentUser.value = defaultAdmin
    }

    // Promo code redemption
    fun redeemPromoCode(code: String): Result<String> {
        val promo = _promoCodes.value.find { it.code.equals(code.trim(), ignoreCase = true) && it.isActive }
            ?: return Result.failure(Exception("Promo-kod topilmadi yoki muddati tugagan"))

        val current = _currentUser.value ?: return Result.failure(Exception("Iltimos, avval tizimga kiring"))

        val now = System.currentTimeMillis()
        val additionalTime = promo.durationDays.toLong() * 24 * 60 * 60 * 1000L
        val newExpiry = if (current.premiumExpiry > now) current.premiumExpiry + additionalTime else now + additionalTime

        val updatedUser = current.copy(
            isPremium = true,
            subscriptionTier = promo.tier,
            premiumExpiry = newExpiry
        )

        _currentUser.value = updatedUser
        _users.update { list -> list.map { if (it.id == updatedUser.id) updatedUser else it } }

        // Increment used count
        _promoCodes.update { list ->
            list.map {
                if (it.code == promo.code) it.copy(usedCount = it.usedCount + 1) else it
            }
        }

        return Result.success("Muvaffaqiyatli faollashtirildi! ${promo.durationDays} kun ${promo.tier} obunasi berildi.")
    }

    fun activatePlan(plan: SubscriptionPlan) {
        val current = _currentUser.value ?: return
        val now = System.currentTimeMillis()
        val additionalTime = plan.durationDays.toLong() * 24 * 60 * 60 * 1000L
        val newExpiry = if (current.premiumExpiry > now) current.premiumExpiry + additionalTime else now + additionalTime

        val updatedUser = current.copy(
            isPremium = true,
            subscriptionTier = plan.title,
            premiumExpiry = newExpiry
        )
        _currentUser.value = updatedUser
        _users.update { list -> list.map { if (it.id == updatedUser.id) updatedUser else it } }
    }

    // Watchlist
    fun toggleWatchlist(movieId: String) {
        _watchlistIds.update { set ->
            if (set.contains(movieId)) set - movieId else set + movieId
        }
    }

    fun isWatchlisted(movieId: String): Boolean = _watchlistIds.value.contains(movieId)

    // Downloads
    fun downloadMovie(movie: Movie) {
        val existing = _downloads.value.find { it.movieId == movie.id }
        if (existing == null) {
            val item = DownloadItem(
                id = UUID.randomUUID().toString(),
                movieId = movie.id,
                title = movie.title,
                posterUrl = movie.posterUrl,
                sizeMb = (1200..2800).random(),
                progress = 1.0f,
                status = "Yuklab olingan"
            )
            _downloads.update { it + item }
        }
    }

    fun removeDownload(downloadId: String) {
        _downloads.update { list -> list.filterNot { it.id == downloadId } }
    }

    // Reviews
    fun addReview(movieId: String, rating: Int, comment: String) {
        val user = _currentUser.value ?: return
        val sdf = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
        val review = Review(
            id = UUID.randomUUID().toString(),
            movieId = movieId,
            userName = user.name,
            userAvatar = user.avatarUrl,
            rating = rating,
            comment = comment,
            date = sdf.format(Date())
        )
        _reviews.update { listOf(review) + it }
    }

    // Admin Operations: Movie CRUD
    fun addMovie(movie: Movie) {
        _movies.update { listOf(movie) + it }
    }

    fun updateMovie(movie: Movie) {
        _movies.update { list -> list.map { if (it.id == movie.id) movie else it } }
    }

    fun deleteMovie(movieId: String) {
        _movies.update { list -> list.filterNot { it.id == movieId } }
    }

    // Admin Operations: Users & Subscriptions
    fun grantUserSubscription(userId: String, days: Int, tier: String) {
        val now = System.currentTimeMillis()
        val additional = days.toLong() * 24 * 60 * 60 * 1000L
        _users.update { list ->
            list.map { u ->
                if (u.id == userId) {
                    val exp = if (u.premiumExpiry > now) u.premiumExpiry + additional else now + additional
                    u.copy(isPremium = true, subscriptionTier = tier, premiumExpiry = exp)
                } else u
            }
        }
        if (_currentUser.value?.id == userId) {
            _currentUser.value = _users.value.find { it.id == userId }
        }
    }

    fun revokeUserSubscription(userId: String) {
        _users.update { list ->
            list.map { u ->
                if (u.id == userId) {
                    u.copy(isPremium = false, subscriptionTier = "Alfa Bepul", premiumExpiry = 0L)
                } else u
            }
        }
        if (_currentUser.value?.id == userId) {
            _currentUser.value = _users.value.find { it.id == userId }
        }
    }

    fun toggleUserBan(userId: String) {
        _users.update { list ->
            list.map { u ->
                if (u.id == userId) u.copy(isBanned = !u.isBanned) else u
            }
        }
    }

    // Admin Operations: Promo Codes CRUD
    fun createPromoCode(code: String, title: String, days: Int, tier: String, maxUses: Int) {
        val promo = PromoCode(
            code = code.uppercase().trim(),
            title = title,
            durationDays = days,
            tier = tier,
            maxUses = maxUses,
            usedCount = 0,
            isActive = true
        )
        _promoCodes.update { listOf(promo) + it }
    }

    fun togglePromoStatus(code: String) {
        _promoCodes.update { list ->
            list.map { if (it.code == code) it.copy(isActive = !it.isActive) else it }
        }
    }

    fun deletePromoCode(code: String) {
        _promoCodes.update { list -> list.filterNot { it.code == code } }
    }
}
