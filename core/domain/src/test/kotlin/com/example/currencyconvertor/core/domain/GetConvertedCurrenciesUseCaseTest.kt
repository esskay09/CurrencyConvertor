package com.example.currencyconvertor.core.domain

import com.example.currencyconvertor.core.model.ConvertedCurrency
import com.example.currencyconvertor.core.model.Currency
import com.example.currencyconvertor.core.model.ExchangeRate
import com.example.currencyconvertor.core.testing.data.currenciesTestData
import com.example.currencyconvertor.core.testing.data.exchangeRatesTestData
import com.example.currencyconvertor.core.testing.repository.TestCurrenciesRepository
import com.example.currencyconvertor.core.testing.repository.TestExchangeRatesRepository
import com.example.currencyconvertor.core.testing.util.MainDispatcherRule
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertTrue

class GetConvertedCurrenciesUseCaseTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val currenciesRepository = TestCurrenciesRepository()
    private val ratesRepository = TestExchangeRatesRepository()

    val useCase = GetConvertedCurrenciesUseCase(
        currenciesRepository,
        ratesRepository,
    )

    @Test
    fun whenNoCurrencies_emptyListIsReturned() = runTest {
        currenciesRepository.sendCurrencies(emptyList())
        ratesRepository.sendExchangeRates(emptyList())
        val convertedCurrencies =
            useCase(amount = 1.0, selectedCurrency = Currency("USD", "US Dollar"))
        assertTrue(convertedCurrencies.first().isEmpty())
    }

    @Test
    fun whenNoRates_CurrencyListWithZeroValuesIsReturned() = runTest {
        currenciesRepository.sendCurrencies(currenciesTestData)
        ratesRepository.sendExchangeRates(emptyList())
        val convertedCurrencies =
            useCase(amount = 0.0, selectedCurrency = Currency("USD", "US Dollar"))

        assertTrue { convertedCurrencies.first().isNotEmpty() }
        assertTrue(convertedCurrencies.first().all { it.value == 0.0 })
    }

    @Test
    fun whenAmountIsZero_CurrencyListWithZeroValuesIsReturned() = runTest {
        currenciesRepository.sendCurrencies(currenciesTestData)
        ratesRepository.sendExchangeRates(exchangeRatesTestData)
        val convertedCurrencies =
            useCase(amount = 0.0, selectedCurrency = Currency("USD", "US Dollar"))
        assertTrue { convertedCurrencies.first().isNotEmpty() }
        assertTrue(convertedCurrencies.first().all { it.value == 0.0 })
    }

    @Test
    fun whenValidAmount_CurrencyListWithCorrectValuesIsReturned() = runTest {
        val amount = 2.0
        val selectedCurrency = Currency("INR", "Indian Rupee")
        currenciesRepository.sendCurrencies(
            listOf(
                Currency("USD", "US Dollar"),
                Currency("EUR", "Euro"),
                Currency("JPY", "Japanese Yen"),
                Currency("INR", "Indian Rupee")
            )
        )
        ratesRepository.sendExchangeRates(
            listOf(
                ExchangeRate("USD", "EUR", 0.8),
                ExchangeRate("USD", "JPY", 110.0),
                ExchangeRate("USD", "INR", 75.0),
                ExchangeRate("USD", "USD", 1.0),
                ExchangeRate("EUR", "USD", 1.2),
                ExchangeRate("EUR", "JPY", 125.0),
            )
        )

        val expected = listOf(
            ConvertedCurrency(
                from = selectedCurrency,
                to = Currency("USD", "US Dollar"),
                value = amount / 75.0
            ),
            ConvertedCurrency(
                from = selectedCurrency,
                to = Currency("EUR", "Euro"),
                value = amount / 75.0 * 0.8
            ),
            ConvertedCurrency(
                from = selectedCurrency,
                to = Currency("JPY", "Japanese Yen"),
                value = amount / 75.0 * 110.0
            ),
            ConvertedCurrency(
                from = selectedCurrency,
                to = Currency("INR", "Indian Rupee"),
                value = amount
            ),
        )
        val convertedCurrencies = useCase(amount = amount, selectedCurrency = selectedCurrency)

        assertContentEquals(expected, convertedCurrencies.first())
    }

    @Test
    fun whenSomeRatesAreMissing_ZeroValuesForMissingRates() = runTest {
        currenciesRepository.sendCurrencies(currenciesTestData)
        ratesRepository.sendExchangeRates(
            listOf(
                ExchangeRate("USD", "EUR", 0.8),
                ExchangeRate("USD", "USD", 1.0),
            )
        )
        val convertedCurrencies =
            useCase(amount = 100.0, selectedCurrency = Currency("USD", "US Dollar"))
        assertTrue { convertedCurrencies.first().any { it.to.id == "JPY" && it.value == 0.0 } }
    }

    @Test
    fun whenSelectedCurrencyIsNotInRates_ErrorIsThrown() = runTest {
        currenciesRepository.sendCurrencies(currenciesTestData)
        ratesRepository.sendExchangeRates(
            listOf(
                ExchangeRate("USD", "EUR", 0.8),
                ExchangeRate("USD", "USD", 1.0),
            )
        )
        val selectedCurrency = Currency("INR", "Indian Rupee")
        useCase(amount = 100.0, selectedCurrency = selectedCurrency).catch {
            assertTrue(it is IllegalStateException)
        }.collect{

        }
    }
}
