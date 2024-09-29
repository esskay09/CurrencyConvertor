package com.example.currencyconvertor.core.testing.repository

import com.example.currencyconvertor.core.Constants
import com.example.currencyconvertor.core.data.Synchronizer
import com.example.currencyconvertor.core.data.repository.CurrenciesRepository
import com.example.currencyconvertor.core.model.Currency
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow


class TestCurrenciesRepository : CurrenciesRepository {

    private val currenciesFlow: MutableSharedFlow<List<Currency>> =
        MutableSharedFlow(replay = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)

    private val selectedBaseCurrencyFlow: MutableSharedFlow<String> =
        MutableSharedFlow(replay = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)

    override val currencies: Flow<List<Currency>>
        get() = currenciesFlow

    override val selectedBaseCurrency: Flow<String> = selectedBaseCurrencyFlow

    fun sendCurrencies(currencies: List<Currency>) {
        currenciesFlow.tryEmit(currencies)
    }

    fun setSelectedBaseCurrency(id: String) {
        selectedBaseCurrencyFlow.tryEmit(id)
    }

    init {
        setSelectedBaseCurrency(Constants.DEFAULT_BASE_CURRENCY)
    }

    override suspend fun syncWith(synchronizer: Synchronizer): Boolean = true
}
