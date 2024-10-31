package com.example.currencyconvertor.core.datastore.test

import androidx.datastore.core.DataStore
import com.example.currencyconvertor.core.datastore.PreferencesProtoModel
import com.example.currencyconvertor.core.datastore.PreferencesSerializer
import com.example.currencyconvertor.core.datastore.di.DataStoreModule
import com.example.currencyconvertor.core.common.network.di.ApplicationScope
import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import kotlinx.coroutines.CoroutineScope
import javax.inject.Singleton

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [DataStoreModule::class],
)
internal object TestDataStoreModule {

    @Provides
    @Singleton
    fun providesUserPreferencesDataStore(
        @ApplicationScope scope: CoroutineScope,
        preferencesSerializer: PreferencesSerializer,
    ): DataStore<PreferencesProtoModel> {
        return testPreferencesDataStore(
            serializer = preferencesSerializer,
        )
    }
}

fun testPreferencesDataStore(
    serializer: PreferencesSerializer = PreferencesSerializer(),
): DataStore<PreferencesProtoModel> = InMemoryDataStore(serializer.defaultValue)