package com.example.currencyconvertor.core.data.testdoubles

import com.example.currencyconvertor.core.database.dao.CurrencyDao
import com.example.currencyconvertor.core.database.model.CurrencyEntity
import com.example.currencyconvertor.core.database.model.ExchangeRateEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update


class TestCurrencyDao : CurrencyDao {

    private val currencyEntitiesStateFlow = MutableStateFlow(emptyList<CurrencyEntity>())
    private val ratesEntitiesStateFlow = MutableStateFlow(emptyList<ExchangeRateEntity>())

    override fun getAllCurrencies(): Flow<List<CurrencyEntity>> = currencyEntitiesStateFlow

    override suspend fun insertCurrencies(currencies: List<CurrencyEntity>) {
        currencyEntitiesStateFlow.update { oldValues ->
            (oldValues + currencies).distinctBy(
                CurrencyEntity::id
            )
        }
    }

    override suspend fun deleteCurrencies() {
        currencyEntitiesStateFlow.update { emptyList() }
    }

    override suspend fun updateCurrencies(currencies: List<CurrencyEntity>) {
        super.updateCurrencies(currencies)
    }

    override suspend fun insertExchangeRates(rates: List<ExchangeRateEntity>) {
        ratesEntitiesStateFlow.update { oldValues ->
            (oldValues + rates).distinctBy(
                ExchangeRateEntity::id
            )
        }
    }

    override fun getExchangeRates(baseId: String): Flow<List<ExchangeRateEntity>> =
        ratesEntitiesStateFlow.map { rates ->
            rates.filter { it.baseCurrencyId == baseId }
        }

    override suspend fun deleteExchangeRates(currencyId: String) {
        ratesEntitiesStateFlow.update { rates ->
            rates.filterNot { it.baseCurrencyId == currencyId || it.targetCurrencyId == currencyId }
        }
    }
}
