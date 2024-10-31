package com.example.currencyconvertor.core.testing.di

import com.example.currencyconvertor.core.common.network.ConvertorDispatchers.Default
import com.example.currencyconvertor.core.common.network.ConvertorDispatchers.IO
import com.example.currencyconvertor.core.common.network.Dispatcher
import com.example.currencyconvertor.core.common.network.di.DispatchersModule
import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.test.TestDispatcher

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [DispatchersModule::class],
)
internal object TestDispatchersModule {
    @Provides
    @Dispatcher(IO)
    fun providesIODispatcher(testDispatcher: TestDispatcher): CoroutineDispatcher = testDispatcher

    @Provides
    @Dispatcher(Default)
    fun providesDefaultDispatcher(
        testDispatcher: TestDispatcher,
    ): CoroutineDispatcher = testDispatcher
}
