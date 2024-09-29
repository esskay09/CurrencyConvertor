package com.example.currencyconvertor.core.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ExchangeRatesNetwork(
    val timestamp: Long,
    @SerialName("base") val baseCurrencyId: String,
    val rates: Map<String, Double>
)