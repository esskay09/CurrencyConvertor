package com.example.currencyconvertor.core.database.model

import androidx.room.Embedded
import androidx.room.Relation

data class CurrencyWithConversionRates(
    @Embedded
    val base: CurrencyEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "base_currency_id"
    )
    val rates: List<ConversionRateEntity>,
)