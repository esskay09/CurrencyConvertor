package com.example.currencyconvertor.core.data.di

import com.example.currencyconvertor.core.data.repository.CurrenciesRepository
import com.example.currencyconvertor.core.data.repository.DefaultCurrenciesRepository
import com.example.currencyconvertor.core.data.util.ConnectivityManagerNetworkMonitor
import com.example.currencyconvertor.core.data.util.NetworkMonitor
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {
    @Binds
    internal abstract fun bindsCurrenciesRepository(
        currenciesRepository: DefaultCurrenciesRepository,
    ): CurrenciesRepository

    @Binds
    internal abstract fun bindsNetworkMonitor(
        networkMonitor: ConnectivityManagerNetworkMonitor,
    ): NetworkMonitor
}
