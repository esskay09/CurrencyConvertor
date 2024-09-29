package com.example.currencyconvertor.core.data.testdoubles

import com.example.currencyconvertor.core.data.testdoubles.samples.currenciesJson
import com.example.currencyconvertor.core.data.testdoubles.samples.exchangeRatesJson
import com.example.currencyconvertor.core.network.ConvertorDispatchers.IO
import com.example.currencyconvertor.core.network.CurrencyNetworkDataSource
import com.example.currencyconvertor.core.network.Dispatcher
import com.example.currencyconvertor.core.network.model.ExchangeRatesNetwork
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import javax.inject.Inject

class TestCurrencyNetworkDataSource @Inject constructor(
    @Dispatcher(IO) private val ioDispatcher: CoroutineDispatcher,
    private val networkJson: Json
) : CurrencyNetworkDataSource {

    private var allCurrencies = networkJson.decodeFromString<Map<String, String>>(currenciesJson)
    private var exchangeRatesNetwork = networkJson.decodeFromString<ExchangeRatesNetwork>(exchangeRatesJson)

    override suspend fun getCurrencies(): Map<String, String> = allCurrencies

    override suspend fun getExchangeRates(base: String): ExchangeRatesNetwork = exchangeRatesNetwork


    fun updateCurrencies(vararg removeIds: String) {
        allCurrencies = allCurrencies.filterNot {
            it.key in removeIds
        }
    }

    fun updateExchangeRates(vararg removeIds: String) {
        exchangeRatesNetwork = exchangeRatesNetwork.copy(
            rates = exchangeRatesNetwork.rates.filterKeys {
                it !in removeIds
            }
        )
    }
}
