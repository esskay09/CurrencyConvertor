package com.example.currencyconvertor.core.network

import com.example.currencyconvertor.core.network.model.ExchangeRatesNetwork

interface CurrencyNetworkDataSource {
    suspend fun getCurrencies(): Map<String, String>
    suspend fun getExchangeRates(base: String): ExchangeRatesNetwork
}
