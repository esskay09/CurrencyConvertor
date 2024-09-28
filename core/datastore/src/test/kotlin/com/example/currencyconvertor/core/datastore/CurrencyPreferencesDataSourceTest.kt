package com.example.currencyconvertor.core.datastore

import com.example.currencyconvertor.core.datastore.test.testFetchTimesDataStore
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import kotlin.test.assertTrue

class CurrencyPreferencesDataSourceTest {

    private val testScope = TestScope(UnconfinedTestDispatcher())

    private lateinit var subject: CurrencyPreferencesDataSource

    @get:Rule
    val tmpFolder: TemporaryFolder = TemporaryFolder.builder().assureDeletion().build()

    @Before
    fun setup() {
        subject = CurrencyPreferencesDataSource(
            tmpFolder.testFetchTimesDataStore(testScope),
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
        assertTrue(subject.getNetworkFetchTimeStamps().timeStamps.currencies == 444L)
        assertTrue(subject.getNetworkFetchTimeStamps().timeStamps.conversionRates == 555L)
    }
}
