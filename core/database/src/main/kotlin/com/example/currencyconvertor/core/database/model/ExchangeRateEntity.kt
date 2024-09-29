package com.example.currencyconvertor.core.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.currencyconvertor.core.model.ExchangeRate

@Entity(tableName = "exchange_rate")
data class ExchangeRateEntity(
    @PrimaryKey
    @ColumnInfo(name = "conversion_rate_id")
    val id: String,
    @ColumnInfo(name = "target_currency_id")
    val targetCurrencyId: String,
    @ColumnInfo(name = "base_currency_id")
    val baseCurrencyId: String,
    val rate: Double,
)

fun ExchangeRateEntity.asExternalModel(): ExchangeRate {
    return ExchangeRate(
        baseId = baseCurrencyId,
        targetId = targetCurrencyId,
        rate = rate
    )
}
