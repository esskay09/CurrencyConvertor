package com.example.currencyconvertor.core.data.model

import com.example.currencyconvertor.core.database.model.ExchangeRateEntity
import com.example.currencyconvertor.core.network.model.ExchangeRatesNetwork

fun ExchangeRatesNetwork.asEntityList(
    baseId: String
): List<ExchangeRateEntity> {
    return this.rates.keys.map { targetCurrencyId ->
        ExchangeRateEntity(
            id = "${baseId}_${targetCurrencyId}",
            baseCurrencyId = baseId,
            targetCurrencyId = targetCurrencyId,
            rate = this.rates[targetCurrencyId]!!
        )
    }
}