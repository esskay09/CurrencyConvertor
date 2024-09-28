package com.example.currencyconvertor.core.network

import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import org.junit.Before
import org.junit.Test
import kotlin.test.assertContains
import kotlin.test.assertTrue

class NetworkDataSourceTest {

    private lateinit var subject: TestNetworkDataSource

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        subject = TestNetworkDataSource(
            ioDispatcher = testDispatcher,
            networkJson = Json { ignoreUnknownKeys = true },
        )
    }

    @Test
    fun testDeserializationOfCurrencies() = runTest(testDispatcher) {
        val currencies = subject.getCurrencies()
        assertContains(currencies.keys, "USD")
        assertTrue(currencies["USD"] == "United States Dollar")
    }

    @Test
    fun testDeserializationOfExchangeRates() = runTest(testDispatcher) {
        val exchangeRates = subject.getExchangeRates("USD")
        assertContains(exchangeRates.rates, "USD")
        assertTrue(exchangeRates.rates["USD"] == 1.0)
        assertTrue(exchangeRates.rates["INR"] == 83.71655)
    }
}
