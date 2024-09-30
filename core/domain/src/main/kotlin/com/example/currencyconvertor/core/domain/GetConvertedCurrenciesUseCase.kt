package com.example.currencyconvertor.core.domain

import com.example.currencyconvertor.core.data.repository.CurrenciesRepository
import com.example.currencyconvertor.core.data.repository.ExchangeRatesRepository
import com.example.currencyconvertor.core.model.ConvertedCurrency
import com.example.currencyconvertor.core.model.Currency
import com.example.currencyconvertor.core.model.ExchangeRate
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject


class GetConvertedCurrenciesUseCase @Inject constructor(
    private val currenciesRepository: CurrenciesRepository,
    private val exchangeRatesRepository: ExchangeRatesRepository
) {

    operator fun invoke(
        amount: Double
    ): Flow<List<ConvertedCurrency>> = combine(
        currenciesRepository.currencies,
        exchangeRatesRepository.exchangeRates,
        currenciesRepository.selectedBaseCurrency,
        currenciesRepository.selectedCurrency
    ) { currencies, exchangeRates, baseCurrencyId, selectedCurrency ->

        if (currencies.isEmpty() || baseCurrencyId.isEmpty() || selectedCurrency == null) return@combine emptyList()

        if (exchangeRates.isEmpty()) return@combine emptyConvertedList(
            currencies,
            selectedCurrency
        )

        val exchangeRatesMap = buildExchangeRateMap(exchangeRates, baseCurrencyId)

        val selectedCurrencyExchangeRate = exchangeRatesMap[selectedCurrency.id]
            ?: error("Exchange rate for selected currency not found")

        val inverseSelectedCurrencyRate =
            if (selectedCurrencyExchangeRate == 0.0) 0.0 else 1.0 / selectedCurrencyExchangeRate
        currencies.map { currency ->
            val currencyExchangeRateInBase = exchangeRatesMap[currency.id] ?: 0.0
            val exchangeValue =
                (currencyExchangeRateInBase * inverseSelectedCurrencyRate).times(amount)
            ConvertedCurrency(
                from = selectedCurrency,
                to = currency,
                value = exchangeValue
            )
        }
    }

    private fun buildExchangeRateMap(
        exchangeRates: List<ExchangeRate>,
        baseCurrencyId: String
    ): Map<String, Double> {
        val exchangeRatesOfCurrentBase = exchangeRates.filter { it.baseId == baseCurrencyId }
        val exchangeRatesMap: MutableMap<String, Double> = mutableMapOf()
        exchangeRatesOfCurrentBase.forEach { exchangeRate ->
            exchangeRatesMap[exchangeRate.targetId] = exchangeRate.rate
        }
        return exchangeRatesMap
    }

    private fun emptyConvertedList(
        currencies: List<Currency>,
        selectedCurrency: Currency
    ) = currencies.map { currency ->
        ConvertedCurrency(
            from = selectedCurrency,
            to = currency,
            value = 0.0
        )
    }
}

