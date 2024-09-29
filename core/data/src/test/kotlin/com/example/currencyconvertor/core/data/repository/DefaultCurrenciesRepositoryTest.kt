package com.example.currencyconvertor.core.data.repository

import com.example.currencyconvertor.core.data.NetworkConstants
import com.example.currencyconvertor.core.data.Synchronizer
import com.example.currencyconvertor.core.data.model.mapNetworkCurrenciesToEntity
import com.example.currencyconvertor.core.data.testdoubles.TestCurrencyDao
import com.example.currencyconvertor.core.data.testdoubles.TestCurrencyNetworkDataSource
import com.example.currencyconvertor.core.database.model.CurrencyEntity
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

class DefaultCurrenciesRepositoryTest {
    private val testScope = TestScope(UnconfinedTestDispatcher())

    private lateinit var subject: DefaultCurrenciesRepository
    private lateinit var currencyDao: TestCurrencyDao
    private lateinit var network: TestCurrencyNetworkDataSource
    private lateinit var preferences: CurrencyPreferencesDataSource
    private lateinit var synchronizer: Synchronizer


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

        subject = DefaultCurrenciesRepository(
            currencyDao = currencyDao,
            network = network,
        )
    }

    @Test
    fun repository_currencies_stream_is_backed_by_currencies_dao() =
        testScope.runTest {
            assertEquals(
                currencyDao.getAllCurrencies()
                    .first()
                    .map(CurrencyEntity::asExternalModel),
                subject.getCurrencies()
                    .first(),
            )
        }

    @Test
    fun repository_sync_pulls_from_network() =
        testScope.runTest {
            val lastTimeStamp = synchronizer.getTimeStamps().currencies
            subject.syncWith(synchronizer)
            val newTimeStamp = synchronizer.getTimeStamps().currencies
            val networkCurrencies = mapNetworkCurrenciesToEntity(network.getCurrencies())
            val dbCurrencies = currencyDao.getAllCurrencies()
                .first()
            assertEquals(
                networkCurrencies.map(CurrencyEntity::id),
                dbCurrencies.map(CurrencyEntity::id),
            )
            assertTrue(newTimeStamp > lastTimeStamp)
        }


    @Test
    fun repository_doesnt_pull_from_network_if_sync_is_not_needed() =
        testScope.runTest {
            synchronizer.updateTimeStamps {
                copy(currencies = 0)
            }
            subject.syncWith(synchronizer)

            val currencies = subject.getCurrencies().first()
            assert(currencies.isNotEmpty())

            val savedTimeStamp =
                (Clock.System.now() - (NetworkConstants.DEFAULT_MINUTES_THRESHOLD - 2).minutes).toEpochMilliseconds()
            synchronizer.updateTimeStamps {
                copy(
                    currencies = savedTimeStamp
                )
            }
            network.updateCurrencies("USD")
            subject.syncWith(synchronizer)

            val lastTimeStamp = synchronizer.getTimeStamps().currencies
            val newCurrencies = subject.getCurrencies().first()

            assertEquals(savedTimeStamp, lastTimeStamp)
            assert(newCurrencies.find { it.id == "USD" } != null)
        }

}
