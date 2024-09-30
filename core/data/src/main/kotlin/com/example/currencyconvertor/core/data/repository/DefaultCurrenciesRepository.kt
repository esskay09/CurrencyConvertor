package com.example.currencyconvertor.core.data.repository

import com.example.currencyconvertor.core.data.NetworkConstants
import com.example.currencyconvertor.core.data.Synchronizer
import com.example.currencyconvertor.core.data.changeListSync
import com.example.currencyconvertor.core.data.model.mapNetworkCurrenciesToEntity
import com.example.currencyconvertor.core.database.dao.CurrencyDao
import com.example.currencyconvertor.core.database.model.CurrencyEntity
import com.example.currencyconvertor.core.database.model.asExternalModel
import com.example.currencyconvertor.core.datastore.CurrencyPreferencesDataSource
import com.example.currencyconvertor.core.model.Currency
import com.example.currencyconvertor.core.network.CurrencyNetworkDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class DefaultCurrenciesRepository @Inject constructor(
    private val currencyDao: CurrencyDao,
    private val network: CurrencyNetworkDataSource,
    private val preferencesDataSource: CurrencyPreferencesDataSource
) : CurrenciesRepository {

    override val currencies: Flow<List<Currency>> =
        currencyDao.getAllCurrencies().map { it.map(CurrencyEntity::asExternalModel) }

    override val selectedBaseCurrency: Flow<String> = preferencesDataSource.selectedBaseCurrencyId

    override val selectedCurrency: Flow<Currency?> = combine(
        preferencesDataSource.selectedCurrencyId,
        currencies
    ) { selectedCurrencyId, currencies ->
        currencies.find { it.id == selectedCurrencyId }
    }

    override suspend fun setSelectedCurrency(currencyId: String) {
        preferencesDataSource.updateSelectedCurrencyId(currencyId)
    }

    override suspend fun syncWith(synchronizer: Synchronizer): Boolean =
        synchronizer.changeListSync(
            timeStampReader = { it.currencies },
            shouldUpdate = { lastTimeStamp, currentTimeStamp ->
                val minutesSince = currentTimeStamp.minus(lastTimeStamp).inWholeMinutes
                minutesSince > NetworkConstants.DEFAULT_MINUTES_THRESHOLD
            },
            timeStampUpdater = { timeStamp -> copy(currencies = timeStamp) },
            updater = {
                val currencies = network.getCurrencies()
                currencyDao.updateCurrencies(currencies = mapNetworkCurrenciesToEntity(currencies))
            }
        )
}