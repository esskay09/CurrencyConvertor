package com.example.currencyconvertor.core.data.repository

import com.example.currencyconvertor.core.data.Syncable
import com.example.currencyconvertor.core.model.Currency
import kotlinx.coroutines.flow.Flow

interface CurrenciesRepository: Syncable {
    fun getCurrencies(): Flow<List<Currency>>
}