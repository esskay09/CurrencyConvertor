package com.example.currencyconvertor.core.datastore

import com.example.currencyconvertor.core.datastore.test.testPreferencesDataStore
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
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
    fun updatingTimeStampsShouldReflectInDataStore() = testScope.runTest {
        subject.updateNetworkFetchTimeStamps { currentTimeStamps ->
            currentTimeStamps.copy(
                currencies = 444,
                conversionRates = 555,
            )
        }
        assertTrue(subject.getNetworkFetchTimeStamps().currencies == 444L)
        assertTrue(subject.getNetworkFetchTimeStamps().conversionRates == 555L)
    }
}
