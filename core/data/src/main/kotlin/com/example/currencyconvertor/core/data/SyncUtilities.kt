package com.example.currencyconvertor.core.data

import android.util.Log
import com.example.currencyconvertor.core.datastore.Preferences
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlin.coroutines.cancellation.CancellationException


object NetworkConstants {

    //For syncing data
    const val DEFAULT_MINUTES_THRESHOLD = 30
}


interface Synchronizer {
    suspend fun getTimeStamps(): Preferences.TimeStamps

    suspend fun updateTimeStamps(update: Preferences.TimeStamps.() -> Preferences.TimeStamps)
    suspend fun Syncable.sync() =
        this@sync.syncWith(this@Synchronizer)
}

interface Syncable {
    suspend fun syncWith(synchronizer: Synchronizer): Boolean
}

private suspend fun <T> suspendRunCatching(block: suspend () -> T): Result<T> = try {
    Result.success(block())
} catch (cancellationException: CancellationException) {
    throw cancellationException
} catch (exception: Exception) {
    Log.i(
        "suspendRunCatching",
        "Failed to evaluate a suspendRunCatchingBlock. Returning failure Result",
        exception,
    )
    Result.failure(exception)
}

suspend fun Synchronizer.changeListSync(
    timeStampReader: (Preferences.TimeStamps) -> Long,
    shouldUpdate: (last: Instant, current: Instant) -> Boolean,
    timeStampUpdater: Preferences.TimeStamps.(Long) -> Preferences.TimeStamps,
    updater: suspend () -> Unit
) = suspendRunCatching {
    val lastTimeStamp = timeStampReader(getTimeStamps()).let {
        Instant.fromEpochMilliseconds(it)
    }
    val currentTime = Clock.System.now()
    if (!shouldUpdate(lastTimeStamp, currentTime)) return@suspendRunCatching true
    updater()
    updateTimeStamps { timeStampUpdater(Clock.System.now().toEpochMilliseconds()) }
}.isSuccess
