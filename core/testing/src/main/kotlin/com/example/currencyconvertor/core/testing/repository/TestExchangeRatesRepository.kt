

package com.example.currencyconvertor.core.testing.repository

import com.example.currencyconvertor.core.data.Synchronizer
import com.example.currencyconvertor.core.data.repository.CurrenciesRepository
import com.example.currencyconvertor.core.data.repository.ExchangeRatesRepository
import com.example.currencyconvertor.core.model.Currency
import com.example.currencyconvertor.core.model.ExchangeRate
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow


class TestExchangeRatesRepository : ExchangeRatesRepository {

    private val ratesFlow: MutableSharedFlow<List<ExchangeRate>> =
        MutableSharedFlow(replay = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)

    override val exchangeRates: Flow<List<ExchangeRate>>
        get() =  ratesFlow

    fun sendExchangeRates(rates: List<ExchangeRate>) {
        ratesFlow.tryEmit(rates)
    }


    override suspend fun syncWith(synchronizer: Synchronizer): Boolean = true
}
