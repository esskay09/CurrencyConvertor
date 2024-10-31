package com.example.currencyconvertor.core.network

import com.example.currencyconvertor.core.common.network.ConvertorDispatchers.IO
import com.example.currencyconvertor.core.common.network.Dispatcher
import com.example.currencyconvertor.core.network.model.ExchangeRatesNetwork
import com.example.currencyconvertor.core.network.samples.currenciesJson
import com.example.currencyconvertor.core.network.samples.exchangeRatesJson
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import javax.inject.Inject


class TestNetworkDataSource @Inject constructor(
    @Dispatcher(IO) private val ioDispatcher: CoroutineDispatcher,
    private val networkJson: Json
) : CurrencyNetworkDataSource {

    override suspend fun getCurrencies(): Map<String, String> = withContext(ioDispatcher) {
        networkJson.decodeFromString<Map<String, String>>(currenciesJson)
    }

    override suspend fun getExchangeRates(base: String): ExchangeRatesNetwork =
        withContext(ioDispatcher) {
            networkJson.decodeFromString<ExchangeRatesNetwork>(exchangeRatesJson)
        }
}
