package tv.trakt.trakt.common.config

/**
 * Static app configuration. These values were previously served by Firebase
 * Remote Config; this fork ships them as compile-time constants instead.
 */
object AppConfig {
    // Mobile

    const val MOBILE_BACKGROUND_IMAGE_URL =
        "https://image.tmdb.org/t/p/w1280/xdP0op99C5oDvkAOfoYbtPKP91v.jpg"

    const val MOBILE_BACKGROUND_VIP_IMAGE_URL =
        "https://image.tmdb.org/t/p/w1280/cNsdbWfcyv7vkatH2NVktYBOmR2.jpg"

    // No default artwork; set a URL to show a background on monthly stats.
    const val MOBILE_THIS_MONTH_IMAGE_URL = ""

    const val MOBILE_EMPTY_IMAGE_1 =
        "https://media.trakt.tv/images/shows/000/150/469/fanarts/medium/8a02e4c084.jpg.webp"

    const val MOBILE_EMPTY_IMAGE_2 =
        "https://media.trakt.tv/images/movies/000/729/817/fanarts/medium/2934c87271.jpg.webp"

    const val MOBILE_EMPTY_IMAGE_3 =
        "https://media.trakt.tv/images/shows/000/001/390/posters/thumb/93df9cd612.jpg.webp"

    const val MOBILE_EMPTY_IMAGE_4 =
        "https://media.trakt.tv/images/shows/000/162/206/fanarts/medium/2d467422a7.jpg.webp"

    const val MOBILE_EMPTY_IMAGE_5 =
        "https://media.trakt.tv/images/shows/000/154/997/fanarts/medium/9400ecb8e2.jpg.webp"

    const val MOBILE_WELCOME_BANNER_ENABLED = false

    // TV

    const val BACKGROUND_IMAGE_URL =
        "https://image.tmdb.org/t/p/w1280/xdP0op99C5oDvkAOfoYbtPKP91v.jpg"

    const val PLEX_PLAY_ENABLED = false

    // Shared

    val IN_APP_REVIEW_PROMPT_COUNTS = listOf(3L, 10L, 30L)
}
