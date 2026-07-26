package tv.trakt.trakt.core.auth.data.remote

import tv.trakt.trakt.core.auth.model.AuthDeviceCode
import tv.trakt.trakt.core.auth.model.AuthDeviceTokenState

internal interface AuthRemoteDataSource {
    suspend fun getDeviceCode(): AuthDeviceCode

    suspend fun getDeviceToken(deviceCode: String): AuthDeviceTokenState
}
