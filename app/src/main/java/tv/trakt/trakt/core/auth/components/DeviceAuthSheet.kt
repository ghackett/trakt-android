@file:OptIn(ExperimentalMaterial3Api::class)

package tv.trakt.trakt.core.auth.components

import android.content.ClipData
import androidx.compose.foundation.layout.Arrangement.spacedBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow.Companion.Ellipsis
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import tv.trakt.trakt.core.auth.model.DeviceAuthState
import tv.trakt.trakt.resources.R
import tv.trakt.trakt.ui.components.TraktBottomSheet
import tv.trakt.trakt.ui.components.buttons.PrimaryButton
import tv.trakt.trakt.ui.theme.TraktTheme

@Composable
internal fun DeviceAuthSheet(
    state: DeviceAuthState?,
    onRetry: () -> Unit = {},
    onDismiss: () -> Unit = {},
) {
    if (state == null) return

    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true,
    )
    val sheetScope = rememberCoroutineScope()
    val dismiss = {
        sheetScope
            .launch { sheetState.hide() }
            .invokeOnCompletion {
                if (!sheetState.isVisible) {
                    onDismiss()
                }
            }
        Unit
    }

    TraktBottomSheet(
        sheetState = sheetState,
        onDismiss = onDismiss,
    ) {
        DeviceAuthView(
            state = state,
            onRetry = onRetry,
            onCancel = dismiss,
        )
    }
}

@Composable
private fun DeviceAuthView(
    state: DeviceAuthState,
    onRetry: () -> Unit = {},
    onCancel: () -> Unit = {},
) {
    Column(
        modifier = Modifier
            .padding(horizontal = 24.dp)
            .padding(bottom = 24.dp),
    ) {
        Text(
            text = stringResource(R.string.header_device_auth_title_mobile).uppercase(),
            style = TraktTheme.typography.heading6,
            color = TraktTheme.colors.textSecondary,
            maxLines = 1,
            overflow = Ellipsis,
            modifier = Modifier
                .padding(bottom = 30.dp),
        )

        when (state) {
            DeviceAuthState.Loading -> LoadingContent()
            is DeviceAuthState.AwaitingActivation -> AwaitingActivationContent(
                state = state,
                onCancel = onCancel,
            )
            DeviceAuthState.Failed -> FailedContent(
                onRetry = onRetry,
                onCancel = onCancel,
            )
        }
    }
}

@Composable
private fun LoadingContent() {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 34.dp),
    ) {
        CircularProgressIndicator(
            trackColor = Color.Transparent,
            color = TraktTheme.colors.textPrimary,
            strokeWidth = 2.dp,
            modifier = Modifier.size(32.dp),
        )
    }
}

@Composable
private fun AwaitingActivationContent(
    state: DeviceAuthState.AwaitingActivation,
    onCancel: () -> Unit,
) {
    val uriHandler = LocalUriHandler.current
    val clipboard = LocalClipboard.current
    val scope = rememberCoroutineScope()

    Text(
        text = stringResource(R.string.text_device_auth_instruction_mobile),
        style = TraktTheme.typography.paragraph,
        color = TraktTheme.colors.textPrimary,
    )

    Text(
        text = state.userCode,
        style = TraktTheme.typography.heading2,
        color = TraktTheme.colors.textPrimary,
        textAlign = TextAlign.Center,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 24.dp),
    )

    Row(
        horizontalArrangement = spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth(),
    ) {
        CircularProgressIndicator(
            trackColor = Color.Transparent,
            color = TraktTheme.colors.textSecondary,
            strokeWidth = 1.5.dp,
            modifier = Modifier.size(16.dp),
        )
        Text(
            text = stringResource(R.string.text_device_auth_waiting_mobile),
            style = TraktTheme.typography.paragraphSmall,
            color = TraktTheme.colors.textSecondary,
        )
    }

    Column(
        verticalArrangement = spacedBy(10.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 34.dp),
    ) {
        PrimaryButton(
            text = stringResource(R.string.button_text_copy_open_trakt_mobile),
            onClick = {
                scope.launch {
                    clipboard.setClipEntry(
                        ClipEntry(
                            ClipData.newPlainText("Trakt activation code", state.userCode),
                        ),
                    )
                    uriHandler.openUri(state.verificationUrl)
                }
            },
            modifier = Modifier.fillMaxWidth(),
        )

        PrimaryButton(
            text = stringResource(R.string.button_text_cancel),
            containerColor = TraktTheme.colors.primaryButtonContainerDisabled,
            onClick = onCancel,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
private fun FailedContent(
    onRetry: () -> Unit,
    onCancel: () -> Unit,
) {
    Text(
        text = stringResource(R.string.header_device_auth_try_again),
        style = TraktTheme.typography.paragraph,
        color = TraktTheme.colors.textPrimary,
    )

    Column(
        verticalArrangement = spacedBy(10.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 34.dp),
    ) {
        PrimaryButton(
            text = stringResource(R.string.button_text_retry),
            onClick = onRetry,
            modifier = Modifier.fillMaxWidth(),
        )

        PrimaryButton(
            text = stringResource(R.string.button_text_cancel),
            containerColor = TraktTheme.colors.primaryButtonContainerDisabled,
            onClick = onCancel,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF131517)
@Composable
private fun PreviewAwaitingActivation() {
    TraktTheme {
        DeviceAuthView(
            state = DeviceAuthState.AwaitingActivation(
                userCode = "1A2B3C4D",
                verificationUrl = "https://trakt.tv/activate",
            ),
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF131517)
@Composable
private fun PreviewFailed() {
    TraktTheme {
        DeviceAuthView(
            state = DeviceAuthState.Failed,
        )
    }
}
