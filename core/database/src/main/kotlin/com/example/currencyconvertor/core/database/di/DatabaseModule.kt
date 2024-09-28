package com.example.currencyconvertor.core.database.di

import android.content.Context
import androidx.room.Room
import com.example.currencyconvertor.core.database.CurrencyDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object DatabaseModule {

    @Provides
    @Singleton
    fun providesCurrencyDatabase(
        @ApplicationContext context: Context,
    ): CurrencyDatabase = Room.databaseBuilder(
        context,
        CurrencyDatabase::class.java,
        "currency-database",
    ).build()

}
