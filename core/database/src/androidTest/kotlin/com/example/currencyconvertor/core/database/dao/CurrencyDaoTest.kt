package com.example.currencyconvertor.core.database.dao

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.currencyconvertor.core.database.CurrencyDatabase
import com.example.currencyconvertor.core.database.model.ConversionRateEntity
import com.example.currencyconvertor.core.database.model.CurrencyEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

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
        val conversionRates = currencyRates()

        currencyDao.updateCurrencies(currencies)
        currencyDao.insertConversionRates(rates = conversionRates)

        val fetchedCurrencyWithRates = currencyDao.getConversionRates("NOCOUNTRY").first()
        assertEquals(null, fetchedCurrencyWithRates)
    }

    @Test
    fun currencyDao_fetches_currency_rates_by_base_id() = runTest {
        val currencies = currencies()
        val conversionRates = currencyRates()

        val baseCurrency = currencies.find { it.id == "USD" }!!

        currencyDao.updateCurrencies(currencies)
        currencyDao.insertConversionRates(rates = conversionRates)

        val fetchedCurrencyWithRates = currencyDao.getConversionRates(baseCurrency.id).first()

        assertEquals(baseCurrency, fetchedCurrencyWithRates?.base)
        val expectedRateList = conversionRates.filter { it.baseCurrencyId == baseCurrency.id }
        assertEquals(expectedRateList, fetchedCurrencyWithRates?.rates)
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

    private fun currencyRates() = listOf(
        ConversionRateEntity(
            id = "USD_EUR",
            baseCurrencyId = "USD",
            target = CurrencyEntity(
                id = "EUR",
                name = "Euro",
            ),
            rate = 0.85,
        ),
        ConversionRateEntity(
            id = "USD_JPY",
            baseCurrencyId = "USD",
            target = CurrencyEntity(
                id = "JPY",
                name = "Japanese Yen",
            ),
            rate = 110.0,
        ),
        ConversionRateEntity(
            id = "USD_GBP",
            baseCurrencyId = "USD",
            target = CurrencyEntity(
                id = "GBP",
                name = "British Pound",
            ),
            rate = 0.72,
        ),
        ConversionRateEntity(
            id = "EUR_USD",
            baseCurrencyId = "EUR",
            target = CurrencyEntity(
                id = "USD",
                name = "US Dollar",
            ),
            rate = 1.18,
        ),
        ConversionRateEntity(
            id = "EUR_JPY",
            baseCurrencyId = "EUR",
            target = CurrencyEntity(
                id = "JPY",
                name = "Japanese Yen",
            ),
            rate = 129.41,
        ),
        ConversionRateEntity(
            id = "EUR_GBP",
            baseCurrencyId = "EUR",
            target = CurrencyEntity(
                id = "GBP",
                name = "British Pound",
            ),
            rate = 0.85,
        ),

        )
}