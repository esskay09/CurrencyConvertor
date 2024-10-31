package com.example.currencyconvertor.core.datastore.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.dataStoreFile
import com.example.currencyconvertor.core.datastore.PreferencesProtoModel
import com.example.currencyconvertor.core.datastore.PreferencesSerializer
import com.example.currencyconvertor.core.common.network.ConvertorDispatchers.IO
import com.example.currencyconvertor.core.common.network.Dispatcher
import com.example.currencyconvertor.core.common.network.di.ApplicationScope
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataStoreModule {

    @Provides
    @Singleton
    internal fun providesUserPreferencesDataStore(
        @ApplicationContext context: Context,
        @Dispatcher(IO) ioDispatcher: CoroutineDispatcher,
        @ApplicationScope scope: CoroutineScope,
        fetchTimeStampsSerializer: PreferencesSerializer,
    ): DataStore<PreferencesProtoModel> =
        DataStoreFactory.create(
            serializer = fetchTimeStampsSerializer,
            scope = CoroutineScope(scope.coroutineContext + ioDispatcher)
        ) {
            context.dataStoreFile("preferences.pb")
        }
}
