package com.example.currencyconvertor.core.datastore

import app.cash.turbine.test
import com.example.currencyconvertor.core.Constants
import com.example.currencyconvertor.core.datastore.test.testPreferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class CurrencyPreferencesDataSourceTest {

    private val testScope = TestScope(UnconfinedTestDispatcher())

    private lateinit var subject: CurrencyPreferencesDataSource

    @Before
    fun setup() {
        subject = CurrencyPreferencesDataSource(
            testPreferencesDataStore(),
        )
    }

    @Test
    fun updating_time_stamps_reflect_in_store() = testScope.runTest {
        subject.updateNetworkFetchTimeStamps { currentTimeStamps ->
            currentTimeStamps.copy(
                currencies = 444,
                exchangeRates = 555,
            )
        }
        assertTrue(subject.getNetworkFetchTimeStamps().currencies == 444L)
        assertTrue(subject.getNetworkFetchTimeStamps().exchangeRates == 555L)
    }

    @Test
    fun initial_base_currency_id_should_be_default() = testScope.runTest {
        assertEquals(subject.selectedBaseCurrencyId.first(), Constants.DEFAULT_BASE_CURRENCY)
    }

    @Test
    fun updating_base_currency_id_reflect_in_store() = testScope.runTest {
        subject.selectedBaseCurrencyId.test {
            assertEquals(awaitItem(), Constants.DEFAULT_BASE_CURRENCY)
            subject.updateSelectedBaseCurrencyId("INR")
            assertEquals(awaitItem(), "INR")
        }
    }

    @Test
    fun updating_selected_currency_id_reflect_in_store() = testScope.runTest {
        subject.selectedCurrencyId.test {
            assertEquals(awaitItem(), "")
            subject.updateSelectedCurrencyId("USD")
            assertEquals(awaitItem(), "USD")
        }
    }
}
