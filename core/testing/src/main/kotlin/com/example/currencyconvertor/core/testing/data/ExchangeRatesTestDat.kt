package com.example.currencyconvertor.core.testing.data

import com.example.currencyconvertor.core.model.ExchangeRate

val exchangeRatesTestData : List<ExchangeRate> = listOf(
    ExchangeRate(
        baseId = "USD",
        targetId = "EUR",
        rate = 0.85
    ),
    ExchangeRate(
        baseId = "USD",
        targetId = "GBP",
        rate = 0.75
    ),
    ExchangeRate(
        baseId = "USD",
        targetId = "INR",
        rate = 75.0
    ),
    ExchangeRate(
        baseId = "USD",
        targetId = "JPY",
        rate = 110.0
    ),
    ExchangeRate(
        baseId = "USD",
        targetId = "CAD",
        rate = 1.25
    ),
    ExchangeRate(
        baseId = "USD",
        targetId = "AUD",
        rate = 1.3
    ),
    ExchangeRate(
        baseId = "USD",
        targetId = "CHF",
        rate = 0.9
    ),
    ExchangeRate(
        baseId = "USD",
        targetId = "CNY",
        rate = 6.5
    ),
    ExchangeRate(
        baseId = "USD",
        targetId = "INR",
        rate = 75.0
    ),
    ExchangeRate(
        baseId = "EUR",
        targetId = "USD",
        rate = 1.18
    ),
    ExchangeRate(
        baseId = "EUR",
        targetId = "GBP",
        rate = 0.88
    ),
    ExchangeRate(
        baseId = "EUR",
        targetId = "JPY",
        rate = 130.0
    ),
    ExchangeRate(
        baseId = "EUR",
        targetId = "CAD",
        rate = 1.48
    ),
    ExchangeRate(
        baseId = "USD",
        targetId = "USD",
        rate = 1.00
    ),
)