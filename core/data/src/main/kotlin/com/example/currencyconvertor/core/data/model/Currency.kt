package com.example.currencyconvertor.core.data.model

import com.example.currencyconvertor.core.database.model.CurrencyEntity


fun mapNetworkCurrenciesToEntity(currencies: Map<String, String>): List<CurrencyEntity> {
    return currencies.map { (key, value) -> CurrencyEntity(id = key, name = value) }
}