package com.example.currencyconvertor.core.model

data class ExchangeRate(
    val baseId: String,
    val targetId: String,
    val rate: Double
)
