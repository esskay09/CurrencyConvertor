package com.example.currencyconvertor.core.data.repository

import com.example.currencyconvertor.core.data.Syncable
import com.example.currencyconvertor.core.model.ExchangeRate
import kotlinx.coroutines.flow.Flow

interface ExchangeRatesRepository: Syncable {
    val exchangeRates: Flow<List<ExchangeRate>>
}