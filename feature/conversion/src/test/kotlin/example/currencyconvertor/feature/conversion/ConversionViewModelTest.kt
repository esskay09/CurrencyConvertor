package example.currencyconvertor.feature.conversion

import androidx.lifecycle.SavedStateHandle
import com.example.currencyconvertor.core.domain.GetConvertedCurrenciesUseCase
import com.example.currencyconvertor.core.model.ConvertedCurrency
import com.example.currencyconvertor.core.model.Currency
import com.example.currencyconvertor.core.model.ExchangeRate
import com.example.currencyconvertor.core.testing.repository.TestCurrenciesRepository
import com.example.currencyconvertor.core.testing.repository.TestExchangeRatesRepository
import com.example.currencyconvertor.core.testing.util.MainDispatcherRule
import com.example.currencyconvertor.feature.conversion.ConversionViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.math.abs
import kotlin.math.pow
import kotlin.test.assertEquals


class ConversionViewModelTest {

    @get:Rule
    val dispatcherRule = MainDispatcherRule()

    private val currenciesRepository = TestCurrenciesRepository()
    private val ratesRepository = TestExchangeRatesRepository()

    private lateinit var useCase: GetConvertedCurrenciesUseCase

    private lateinit var viewModel: ConversionViewModel

    @Before
    fun setup() {
        useCase = GetConvertedCurrenciesUseCase(
            currenciesRepository = currenciesRepository,
            exchangeRatesRepository = ratesRepository,
        )
        viewModel = ConversionViewModel(
            savedStateHandle = SavedStateHandle(),
            currenciesRepository = currenciesRepository,
            getConvertedCurrenciesUseCase = useCase,
        )
    }


    @Test
    fun uiState_whenInitialized_Empty() = runTest {
        val collectJob =
            launch(UnconfinedTestDispatcher()) { viewModel.conversionUiState.collect() }

        currenciesRepository.sendCurrencies(emptyList())
        ratesRepository.sendExchangeRates(emptyList())
        currenciesRepository.setSelectedCurrency("")

        val state = viewModel.conversionUiState.value
        assertEquals(emptyList(), state.currencies)
        assertEquals(null, state.selectedCurrency)

        collectJob.cancel()
    }

    @Test
    fun uiState_whenInitializedAndLoaded_thenMatchCurrenciesFromRepository() = runTest {
        val collectJob =
            launch(UnconfinedTestDispatcher()) { viewModel.conversionUiState.collect() }

        currenciesRepository.sendCurrencies(testCurrencies)
        ratesRepository.sendExchangeRates(testRates)
        currenciesRepository.setSelectedCurrency("USD")


        val state = viewModel.conversionUiState.value
        assertEquals(testCurrencies, state.currencies)
        assertEquals("USD", state.selectedCurrency?.id)

        collectJob.cancel()
    }

    @Test
    fun uiState_whenAmountChanged_reflectAmount() = runTest {
        val collectJob = launch(UnconfinedTestDispatcher()) { viewModel.amountFlow.collect() }
        viewModel.onAmountChange("100")
        assertEquals(100.0, viewModel.amountFlow.value)
        collectJob.cancel()
    }


    @Test
    fun uiState_whenAmountOrCountryChange_reflectConvertedCurrencies() = runTest {
        val collectJob =
            launch(UnconfinedTestDispatcher()) { viewModel.conversionUiState.collect() }

        val selectedCurrency = Currency("INR", "Indian Rupee")
        val amount = 100.0
        viewModel.onAmountChange("12")
        currenciesRepository.sendCurrencies(testCurrencies)
        ratesRepository.sendExchangeRates(testRates)
        viewModel.onCurrencySelect(selectedCurrency)

        viewModel.onAmountChange(amount.toString())


        val state = viewModel.conversionUiState.value

        val expected = listOf(
            ConvertedCurrency(
                from = selectedCurrency,
                to = Currency("USD", "US Dollar"),
                value = amount / 75.0
            ),
            ConvertedCurrency(
                from = selectedCurrency,
                to = Currency("EUR", "Euro"),
                value = amount / 75.0 * 0.85
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

        assertConvertedCurrenciesEqual(expected, state.convertedCurrencies)

        collectJob.cancel()
    }
}

private fun assertConvertedCurrenciesEqual(
    expected: List<ConvertedCurrency>,
    actual: List<ConvertedCurrency>,
    precision: Int = 4
) {
    assert(expected.size == actual.size) { "List sizes are different." }

    expected.zip(actual).forEach { (expectedItem, actualItem) ->
        assert(expectedItem.from == actualItem.from) { "From currencies are different: ${expectedItem.from} != ${actualItem.from}" }
        assert(expectedItem.to == actualItem.to) { "To currencies are different: ${expectedItem.to} != ${actualItem.to}" }
        assert(expectedItem.value.isApproximatelyEqual(actualItem.value, precision)) {
            "Values are different: ${expectedItem.value} != ${actualItem.value} (up to $precision decimal places)"
        }
    }
}


fun Double.isApproximatelyEqual(other: Double, precision: Int = 4): Boolean {
    val scale = 10.0.pow(precision)
    return abs(this - other) < 1 / scale
}

private val testCurrencies = listOf(
    Currency("USD", "US Dollar"),
    Currency("EUR", "Euro"),
    Currency("JPY", "Japanese Yen"),
    Currency("INR", "Indian Rupee"),
)

private val testRates = listOf(
    ExchangeRate(baseId = "USD", targetId = "EUR", rate = 0.85),
    ExchangeRate(baseId = "USD", targetId = "JPY", rate = 110.0),
    ExchangeRate(baseId = "USD", targetId = "INR", rate = 75.0),
    ExchangeRate(baseId = "EUR", targetId = "USD", rate = 1.18),
    ExchangeRate(baseId = "USD", targetId = "USD", rate = 1.0),
)