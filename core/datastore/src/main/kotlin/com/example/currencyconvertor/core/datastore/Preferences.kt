package com.example.currencyconvertor.core.datastore

data class Preferences(
    val timeStamps: TimeStamps = TimeStamps(),
) {
    data class TimeStamps(
        val currencies: Long = 0,
        val conversionRates: Long = 0,
    )
}
