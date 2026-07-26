package tv.trakt.trakt.common.helpers.extensions

import timber.log.Timber

/**
 * Records the given [error] to the log.
 */
fun Timber.Forest.recordError(error: Exception) {
    Timber.e(error)
}
