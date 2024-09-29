package com.example.currencyconvertor.core.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.currencyconvertor.core.model.Currency

@Entity(tableName = "currency")
data class CurrencyEntity(
    @PrimaryKey
    val id: String,
    val name: String
)

fun CurrencyEntity.asExternalModel() = Currency(
    id = id,
    name = name
)