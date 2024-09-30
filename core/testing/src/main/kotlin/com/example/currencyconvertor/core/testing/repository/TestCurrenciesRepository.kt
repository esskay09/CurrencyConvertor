package com.example.currencyconvertor.core.testing.repository

import com.example.currencyconvertor.core.Constants
import com.example.currencyconvertor.core.data.Synchronizer
import com.example.currencyconvertor.core.data.repository.CurrenciesRepository
import com.example.currencyconvertor.core.model.Currency
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull


class TestCurrenciesRepository : CurrenciesRepository {

    private val currenciesFlow: MutableSharedFlow<List<Currency>> =
        MutableSharedFlow(replay = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)

    private val selectedBaseCurrencyFlow: MutableSharedFlow<String> =
        MutableSharedFlow(replay = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)

    private val selectedCurrencyFlow: MutableSharedFlow<Currency?> =
        MutableSharedFlow(replay = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)


    override val currencies: Flow<List<Currency>> = currenciesFlow

    override val selectedBaseCurrency: Flow<String> = selectedBaseCurrencyFlow

    override val selectedCurrency: Flow<Currency?> = selectedCurrencyFlow

    fun sendCurrencies(currencies: List<Currency>) {
        currenciesFlow.tryEmit(currencies)
    }

    fun setSelectedBaseCurrency(id: String) {
        selectedBaseCurrencyFlow.tryEmit(id)
    }

    suspend fun setSelectedCurrency(currency: Currency) {
        selectedCurrencyFlow.tryEmit(currency)
    }

    override suspend fun setSelectedCurrency(id: String) {
        selectedCurrencyFlow.tryEmit(currencies.firstOrNull()?.firstOrNull  { it.id == id })
    }


    init {
        setSelectedBaseCurrency(Constants.DEFAULT_BASE_CURRENCY)
    }

    override suspend fun syncWith(synchronizer: Synchronizer): Boolean = true
}
