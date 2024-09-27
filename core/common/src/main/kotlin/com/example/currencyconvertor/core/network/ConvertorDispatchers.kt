package com.example.currencyconvertor.core.network

import javax.inject.Qualifier
import kotlin.annotation.AnnotationRetention.RUNTIME

@Qualifier
@Retention(RUNTIME)
annotation class Dispatcher(val convertorDispatcher: ConvertorDispatchers)

enum class ConvertorDispatchers {
    Default,
    IO,
}
