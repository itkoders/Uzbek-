package com.example

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.Movie
import com.example.ui.AppScreen
import com.example.ui.CinemaViewModel
import com.example.ui.components.PremiumPaywallDialog
import com.example.ui.screens.AdminScreen
import com.example.ui.screens.AuthScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.MovieDetailScreen
import com.example.ui.screens.PlayerScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.SearchScreen
import com.example.ui.screens.SubscriptionScreen
import com.example.ui.screens.WatchlistScreen
import com.example.ui.theme.CinemaBackground
import com.example.ui.theme.CinemaGold
import com.example.ui.theme.CinemaRed
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.TextSecondary
import kotlinx.coroutines.flow.collectLatest

class MainActivity : ComponentActivity() {

    private val viewModel: CinemaViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                CinemaApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun CinemaApp(viewModel: CinemaViewModel) {
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val allMovies by viewModel.allMovies.collectAsStateWithLifecycle()
    val filteredMovies by viewModel.filteredMovies.collectAsStateWithLifecycle()
    val allUsers by viewModel.allUsers.collectAsStateWithLifecycle()
    val promoCodes by viewModel.promoCodes.collectAsStateWithLifecycle()
    val watchlistIds by viewModel.watchlistIds.collectAsStateWithLifecycle()
    val downloads by viewModel.downloads.collectAsStateWithLifecycle()
    val reviews by viewModel.reviews.collectAsStateWithLifecycle()

    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val selectedGenre by viewModel.selectedGenre.collectAsStateWithLifecycle()
    val isKidsMode by viewModel.isKidsMode.collectAsStateWithLifecycle()
    val showPaywall by viewModel.showPaywall.collectAsStateWithLifecycle()

    // Handle toast messages
    LaunchedEffect(Unit) {
        viewModel.toastMessage.collectLatest { msg ->
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
        }
    }

    // List of backdrop posters for dynamic rotating background
    val backdropPosters = remember(allMovies) {
        allMovies.map { it.backdropUrl }.filter { it.isNotBlank() }
    }

    val isBottomBarVisible = currentUser != null &&
            currentScreen !is AppScreen.Player &&
            currentScreen !is AppScreen.MovieDetail &&
            currentScreen !is AppScreen.Auth &&
            currentScreen !is AppScreen.Admin

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = CinemaBackground,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            AnimatedVisibility(
                visible = isBottomBarVisible,
                enter = slideInVertically(initialOffsetY = { it }),
                exit = slideOutVertically(targetOffsetY = { it })
            ) {
                NavigationBar(
                    containerColor = Color(0xFA0B0E17),
                    tonalElevation = 8.dp,
                    modifier = Modifier
                        .windowInsetsPadding(WindowInsets.navigationBars)
                        .testTag("main_navigation_bar")
                ) {
                    NavigationBarItem(
                        selected = currentScreen is AppScreen.Home,
                        onClick = { viewModel.navigateTo(AppScreen.Home) },
                        icon = { Icon(Icons.Default.Home, contentDescription = "Asosiy") },
                        label = { Text("Asosiy", fontSize = 11.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.White,
                            selectedTextColor = CinemaRed,
                            indicatorColor = CinemaRed,
                            unselectedIconColor = TextSecondary,
                            unselectedTextColor = TextSecondary
                        ),
                        modifier = Modifier.testTag("nav_home")
                    )

                    NavigationBarItem(
                        selected = currentScreen is AppScreen.Search,
                        onClick = { viewModel.navigateTo(AppScreen.Search) },
                        icon = { Icon(Icons.Default.Search, contentDescription = "Qidiruv") },
                        label = { Text("Qidiruv", fontSize = 11.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.White,
                            selectedTextColor = CinemaRed,
                            indicatorColor = CinemaRed,
                            unselectedIconColor = TextSecondary,
                            unselectedTextColor = TextSecondary
                        ),
                        modifier = Modifier.testTag("nav_search")
                    )

                    NavigationBarItem(
                        selected = currentScreen is AppScreen.Watchlist,
                        onClick = { viewModel.navigateTo(AppScreen.Watchlist) },
                        icon = { Icon(Icons.Default.Bookmark, contentDescription = "Ro'yxatim") },
                        label = { Text("Ro'yxatim", fontSize = 11.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.White,
                            selectedTextColor = CinemaRed,
                            indicatorColor = CinemaRed,
                            unselectedIconColor = TextSecondary,
                            unselectedTextColor = TextSecondary
                        ),
                        modifier = Modifier.testTag("nav_watchlist")
                    )

                    NavigationBarItem(
                        selected = currentScreen is AppScreen.Subscription,
                        onClick = { viewModel.navigateTo(AppScreen.Subscription) },
                        icon = { Icon(Icons.Default.WorkspacePremium, contentDescription = "VIP") },
                        label = { Text("Obuna", fontSize = 11.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.Black,
                            selectedTextColor = CinemaGold,
                            indicatorColor = CinemaGold,
                            unselectedIconColor = TextSecondary,
                            unselectedTextColor = TextSecondary
                        ),
                        modifier = Modifier.testTag("nav_subscription")
                    )

                    NavigationBarItem(
                        selected = currentScreen is AppScreen.Profile,
                        onClick = { viewModel.navigateTo(AppScreen.Profile) },
                        icon = { Icon(Icons.Default.Person, contentDescription = "Profil") },
                        label = { Text("Profil", fontSize = 11.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.White,
                            selectedTextColor = CinemaRed,
                            indicatorColor = CinemaRed,
                            unselectedIconColor = TextSecondary,
                            unselectedTextColor = TextSecondary
                        ),
                        modifier = Modifier.testTag("nav_profile")
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = if (isBottomBarVisible) innerPadding.calculateBottomPadding() else 0.dp)
        ) {
            when (val screen = currentScreen) {
                is AppScreen.Auth -> {
                    AuthScreen(
                        backdropPosters = backdropPosters,
                        onLogin = { email, pass -> viewModel.login(email, pass) },
                        onRegister = { name, email, pass -> viewModel.register(name, email, pass) },
                        onAdminDemoLogin = { viewModel.quickAdminLogin() }
                    )
                }

                is AppScreen.Home -> {
                    HomeScreen(
                        movies = filteredMovies,
                        currentUser = currentUser,
                        watchlistIds = watchlistIds,
                        selectedGenre = selectedGenre,
                        isKidsMode = isKidsMode,
                        onSelectGenre = { viewModel.selectGenre(it) },
                        onToggleKidsMode = { viewModel.toggleKidsMode() },
                        onMovieClick = { viewModel.navigateTo(AppScreen.MovieDetail(it)) },
                        onWatchClick = { viewModel.playMovie(it) },
                        onToggleWatchlist = { viewModel.toggleWatchlist(it) },
                        onNavigateSearch = { viewModel.navigateTo(AppScreen.Search) },
                        onNavigateAdmin = { viewModel.navigateTo(AppScreen.Admin) },
                        onNavigateProfile = { viewModel.navigateTo(AppScreen.Profile) },
                        onNavigateSubscription = { viewModel.navigateTo(AppScreen.Subscription) }
                    )
                }

                is AppScreen.MovieDetail -> {
                    BackHandler { viewModel.navigateTo(AppScreen.Home) }
                    MovieDetailScreen(
                        movie = screen.movie,
                        similarMovies = allMovies.filter { sim ->
                            sim.genres.any { it in screen.movie.genres }
                        },
                        reviews = reviews.filter { it.movieId == screen.movie.id },
                        isWatchlisted = watchlistIds.contains(screen.movie.id),
                        onBack = { viewModel.navigateTo(AppScreen.Home) },
                        onPlayClick = { viewModel.playMovie(screen.movie) },
                        onDownloadClick = { viewModel.downloadMovie(screen.movie) },
                        onToggleWatchlist = { viewModel.toggleWatchlist(screen.movie) },
                        onSimilarMovieClick = { viewModel.navigateTo(AppScreen.MovieDetail(it)) },
                        onAddReview = { rating, comment -> viewModel.addReview(screen.movie.id, rating, comment) }
                    )
                }

                is AppScreen.Player -> {
                    BackHandler { viewModel.navigateTo(AppScreen.MovieDetail(screen.movie)) }
                    PlayerScreen(
                        movie = screen.movie,
                        onClose = { viewModel.navigateTo(AppScreen.MovieDetail(screen.movie)) }
                    )
                }

                is AppScreen.Search -> {
                    BackHandler { viewModel.navigateTo(AppScreen.Home) }
                    SearchScreen(
                        searchQuery = searchQuery,
                        selectedGenre = selectedGenre,
                        filteredMovies = filteredMovies,
                        onQueryChanged = { viewModel.onSearchQueryChanged(it) },
                        onSelectGenre = { viewModel.selectGenre(it) },
                        onMovieClick = { viewModel.navigateTo(AppScreen.MovieDetail(it)) },
                        onBack = { viewModel.navigateTo(AppScreen.Home) }
                    )
                }

                is AppScreen.Subscription -> {
                    BackHandler { viewModel.navigateTo(AppScreen.Home) }
                    SubscriptionScreen(
                        currentUser = currentUser,
                        onBack = { viewModel.navigateTo(AppScreen.Home) },
                        onActivatePlan = { viewModel.activatePlan(it) },
                        onRedeemPromo = { viewModel.redeemPromo(it) }
                    )
                }

                is AppScreen.Watchlist -> {
                    BackHandler { viewModel.navigateTo(AppScreen.Home) }
                    WatchlistScreen(
                        watchlistMovies = allMovies.filter { watchlistIds.contains(it.id) },
                        downloads = downloads,
                        onMovieClick = { viewModel.navigateTo(AppScreen.MovieDetail(it)) },
                        onWatchClick = { viewModel.playMovie(it) },
                        onRemoveDownload = { viewModel.repository.removeDownload(it) },
                        onBack = { viewModel.navigateTo(AppScreen.Home) }
                    )
                }

                is AppScreen.Admin -> {
                    BackHandler { viewModel.navigateTo(AppScreen.Home) }
                    AdminScreen(
                        movies = allMovies,
                        users = allUsers,
                        promoCodes = promoCodes,
                        onAddMovie = { viewModel.adminAddMovie(it) },
                        onDeleteMovie = { viewModel.adminDeleteMovie(it) },
                        onGrantSub = { uid, days, tier -> viewModel.adminGrantSub(uid, days, tier) },
                        onRevokeSub = { uid -> viewModel.adminRevokeSub(uid) },
                        onToggleBan = { uid -> viewModel.adminToggleBan(uid) },
                        onCreatePromo = { code, title, days, tier, maxUses ->
                            viewModel.adminCreatePromo(code, title, days, tier, maxUses)
                        },
                        onDeletePromo = { code -> viewModel.adminDeletePromo(code) },
                        onBack = { viewModel.navigateTo(AppScreen.Home) }
                    )
                }

                is AppScreen.Profile -> {
                    BackHandler { viewModel.navigateTo(AppScreen.Home) }
                    ProfileScreen(
                        currentUser = currentUser,
                        isKidsMode = isKidsMode,
                        onToggleKidsMode = { viewModel.toggleKidsMode() },
                        onNavigateSubscription = { viewModel.navigateTo(AppScreen.Subscription) },
                        onNavigateAdmin = { viewModel.navigateTo(AppScreen.Admin) },
                        onLogout = { viewModel.logout() },
                        onBack = { viewModel.navigateTo(AppScreen.Home) }
                    )
                }
            }

            // Paywall Dialog
            if (showPaywall) {
                PremiumPaywallDialog(
                    onDismiss = { viewModel.closePaywall() },
                    onNavigateToPlans = {
                        viewModel.closePaywall()
                        viewModel.navigateTo(AppScreen.Subscription)
                    },
                    onRedeemPromo = { code ->
                        viewModel.redeemPromo(code)
                    }
                )
            }
        }
    }
}
