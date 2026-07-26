package tv.trakt.trakt.core.auth.model

import androidx.compose.runtime.Immutable

/**
 * UI state of the in-progress device authorization flow. Absent (null) when no sign-in is active.
 */
@Immutable
internal sealed interface DeviceAuthState {
    data object Loading : DeviceAuthState

    data class AwaitingActivation(
        val userCode: String,
        val verificationUrl: String,
    ) : DeviceAuthState

    data object Failed : DeviceAuthState
}
