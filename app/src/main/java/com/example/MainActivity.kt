package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.database.AppDatabase
import com.example.data.repository.JaapRepository
import com.example.ui.components.BottomNavBar
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.JaapScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.ProfileSetupScreen
import com.example.ui.screens.ProgressScreen
import com.example.ui.screens.WelcomeLoginScreen
import com.example.ui.theme.JaapTheme
import com.example.ui.viewmodel.JaapViewModel
import kotlinx.coroutines.flow.collectLatest

enum class AppScreen {
    LOADING,
    WELCOME,
    PROFILE_SETUP,
    MAIN
}

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        try {
            com.example.data.firebase.FirebaseConfig.initialize(this)
        } catch (e: Throwable) {
            e.printStackTrace()
        }

        val repository = (application as? JaapApplication)?.repository
            ?: JaapRepository(AppDatabase.getDatabase(applicationContext).jaapDao(), applicationContext)

        val viewModelFactory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return JaapViewModel(repository) as T
            }
        }

        setContent {
            val viewModel: JaapViewModel = viewModel(factory = viewModelFactory)
            val userSettings by viewModel.userSettings.collectAsState()
            val themeMode = userSettings?.themeMode ?: "DARK"

            var screenOverride by remember { mutableStateOf<AppScreen?>(null) }
            var selectedAuthMethod by remember { mutableStateOf("GUEST") }
            var selectedEmail by remember { mutableStateOf("") }

            // State-driven startup destination resolution
            val currentScreen: AppScreen = when {
                screenOverride != null -> screenOverride!!
                userSettings == null -> AppScreen.LOADING
                userSettings?.isOnboardingCompleted == true -> AppScreen.MAIN
                else -> {
                    if (viewModel.isUserLoggedIn()) {
                        AppScreen.PROFILE_SETUP
                    } else {
                        AppScreen.WELCOME
                    }
                }
            }

            JaapTheme(themeMode = themeMode) {
                when (currentScreen) {
                    AppScreen.LOADING -> {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color(0xFF0C0720))
                        )
                    }
                    AppScreen.WELCOME -> {
                        WelcomeLoginScreen(
                            viewModel = viewModel,
                            onAuthenticated = { method, email ->
                                selectedAuthMethod = method
                                selectedEmail = email
                                val isCompleted = viewModel.userSettings.value?.isOnboardingCompleted == true
                                if (isCompleted) {
                                    screenOverride = AppScreen.MAIN
                                } else {
                                    screenOverride = AppScreen.PROFILE_SETUP
                                }
                            },
                            onContinueAsGuest = {
                                selectedAuthMethod = "GUEST"
                                selectedEmail = ""
                                val isCompleted = viewModel.userSettings.value?.isOnboardingCompleted == true
                                if (isCompleted) {
                                    screenOverride = AppScreen.MAIN
                                } else {
                                    screenOverride = AppScreen.PROFILE_SETUP
                                }
                            }
                        )
                    }
                    AppScreen.PROFILE_SETUP -> {
                        ProfileSetupScreen(
                            viewModel = viewModel,
                            authMethod = selectedAuthMethod,
                            userEmail = selectedEmail,
                            onNavigateBack = {
                                screenOverride = AppScreen.WELCOME
                            },
                            onCompleteProfile = {
                                screenOverride = AppScreen.MAIN
                            }
                        )
                    }
                    AppScreen.MAIN -> {
                        MainAppContent(
                            viewModel = viewModel,
                            onOpenAccountSetup = {
                                screenOverride = AppScreen.WELCOME
                            },
                            onLoggedOut = {
                                screenOverride = AppScreen.WELCOME
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MainAppContent(
    viewModel: JaapViewModel,
    onOpenAccountSetup: () -> Unit = {},
    onLoggedOut: () -> Unit = {}
) {
    val currentTab by viewModel.currentTab.collectAsState()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            BottomNavBar(
                selectedTab = currentTab,
                onTabSelected = { viewModel.setTab(it) }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentTab) {
                0 -> HomeScreen(
                    viewModel = viewModel,
                    onNavigateToJaap = { viewModel.setTab(1) },
                    onNavigateToProgress = { viewModel.setTab(2) }
                )
                1 -> JaapScreen(viewModel = viewModel)
                2 -> ProgressScreen(viewModel = viewModel)
                3 -> ProfileScreen(
                    viewModel = viewModel,
                    onNavigateToProgress = { viewModel.setTab(2) },
                    onOpenAccountSetup = onOpenAccountSetup,
                    onLoggedOut = onLoggedOut
                )
            }
        }
    }
}
