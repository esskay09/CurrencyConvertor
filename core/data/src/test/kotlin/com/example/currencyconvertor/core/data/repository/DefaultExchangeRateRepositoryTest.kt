package com.example.currencyconvertor.core.data.repository

import com.example.currencyconvertor.core.data.NetworkConstants
import com.example.currencyconvertor.core.data.Synchronizer
import com.example.currencyconvertor.core.data.model.asEntityList
import com.example.currencyconvertor.core.data.testdoubles.TestCurrencyDao
import com.example.currencyconvertor.core.data.testdoubles.TestCurrencyNetworkDataSource
import com.example.currencyconvertor.core.database.model.ExchangeRateEntity
import com.example.currencyconvertor.core.database.model.asExternalModel
import com.example.currencyconvertor.core.datastore.CurrencyPreferencesDataSource
import com.example.currencyconvertor.core.datastore.test.testPreferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Clock
import kotlinx.serialization.json.Json
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.minutes

class DefaultExchangeRateRepositoryTest {
    private val testScope = TestScope(UnconfinedTestDispatcher())

    private lateinit var subject: DefaultExchangeRatesRepository
    private lateinit var currencyDao: TestCurrencyDao
    private lateinit var network: TestCurrencyNetworkDataSource
    private lateinit var preferences: CurrencyPreferencesDataSource
    private lateinit var synchronizer: Synchronizer

    private val baseCurrency = "USD"

    @Before
    fun setup() {
        currencyDao = TestCurrencyDao()
        network = TestCurrencyNetworkDataSource(
            UnconfinedTestDispatcher(),
            Json { ignoreUnknownKeys = true }
        )
        preferences = CurrencyPreferencesDataSource(
            testPreferencesDataStore(),
        )
        synchronizer = TestSynchronizer(preferences)

        subject = DefaultExchangeRatesRepository(
            currencyDao = currencyDao,
            network = network,
            preferencesDataSource = preferences
        )
    }

    @Test
    fun repository_exchange_rates_stream_is_backed_by_currencies_dao() =
        testScope.runTest {
            assertEquals(
                currencyDao.getExchangeRates(baseCurrency)
                    .first()
                    .map(ExchangeRateEntity::asExternalModel),
                subject.exchangeRates
                    .first(),
            )
        }

    @Test
    fun repository_sync_pulls_from_network() =
        testScope.runTest {
            val lastTimeStamp = synchronizer.getTimeStamps().exchangeRates
            subject.syncWith(synchronizer)
            val newTimeStamp = synchronizer.getTimeStamps().exchangeRates


            val networkRates = network.getExchangeRates(baseCurrency).asEntityList(baseCurrency)
            val dbExchangeRates = currencyDao.getExchangeRates(baseCurrency)
                .first()
            assertEquals(
                networkRates.map(ExchangeRateEntity::id),
                dbExchangeRates.map(ExchangeRateEntity::id),
            )
            assertTrue(newTimeStamp > lastTimeStamp)
        }


    @Test
    fun repository_doesnt_pull_from_network_if_sync_is_not_needed() =
        testScope.runTest {
            synchronizer.updateTimeStamps {
                copy(exchangeRates = 0)
            }
            subject.syncWith(synchronizer)

            val rates = subject.exchangeRates.first()
            assert(rates.isNotEmpty())

            val savedTimeStamp =
                (Clock.System.now() - (NetworkConstants.DEFAULT_MINUTES_THRESHOLD - 2).minutes).toEpochMilliseconds()
            synchronizer.updateTimeStamps {
                copy(
                    exchangeRates = savedTimeStamp
                )
            }
            network.updateExchangeRates("USD")
            subject.syncWith(synchronizer)

            val lastTimeStamp = synchronizer.getTimeStamps().exchangeRates
            val newRates = subject.exchangeRates.first()

            assertEquals(savedTimeStamp, lastTimeStamp)
            assert(newRates.find { it.baseId == "USD" } != null)
        }

}
