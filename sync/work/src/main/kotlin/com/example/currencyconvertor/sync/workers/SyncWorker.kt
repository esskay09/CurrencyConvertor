package com.example.currencyconvertor.sync.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import androidx.work.WorkerParameters
import com.example.currencyconvertor.core.data.Synchronizer
import com.example.currencyconvertor.core.data.repository.CurrenciesRepository
import com.example.currencyconvertor.core.data.repository.ExchangeRatesRepository
import com.example.currencyconvertor.core.datastore.CurrencyPreferencesDataSource
import com.example.currencyconvertor.core.datastore.Preferences
import com.example.currencyconvertor.core.common.network.ConvertorDispatchers.IO
import com.example.currencyconvertor.core.common.network.Dispatcher
import com.example.currencyconvertor.sync.initializers.SyncConstraints
import com.example.currencyconvertor.sync.initializers.syncForegroundInfo
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext

/**
 * Syncs the data layer by delegating to the appropriate repository instances with
 * sync functionality.
 */
@HiltWorker
internal class SyncWorker @AssistedInject constructor(
    @Assisted private val appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val preferences: CurrencyPreferencesDataSource,
    private val currencyRepository: CurrenciesRepository,
    private val ratesRepository: ExchangeRatesRepository,
    @Dispatcher(IO) private val ioDispatcher: CoroutineDispatcher
) : CoroutineWorker(appContext, workerParams), Synchronizer {

    override suspend fun getForegroundInfo(): ForegroundInfo =
        appContext.syncForegroundInfo()

    override suspend fun doWork(): Result = withContext(ioDispatcher) {
        val syncedSuccessfully = awaitAll(
            async { currencyRepository.sync() },
            async { ratesRepository.sync() },
        ).all { it }
        if (syncedSuccessfully) {
            Result.success()
        } else {
            Result.retry()
        }
    }

    override suspend fun getTimeStamps(): Preferences.TimeStamps {
        return preferences.getNetworkFetchTimeStamps()
    }

    override suspend fun updateTimeStamps(update: Preferences.TimeStamps.() -> Preferences.TimeStamps) {
        preferences.updateNetworkFetchTimeStamps(update)
    }

    companion object {
        /**
         * Expedited one time work to sync data on app startup
         */
        fun startUpSyncWork() = OneTimeWorkRequestBuilder<DelegatingWorker>()
            .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
            .setConstraints(SyncConstraints)
            .setInputData(SyncWorker::class.delegatedData())
            .build()
    }
}
