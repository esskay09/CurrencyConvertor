package com.example.currencyconvertor.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.currencyconvertor.core.database.dao.CurrencyDao
import com.example.currencyconvertor.core.database.model.ExchangeRateEntity
import com.example.currencyconvertor.core.database.model.CurrencyEntity

@Database(
    entities = [
        CurrencyEntity::class,
        ExchangeRateEntity::class,
    ],
    version = 1,
    exportSchema = true,
)
internal abstract class CurrencyDatabase : RoomDatabase() {
    abstract fun currencyDao(): CurrencyDao
}
