package tv.trakt.trakt.core.auth.di

import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import tv.trakt.trakt.core.auth.data.remote.AuthApiClient
import tv.trakt.trakt.core.auth.data.remote.AuthRemoteDataSource
import tv.trakt.trakt.core.auth.usecase.AuthorizeDeviceUseCase

val authModule = module {
    singleOf(::AuthApiClient) { bind<AuthRemoteDataSource>() }

    factoryOf(::AuthorizeDeviceUseCase)
}
