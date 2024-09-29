package com.example.currencyconvertor.core.data.repository

import com.example.currencyconvertor.core.data.Synchronizer
import com.example.currencyconvertor.core.datastore.CurrencyPreferencesDataSource
import com.example.currencyconvertor.core.datastore.Preferences

class TestSynchronizer(
    private val preferencesDataSource: CurrencyPreferencesDataSource,
) : Synchronizer {

    override suspend fun getTimeStamps(): Preferences.TimeStamps =
        preferencesDataSource.getNetworkFetchTimeStamps()

    override suspend fun updateTimeStamps(update: Preferences.TimeStamps.() -> Preferences.TimeStamps) {
        preferencesDataSource.updateNetworkFetchTimeStamps(update)
    }
}
