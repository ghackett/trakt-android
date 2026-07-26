package tv.trakt.trakt.core.auth.usecase

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import timber.log.Timber
import tv.trakt.trakt.common.auth.TokenProvider
import tv.trakt.trakt.common.helpers.extensions.nowUtc
import tv.trakt.trakt.core.auth.data.remote.AuthRemoteDataSource
import tv.trakt.trakt.core.auth.model.AuthDeviceCode
import tv.trakt.trakt.core.auth.model.AuthDeviceTokenCode.PENDING
import tv.trakt.trakt.core.auth.model.AuthDeviceTokenCode.TOO_MANY_REQUESTS
import tv.trakt.trakt.core.auth.model.AuthDeviceTokenState.Failure
import tv.trakt.trakt.core.auth.model.AuthDeviceTokenState.Success

/**
 * Runs the OAuth device authorization flow (RFC 8628): requests a device code, then polls the
 * token endpoint until the user approves the code on the Trakt website, denies it, or it expires.
 */
internal class AuthorizeDeviceUseCase(
    private val remoteSource: AuthRemoteDataSource,
    private val tokenProvider: TokenProvider,
) {
    sealed interface Progress {
        data class AwaitingActivation(
            val code: AuthDeviceCode,
        ) : Progress

        data object Authorized : Progress

        data object Denied : Progress
    }

    fun authorize(): Flow<Progress> =
        flow {
            val code = remoteSource.getDeviceCode()
            emit(Progress.AwaitingActivation(code))

            while (nowUtc().isBefore(code.expiresAt)) {
                delay(code.interval)

                when (val state = remoteSource.getDeviceToken(code.deviceCode)) {
                    is Success -> {
                        tokenProvider.saveToken(state.token)
                        Timber.d("Received and stored access token!")
                        emit(Progress.Authorized)
                        return@flow
                    }

                    is Failure -> when (state.code) {
                        PENDING, TOO_MANY_REQUESTS -> Unit // Keep polling.
                        else -> {
                            emit(Progress.Denied)
                            return@flow
                        }
                    }
                }
            }

            emit(Progress.Denied)
        }
}
