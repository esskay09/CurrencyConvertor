package com.example.currencyconvertor.core.database.di

import com.example.currencyconvertor.core.database.CurrencyDatabase
import com.example.currencyconvertor.core.database.dao.CurrencyDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal object DaosModule {
    @Provides
    fun providesCurrenciesDao(
        database: CurrencyDatabase,
    ): CurrencyDao = database.currencyDao()

}
