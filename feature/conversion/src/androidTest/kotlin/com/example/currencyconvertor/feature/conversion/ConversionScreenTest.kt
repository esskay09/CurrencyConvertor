package com.example.currencyconvertor.feature.conversion

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.lifecycle.SavedStateHandle
import com.example.currencyconvertor.core.domain.GetConvertedCurrenciesUseCase
import com.example.currencyconvertor.core.model.Currency
import com.example.currencyconvertor.core.model.ExchangeRate
import com.example.currencyconvertor.core.testing.repository.TestCurrenciesRepository
import com.example.currencyconvertor.core.testing.repository.TestExchangeRatesRepository
import com.example.currencyconvertor.core.testing.util.TestNetworkMonitor
import com.example.currencyconvertor.core.testing.util.TestSyncManager
import org.junit.Before
import org.junit.Rule
import kotlin.test.Test

class ConversionScreenTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    private lateinit var currenciesRepository: TestCurrenciesRepository
    private lateinit var ratesRepository: TestExchangeRatesRepository
    private lateinit var conversionUseCase: GetConvertedCurrenciesUseCase
    private lateinit var viewModel: ConversionViewModel
    private val networkMonitor = TestNetworkMonitor()
    private val syncManager = TestSyncManager()


    @Before
    fun setup() {
        currenciesRepository = TestCurrenciesRepository()
        ratesRepository = TestExchangeRatesRepository()
        conversionUseCase = GetConvertedCurrenciesUseCase(currenciesRepository, ratesRepository)
        viewModel = ConversionViewModel(
            savedStateHandle = SavedStateHandle(
                initialState = mapOf("amount" to "0.0"),
            ),
            currenciesRepository = currenciesRepository,
            getConvertedCurrenciesUseCase = conversionUseCase,
            syncManager = syncManager,
            networkMonitor = networkMonitor,
        )
    }

    @Test
    fun testSyncingStateShown() {
        syncManager.setSyncing(true)

        composeTestRule.setContent {
            ConversionScreen(viewModel = viewModel)
        }

        composeTestRule.onNodeWithTag("syncing")
            .assertExists("Syncing card should be displayed")
            .assertIsDisplayed()
    }

    @Test
    fun testNoNetworkStateShown() {
        networkMonitor.setConnected(false)

        composeTestRule.setContent {
            ConversionScreen(viewModel = viewModel)
        }

        composeTestRule.onNodeWithTag("noNetwork")
            .assertExists("No Network card should be displayed")
            .assertIsDisplayed()
    }


    @Test
    fun testConversionScreenShown() {
        composeTestRule.setContent {
            ConversionScreen(viewModel = viewModel)
        }

        composeTestRule.onNodeWithTag("currencyDropdown")
            .assertExists("Conversion screen should be displayed")
            .assertIsDisplayed()
    }


    @Test
    fun testCurrenciesDisplayed() {
        composeTestRule.setContent {
            ConversionScreen(viewModel = viewModel)
        }
        currenciesRepository.sendCurrencies(dummyCurrencies)
        ratesRepository.sendExchangeRates(dummyExchangeRates)

        viewModel.onCurrencySelect(Currency("USD", "US Dollar"))
        dummyCurrencies.forEach {
            composeTestRule.onNodeWithTag("currencyItem${it.name}").assertExists()
        }
    }

    private val dummyCurrencies = listOf(
        Currency("USD", "US Dollar"),
        Currency("EUR", "Euro"),
        Currency("INR", "Indian Rupee")
    )

    private val dummyExchangeRates = listOf(
        ExchangeRate("USD", "EUR", 0.85),
        ExchangeRate("USD", "INR", 74.3),
        ExchangeRate("EUR", "INR", 87.6),
        ExchangeRate("USD", "USD", 1.0)
    )
}
