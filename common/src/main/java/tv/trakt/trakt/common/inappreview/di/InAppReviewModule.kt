package tv.trakt.trakt.common.inappreview.di

import org.koin.core.qualifier.named
import org.koin.dsl.module
import tv.trakt.trakt.common.inappreview.RequestAppReviewUseCase

private const val MAIN_PREFERENCES = "main_preferences"

val inAppReviewModule = module {
    factory {
        RequestAppReviewUseCase(
            mainDataStore = get(named(MAIN_PREFERENCES)),
        )
    }
}
