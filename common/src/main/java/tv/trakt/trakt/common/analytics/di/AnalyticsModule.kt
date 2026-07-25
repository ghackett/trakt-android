package tv.trakt.trakt.common.analytics.di

import org.koin.dsl.module
import tv.trakt.trakt.common.analytics.Analytics
import tv.trakt.trakt.common.analytics.implementation.LogAnalytics
import tv.trakt.trakt.common.analytics.implementation.LogAnalyticsComments
import tv.trakt.trakt.common.analytics.implementation.LogAnalyticsPlayback
import tv.trakt.trakt.common.analytics.implementation.LogAnalyticsProgress
import tv.trakt.trakt.common.analytics.implementation.LogAnalyticsRatings
import tv.trakt.trakt.common.analytics.implementation.LogAnalyticsReactions
import tv.trakt.trakt.common.analytics.implementation.LogAnalyticsTrivia

val analyticsModule = module {
    single<Analytics> {
        LogAnalytics(
            reactions = LogAnalyticsReactions(),
            ratings = LogAnalyticsRatings(),
            comments = LogAnalyticsComments(),
            progress = LogAnalyticsProgress(),
            trivia = LogAnalyticsTrivia(),
            playback = LogAnalyticsPlayback(),
        )
    }
}
