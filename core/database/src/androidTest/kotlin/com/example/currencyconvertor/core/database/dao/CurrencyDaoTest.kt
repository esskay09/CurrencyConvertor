package com.example.currencyconvertor.core.database.dao

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.currencyconvertor.core.database.CurrencyDatabase
import com.example.currencyconvertor.core.database.model.CurrencyEntity
import com.example.currencyconvertor.core.database.model.ExchangeRateEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class CurrencyDaoTest {

    private lateinit var currencyDao: CurrencyDao
    private lateinit var db: CurrencyDatabase

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context,
            CurrencyDatabase::class.java,
        ).build()
        currencyDao = db.currencyDao()
    }

    @After
    fun closeDb() = db.close()

    @Test
    fun currencyDao_fetches_countries() = runTest {
        val currencyEntities = listOf(
            CurrencyEntity(
                id = "USD",
                name = "US Dollar",
            ),
            CurrencyEntity(
                id = "EUR",
                name = "Euro",
            ),
            CurrencyEntity(
                id = "JPY",
                name = "Japanese Yen",
            ),
            CurrencyEntity(
                id = "GBP",
                name = "British Pound",
            ),
        )
        currencyDao.updateCurrencies(currencyEntities)

        val fetchedCurrencies = currencyDao.getAllCurrencies().first()
        assertEquals(currencyEntities, fetchedCurrencies)
    }

    @Test
    fun updating_currencies_removes_existing_currencies() = runTest {
        val currencyEntities = currencies()
        currencyDao.updateCurrencies(currencyEntities)

        val fetchedCurrencies = currencyDao.getAllCurrencies().first()
        assertEquals(currencyEntities, fetchedCurrencies)

        val updatedCurrencies = listOf(
            CurrencyEntity(
                id = "USD",
                name = "US Dollar",
            ),
            CurrencyEntity(
                id = "EUR",
                name = "Euro",
            ),
        )
        currencyDao.updateCurrencies(updatedCurrencies)

        val fetchedUpdatedCurrencies = currencyDao.getAllCurrencies().first()
        assertEquals(updatedCurrencies, fetchedUpdatedCurrencies)
    }


    @Test
    fun currencyDao_fetches_currency_rates_by_base_id_when_currency_does_not_exist() = runTest {
        val currencies = currencies()
        val conversionRates = exchangeRates()

        currencyDao.updateCurrencies(currencies)
        currencyDao.insertExchangeRates(rates = conversionRates)

        val fetchedCurrencyWithRates = currencyDao.getExchangeRates("NOCOUNTRY").first()
        assertEquals(emptyList(), fetchedCurrencyWithRates)
    }

    @Test
    fun currencyDao_fetches_currency_rates_by_base_id() = runTest {
        val currencies = currencies()
        val exchangeRates = exchangeRates()

        val baseCurrency = currencies.find { it.id == "USD" }!!.id

        currencyDao.updateCurrencies(currencies)
        currencyDao.insertExchangeRates(rates = exchangeRates)

        val fetchedCurrencyWithRates = currencyDao.getExchangeRates(baseCurrency).first()

        assertTrue (fetchedCurrencyWithRates.all { it.baseCurrencyId == baseCurrency })
        val expectedRateList = exchangeRates.filter { it.baseCurrencyId == baseCurrency }
        assertEquals(expectedRateList, fetchedCurrencyWithRates)
    }

    private fun currencies() = listOf(
        CurrencyEntity(
            id = "USD",
            name = "US Dollar",
        ),
        CurrencyEntity(
            id = "EUR",
            name = "Euro",
        ),
        CurrencyEntity(
            id = "JPY",
            name = "Japanese Yen",
        ),
        CurrencyEntity(
            id = "GBP",
            name = "British Pound",
        ),
    )

    private fun exchangeRates() = listOf(
        ExchangeRateEntity(
            id = "USD_EUR",
            baseCurrencyId = "USD",
            targetCurrencyId = "EUR",
            rate = 0.85,
        ),
        ExchangeRateEntity(
            id = "USD_JPY",
            baseCurrencyId = "USD",
            targetCurrencyId = "JPY",
            rate = 110.0,
        ),
        ExchangeRateEntity(
            id = "USD_GBP",
            baseCurrencyId = "USD",
            targetCurrencyId = "GBP",
            rate = 0.72,
        ),
        ExchangeRateEntity(
            id = "EUR_USD",
            baseCurrencyId = "EUR",
            targetCurrencyId = "USD",
            rate = 1.18,
        ),
        ExchangeRateEntity(
            id = "EUR_JPY",
            baseCurrencyId = "EUR",
            targetCurrencyId = "JPY",
            rate = 129.41,
        ),
        ExchangeRateEntity(
            id = "EUR_GBP",
            baseCurrencyId = "EUR",
            targetCurrencyId = "GBP",
            rate = 0.85,
        ),
    )
}