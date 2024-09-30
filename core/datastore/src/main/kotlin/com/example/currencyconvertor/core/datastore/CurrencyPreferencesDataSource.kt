package com.example.currencyconvertor.core.datastore

import android.util.Log
import androidx.datastore.core.DataStore
import com.example.currencyconvertor.core.Constants
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject


class CurrencyPreferencesDataSource @Inject constructor(
    private val preferences: DataStore<PreferencesProtoModel>,
) {

    suspend fun getNetworkFetchTimeStamps(): Preferences.TimeStamps = preferences.data
        .map {
            Preferences.TimeStamps(
                currencies = it.networkFetchTimeStamps.currenciesFetchTimeStamp,
                exchangeRates = it.networkFetchTimeStamps.exchangeRatesFetchTimeStamp,
            )
        }
        .firstOrNull() ?: Preferences.TimeStamps()


    suspend fun updateNetworkFetchTimeStamps(update: (Preferences.TimeStamps) -> Preferences.TimeStamps) {
        try {
            preferences.updateData { currentPreferences ->
                val currentTimeStamps = currentPreferences.networkFetchTimeStamps
                val updateTimeStamps = update(
                    Preferences.TimeStamps(
                        currencies = currentTimeStamps.currenciesFetchTimeStamp,
                        exchangeRates = currentTimeStamps.exchangeRatesFetchTimeStamp,
                    ),
                )
                currentPreferences.copy {
                    networkFetchTimeStamps = networkFetchTimeStampsProtoModel {
                        currenciesFetchTimeStamp = updateTimeStamps.currencies
                        exchangeRatesFetchTimeStamp = updateTimeStamps.exchangeRates
                    }
                }
            }
        } catch (ioException: IOException) {
            Log.e("CurrencyPreferences", "Failed to update user preferences", ioException)
            System.err.println("Failed to update user preferences: ${ioException.message}")
        }
    }

    val selectedBaseCurrencyId: Flow<String> = preferences.data
        .map { it.selectedBaseCurrencyId.ifEmpty { Constants.DEFAULT_BASE_CURRENCY } }


    val selectedCurrencyId: Flow<String> = preferences.data
        .map { it.selectedCurrencyId }

    suspend fun updateSelectedBaseCurrencyId(currencyId: String) {
        try {
            preferences.updateData { currentPreferences ->
                currentPreferences.copy {
                    selectedBaseCurrencyId = currencyId
                }
            }
        } catch (ioException: IOException) {
            Log.e("CurrencyPreferences", "Failed to update user preferences", ioException)
            System.err.println("Failed to update user preferences: ${ioException.message}")
        }
    }

    suspend fun updateSelectedCurrencyId(currencyId: String) {
        try {
            preferences.updateData { currentPreferences ->
                currentPreferences.copy {
                    selectedCurrencyId = currencyId
                }
            }
        } catch (ioException: IOException) {
            Log.e("CurrencyPreferences", "Failed to update user preferences", ioException)
            System.err.println("Failed to update user preferences: ${ioException.message}")
        }
    }
}


