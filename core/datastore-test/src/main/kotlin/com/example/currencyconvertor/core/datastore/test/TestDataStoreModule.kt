package com.example.currencyconvertor.core.datastore.test

import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import com.example.currencyconvertor.core.datastore.PreferencesProtoModel
import com.example.currencyconvertor.core.datastore.PreferencesSerializer
import com.example.currencyconvertor.core.datastore.di.DataStoreModule
import com.example.currencyconvertor.core.network.di.ApplicationScope
import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import kotlinx.coroutines.CoroutineScope
import org.junit.rules.TemporaryFolder
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
        userPreferencesSerializer: PreferencesSerializer,
        tmpFolder: TemporaryFolder,
    ): DataStore<PreferencesProtoModel> {
        return tmpFolder.testFetchTimesDataStore(
            coroutineScope = scope,
            preferencesSerializer = userPreferencesSerializer,
        )
    }
}

fun TemporaryFolder.testFetchTimesDataStore(
    coroutineScope: CoroutineScope,
    preferencesSerializer: PreferencesSerializer = PreferencesSerializer(),
) = DataStoreFactory.create(
    serializer = preferencesSerializer,
    scope = coroutineScope,
) {
    newFile("preferences_test.pb")
}
