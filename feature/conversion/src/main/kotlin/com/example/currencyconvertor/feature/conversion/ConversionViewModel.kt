package com.example.currencyconvertor.feature.conversion

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.currencyconvertor.core.data.repository.CurrenciesRepository
import com.example.currencyconvertor.core.data.util.NetworkMonitor
import com.example.currencyconvertor.core.data.util.SyncManager
import com.example.currencyconvertor.core.domain.GetConvertedCurrenciesUseCase
import com.example.currencyconvertor.core.model.ConvertedCurrency
import com.example.currencyconvertor.core.model.Currency
import com.example.currencyconvertor.core.result.Result
import com.example.currencyconvertor.core.result.asResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ConversionViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    syncManager: SyncManager,
    networkMonitor: NetworkMonitor,
    private val currenciesRepository: CurrenciesRepository,
    getConvertedCurrenciesUseCase: GetConvertedCurrenciesUseCase
) : ViewModel() {

    val isSyncing = syncManager.isSyncing
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = false,
        )

    val isOnline = networkMonitor.isOnline
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = true
        )

    val amountStringFlow = savedStateHandle.getStateFlow(
        key = "amount",
        initialValue = "",
    )

    val amountFlow = amountStringFlow.map { it.toDoubleOrNull() ?: 0.0 }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = 0.0,
        )

    val conversionUiState: StateFlow<ConversionUiState> = conversionUiState(
        amount = amountFlow,
        currenciesRepository = currenciesRepository,
        getConvertedCurrenciesUseCase = getConvertedCurrenciesUseCase,
    )
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = ConversionUiState(),
        )

    fun onAmountChange(amount: String) {
        savedStateHandle["amount"] = amount
    }

    fun onCurrencySelect(currency: Currency) {
        viewModelScope.launch {
            currenciesRepository.setSelectedCurrency(currency.id)
        }
    }
}

private fun conversionUiState(
    amount: Flow<Double>,
    currenciesRepository: CurrenciesRepository,
    getConvertedCurrenciesUseCase: GetConvertedCurrenciesUseCase,
): Flow<ConversionUiState> {
    return amount.flatMapLatest {
        combine(
            currenciesRepository.currencies,
            currenciesRepository.selectedCurrency,
            getConvertedCurrenciesUseCase(amount = it),
            ::Triple
        ).asResult()
            .map { result ->
                when (result) {
                    is Result.Success -> {
                        val (currencies, selectedCurrency, conversions) = result.data
                        ConversionUiState(
                            currencies = currencies,
                            selectedCurrency = selectedCurrency,
                            convertedCurrencies = conversions,
                            errorMessage = null
                        )
                    }

                    is Result.Error -> ConversionUiState(errorMessage = result.exception.message)
                    is Result.Loading -> ConversionUiState()
                }
            }
    }
}

data class ConversionUiState(
    val currencies: List<Currency> = emptyList(),
    val convertedCurrencies: List<ConvertedCurrency> = emptyList(),
    val selectedCurrency: Currency? = null,
    val errorMessage: String? = null
)
