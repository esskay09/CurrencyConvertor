package com.example.currencyconvertor.feature.conversion

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.currencyconvertor.core.model.ConvertedCurrency
import com.example.currencyconvertor.core.model.Currency
import com.example.currencyconvertor.theme.CurrencyConvertorTheme

@Composable
fun ConversionScreen(
    viewModel: ConversionViewModel = hiltViewModel()
) {
    val uiState by viewModel.conversionUiState.collectAsStateWithLifecycle()
    val amount by viewModel.amountStringFlow.collectAsStateWithLifecycle()
    ConversionScreen(
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
            .systemBarsPadding()
            .padding(16.dp)
    ) {
        // Amount Input Field
        TextField(
            value = amount,
            onValueChange = onAmountChange,
            label = { Text("Enter amount") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
        )

        var expanded by remember { mutableStateOf(false) }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
                .border(1.dp, Color.Gray, RoundedCornerShape(8.dp))
                .clickable { expanded = true }
                .padding(12.dp)
        ) {
            Text(
                text = selectedCurrency?.name ?: "Select currency",
                modifier = Modifier.fillMaxWidth(),
                color = if (selectedCurrency != null) Color.Black else Color.Gray
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
            contentPadding = PaddingValues(8.dp)
        ) {
            items(convertedCurrencies) { convertedCurrency ->
                ConvertedCurrencyItem(convertedCurrency)
            }
        }

    }
}

@Composable
fun ConvertedCurrencyItem(convertedCurrency: ConvertedCurrency) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = convertedCurrency.to.name,
                style = MaterialTheme.typography.labelMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Value: ${convertedCurrency.value}",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold
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
        ConversionScreen(
            amount = "100",
            onAmountChange = {},
            selectedCurrency = dummyCurrencies[0],
            onCurrencySelected = {},
            currencyList = dummyCurrencies,
            convertedCurrencies = dummyConvertedCurrencies
        )
    }
    ConversionScreen(
        amount = "100",
        onAmountChange = {},
        selectedCurrency = dummyCurrencies[0],
        onCurrencySelected = {},
        currencyList = dummyCurrencies,
        convertedCurrencies = dummyConvertedCurrencies,
    )
}
