package com.example.currencyconvertor.core.data.testdoubles

import com.example.currencyconvertor.core.database.dao.CurrencyDao
import com.example.currencyconvertor.core.database.model.ConversionRateEntity
import com.example.currencyconvertor.core.database.model.CurrencyEntity
import com.example.currencyconvertor.core.database.model.CurrencyWithConversionRates
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update


class TestCurrencyDao : CurrencyDao {

    private val currencyEntitiesStateFlow = MutableStateFlow(emptyList<CurrencyEntity>())
    private val ratesEntitiesStateFlow = MutableStateFlow(emptyList<ConversionRateEntity>())

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

    override suspend fun insertConversionRates(rates: List<ConversionRateEntity>) {
        ratesEntitiesStateFlow.update { oldValues ->
            (oldValues + rates).distinctBy(
                ConversionRateEntity::id
            )
        }
    }

    override fun getConversionRates(baseId: String): Flow<CurrencyWithConversionRates?> {
        return ratesEntitiesStateFlow.map { rates ->
            val base = currencyEntitiesStateFlow.value.find { it.id == baseId } ?: return@map null
            CurrencyWithConversionRates(
                base = base,
                rates = rates.filter { it.baseCurrencyId == baseId }
            )
        }
    }

    override suspend fun deleteConversationRates(currencyId: String) {
        ratesEntitiesStateFlow.update { rates ->
            rates.filterNot { it.baseCurrencyId == currencyId || it.target.id == currencyId }
        }
    }
}
