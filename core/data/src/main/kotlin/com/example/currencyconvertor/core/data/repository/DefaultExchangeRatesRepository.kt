package com.example.currencyconvertor.core.data.repository

import com.example.currencyconvertor.core.data.NetworkConstants
import com.example.currencyconvertor.core.data.Synchronizer
import com.example.currencyconvertor.core.data.changeListSync
import com.example.currencyconvertor.core.data.model.asEntityList
import com.example.currencyconvertor.core.database.dao.CurrencyDao
import com.example.currencyconvertor.core.database.model.ExchangeRateEntity
import com.example.currencyconvertor.core.database.model.asExternalModel
import com.example.currencyconvertor.core.datastore.CurrencyPreferencesDataSource
import com.example.currencyconvertor.core.model.ExchangeRate
import com.example.currencyconvertor.core.network.CurrencyNetworkDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class DefaultExchangeRatesRepository @Inject constructor(
    private val currencyDao: CurrencyDao,
    private val network: CurrencyNetworkDataSource,
    private val preferencesDataSource: CurrencyPreferencesDataSource
) : ExchangeRatesRepository {

    override val exchangeRates: Flow<List<ExchangeRate>> = preferencesDataSource.selectedCurrencyId
        .flatMapLatest {
            currencyDao.getExchangeRates(it)
        }.map { rateList ->
            rateList.map(ExchangeRateEntity::asExternalModel)
        }


    override suspend fun syncWith(synchronizer: Synchronizer): Boolean =
        synchronizer.changeListSync(
            timeStampReader = { it.exchangeRates },
            shouldUpdate = { lastTimeStamp, currentTimeStamp ->
                val minutesSince = currentTimeStamp.minus(lastTimeStamp).inWholeMinutes
                minutesSince > NetworkConstants.DEFAULT_MINUTES_THRESHOLD
            },
            timeStampUpdater = { timeStamp -> copy(exchangeRates = timeStamp) },
            updater = {
                val baseCurrencyId = preferencesDataSource.selectedCurrencyId.first()
                val exchangeRates =
                    network.getExchangeRates(base = baseCurrencyId).asEntityList(baseCurrencyId)
                currencyDao.insertExchangeRates(exchangeRates)
            }
        )
}