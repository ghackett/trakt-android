package tv.trakt.trakt

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.os.Bundle
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import com.jakewharton.processphoenix.ProcessPhoenix
import kotlinx.coroutines.runBlocking
import org.koin.android.ext.android.inject
import org.koin.androidx.compose.koinViewModel
import tv.trakt.trakt.app.TvSplashActivity
import tv.trakt.trakt.common.helpers.extensions.isTelevision
import tv.trakt.trakt.common.ui.theme.colors.DarkColors
import tv.trakt.trakt.core.main.MainScreen
import tv.trakt.trakt.core.main.MainViewModel
import tv.trakt.trakt.core.main.usecases.CustomThemeUseCase
import tv.trakt.trakt.core.main.usecases.CustomThemeUseCase.CustomThemeConfig
import tv.trakt.trakt.ui.theme.TraktTheme
import tv.trakt.trakt.ui.theme.model.toTraktDarkColors

internal val LocalBottomBarVisibility = compositionLocalOf { mutableStateOf(true) }
internal val LocalCheckInVisibility = compositionLocalOf { mutableStateOf(true) }
internal val LocalRatePromptVisibility = compositionLocalOf { mutableStateOf(true) }
internal val LocalSnackbarState = compositionLocalOf { SnackbarHostState() }
internal val LocalStartAuthorization = staticCompositionLocalOf { {} }

internal class MainActivity : AppCompatActivity() {
    private val newIntent = mutableStateOf<Intent?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Redirect to TV Activity if on a television device.
        if (isTelevision()) {
            startActivity(
                Intent(this, TvSplashActivity::class.java),
            )
            finish()
            return
        }

        setupOrientation()
        enableEdgeToEdge(
            navigationBarStyle = SystemBarStyle.dark(
                scrim = Color.TRANSPARENT,
            ),
            statusBarStyle = SystemBarStyle.dark(
                scrim = Color.TRANSPARENT,
            ),
        )

        setContent {
            val bottomBarVisibility = remember { mutableStateOf(true) }
            val checkInVisibility = remember { mutableStateOf(true) }
            val ratePromptVisibility = remember { mutableStateOf(true) }
            val snackbarState = remember { SnackbarHostState() }
            val customThemeState = remember {
                getCustomThemeConfig().also {
                    customThemeConfig = it
                }
            }

            val mainViewModel: MainViewModel = koinViewModel()
            val startAuthorization = remember(mainViewModel) {
                { mainViewModel.startAuthorization() }
            }

            TraktTheme(
                colors = when {
                    customThemeState.enabled -> {
                        val customColors = customThemeConfig?.theme?.colors?.toTraktDarkColors()
                        customColors ?: DarkColors
                    }

                    else -> {
                        DarkColors
                    }
                },
            ) {
                CompositionLocalProvider(
                    LocalBottomBarVisibility provides bottomBarVisibility,
                    LocalCheckInVisibility provides checkInVisibility,
                    LocalRatePromptVisibility provides ratePromptVisibility,
                    LocalSnackbarState provides snackbarState,
                    LocalStartAuthorization provides startAuthorization,
                ) {
                    MainScreen(
                        viewModel = mainViewModel,
                        intent = intent,
                        newIntent = newIntent,
                    )
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        newIntent.value = intent
    }

    @SuppressLint("SourceLockedOrientationActivity")
    private fun setupOrientation() {
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    }

    // Custom Theme
    internal var customThemeConfig: CustomThemeConfig? = null
    private val customThemeUseCase: CustomThemeUseCase by lazy {
        inject<CustomThemeUseCase>().value
    }

    private fun getCustomThemeConfig(): CustomThemeConfig {
        return runBlocking {
            customThemeUseCase.getConfig()
        }
    }

    internal fun toggleCustomTheme(enabled: Boolean) {
        runBlocking {
            customThemeUseCase.toggleUserEnabled(enabled)
            ProcessPhoenix.triggerRebirth(this@MainActivity)
        }
    }

    internal fun toggleCustomThemeOverlay() {
        val id = customThemeConfig?.theme?.id ?: return
        runBlocking {
            customThemeUseCase.setUserDismissedOverlay(id)
        }
    }
}
