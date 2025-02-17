package com.example.currencyconvertor.core.datastore

data class Preferences(
    val selectedBaseCurrencyId: String = "",
    val timeStamps: TimeStamps = TimeStamps(),
) {
    data class TimeStamps(
        val currencies: Long = 0,
        val exchangeRates: Long = 0,
    )
}
