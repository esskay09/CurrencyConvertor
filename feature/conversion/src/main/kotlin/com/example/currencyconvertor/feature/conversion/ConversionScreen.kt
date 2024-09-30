package com.example.currencyconvertor.feature.conversion

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.currencyconvertor.core.model.ConvertedCurrency
import com.example.currencyconvertor.core.model.Currency
import com.example.currencyconvertor.feature.topic.R
import com.example.currencyconvertor.theme.CurrencyConvertorTheme
import java.util.Locale

@Composable
fun ConversionScreen(
    viewModel: ConversionViewModel = hiltViewModel()
) {
    val isSyncing by viewModel.isSyncing.collectAsStateWithLifecycle()
    val isOnline by viewModel.isOnline.collectAsStateWithLifecycle()
    val uiState by viewModel.conversionUiState.collectAsStateWithLifecycle()
    val amount by viewModel.amountStringFlow.collectAsStateWithLifecycle()
    ConversionScreen(
        isOnline = isOnline,
        isSyncing = isSyncing,
        amount = amount,
        onAmountChange = { viewModel.onAmountChange(it) },
        selectedCurrency = uiState.selectedCurrency,
        onCurrencySelected = viewModel::onCurrencySelect,
        currencyList = uiState.currencies,
        convertedCurrencies = uiState.convertedCurrencies
    )
}

@Composable
private fun ConversionScreen(
    isOnline: Boolean = false,
    isSyncing: Boolean = false,
    errorMessage: String? = null,
    amount: String,
    onAmountChange: (String) -> Unit,
    selectedCurrency: Currency?,
    onCurrencySelected: (Currency) -> Unit,
    currencyList: List<Currency>,
    convertedCurrencies: List<ConvertedCurrency>,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(16.dp)
    ) {
        if (isSyncing) {
            InfoCard(
                modifier = Modifier
                    .testTag("syncing")
                    .fillMaxWidth(),
                text = stringResource(R.string.feature_conversion_syncing),
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        if (!isOnline) {
            InfoCard(
                modifier = Modifier
                    .testTag("noNetwork")
                    .fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                ),
                text = stringResource(R.string.feature_conversion_no_internet)
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        if (errorMessage != null) {
            InfoCard(
                modifier = Modifier
                    .testTag("error")
                    .fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                ),
                text = errorMessage
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        OutlinedTextField(
            value = amount,
            onValueChange = onAmountChange,
            label = { Text(stringResource(R.string.feature_conversion_enter_amount)) },
            modifier = Modifier
                .testTag("amountField")
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
        )

        var expanded by remember { mutableStateOf(false) }

        Box(
            modifier = Modifier
                .testTag("currencyDropdown")
                .fillMaxWidth()
                .padding(bottom = 16.dp)
                .border(1.dp, Color.Gray, RoundedCornerShape(8.dp))
                .clickable { expanded = true }
                .padding(12.dp)
        ) {
            Text(
                text = selectedCurrency?.name
                    ?: stringResource(R.string.feature_conversion_select_currency),
                modifier = Modifier.fillMaxWidth()
            )

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                currencyList.forEach { currency ->
                    DropdownMenuItem(
                        text = { Text(text = currency.name) },
                        onClick = {
                            onCurrencySelected(currency)
                            expanded = false
                        }
                    )
                }
            }
        }
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.fillMaxSize(),
            contentPadding = WindowInsets.navigationBars.asPaddingValues(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(convertedCurrencies) { convertedCurrency ->
                CurrencyItem(
                    modifier = Modifier
                        .testTag("currencyItem${convertedCurrency.to.name}")
                        .fillMaxWidth(),
                    convertedCurrency
                )
            }
        }
    }
}

@Composable
private fun InfoCard(
    modifier: Modifier = Modifier,
    colors: CardColors = CardDefaults.cardColors(),
    text: String
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        colors = colors
    ) {
        Text(
            modifier = Modifier
                .padding(16.dp),
            text = text,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun CurrencyItem(
    modifier: Modifier = Modifier,
    currency: ConvertedCurrency
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = String.format(Locale.getDefault(), "%.2f", currency.value),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = currency.to.name,
                style = MaterialTheme.typography.bodyLarge,
                minLines = 2,
                maxLines = 2,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewCurrencyConverterScreen() {
    val dummyCurrencies = listOf(
        Currency("USD", "US Dollar"),
        Currency("EUR", "Euro"),
        Currency("INR", "Indian Rupee"),
    )

    val dummyConvertedCurrencies = listOf(
        ConvertedCurrency(dummyCurrencies[0], dummyCurrencies[1], 0.85),
        ConvertedCurrency(dummyCurrencies[0], dummyCurrencies[2], 74.3),
        ConvertedCurrency(dummyCurrencies[1], dummyCurrencies[2], 87.6),
    )

    CurrencyConvertorTheme {
        Surface(
            color = MaterialTheme.colorScheme.background
        ) {
            ConversionScreen(
                isSyncing = true,
                isOnline = false,
                errorMessage = "Something went wrong!",
                amount = "100",
                onAmountChange = {},
                selectedCurrency = dummyCurrencies[0],
                onCurrencySelected = {},
                currencyList = dummyCurrencies,
                convertedCurrencies = dummyConvertedCurrencies
            )
        }
    }
}
