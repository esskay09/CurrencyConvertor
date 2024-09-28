package com.example.currencyconvertor.core.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "currency")
data class CurrencyEntity(
    @PrimaryKey
    val id: String,
    val name: String
)