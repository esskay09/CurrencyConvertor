package com.example.currencyconvertor.core.data.repository

import com.example.currencyconvertor.core.data.Syncable
import com.example.currencyconvertor.core.model.Currency
import kotlinx.coroutines.flow.Flow

interface CurrenciesRepository : Syncable {
    val currencies: Flow<List<Currency>>
    val selectedBaseCurrency: Flow<String>
    val selectedCurrency: Flow<Currency?>

    suspend fun setSelectedCurrency(currencyId: String)
}