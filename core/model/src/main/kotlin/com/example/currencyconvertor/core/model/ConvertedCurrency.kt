package com.example.currencyconvertor.core.model

data class ConvertedCurrency(
    val from: Currency,
    val to: Currency,
    val value: Double
)
