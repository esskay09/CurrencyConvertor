package com.example.currencyconvertor.core.common.network

import javax.inject.Qualifier
import kotlin.annotation.AnnotationRetention.RUNTIME

@Qualifier
@Retention(RUNTIME)
annotation class Dispatcher(val convertorDispatcher: ConvertorDispatchers)

enum class ConvertorDispatchers {
    Default,
    IO,
}
