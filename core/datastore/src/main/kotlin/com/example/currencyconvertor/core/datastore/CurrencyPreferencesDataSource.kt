package com.example.currencyconvertor.core.datastore

import android.util.Log
import androidx.datastore.core.DataStore
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject


class CurrencyPreferencesDataSource @Inject constructor(
    private val preferences: DataStore<PreferencesProtoModel>,
) {

    suspend fun getNetworkFetchTimeStamps() = preferences.data
        .map {
            Preferences(
                timeStamps = Preferences.TimeStamps(
                    currencies = it.networkFetchTimeStamps.currenciesFetchTimeStamp,
                    conversionRates = it.networkFetchTimeStamps.currencyRatesFetchTimeStamp,
                )
            )
        }
        .firstOrNull() ?: Preferences()


    suspend fun updateNetworkFetchTimeStamps(update: (Preferences.TimeStamps) -> Preferences.TimeStamps) {
        try {
            preferences.updateData { currentPreferences ->
                val currentTimeStamps = currentPreferences.networkFetchTimeStamps
                val updateTimeStamps = update(
                    Preferences.TimeStamps(
                        currencies = currentTimeStamps.currenciesFetchTimeStamp,
                        conversionRates = currentTimeStamps.currencyRatesFetchTimeStamp,
                    ),
                )
                currentPreferences.copy {
                    networkFetchTimeStamps = networkFetchTimeStampsProtoModel {
                        currenciesFetchTimeStamp = updateTimeStamps.currencies
                        currencyRatesFetchTimeStamp = updateTimeStamps.conversionRates
                    }
                }
            }
        } catch (ioException: IOException) {
            Log.e("CurrencyPreferences", "Failed to update user preferences", ioException)
        }
    }
}

