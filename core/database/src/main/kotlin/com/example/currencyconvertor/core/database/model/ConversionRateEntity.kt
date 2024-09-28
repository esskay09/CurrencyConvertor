package com.example.currencyconvertor.core.database.model

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "conversion_rate")
data class ConversionRateEntity(
    @PrimaryKey
    @ColumnInfo(name = "conversion_rate_id")
    val id: String,
    @Embedded(prefix = "target_")
    val target: CurrencyEntity,
    @ColumnInfo(name = "base_currency_id")
    val baseCurrencyId: String,
    val rate: Double,
)

