package com.example.courrencyconverter

import android.app.Application
import com.example.currencyconvertor.sync.initializers.Sync
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MainApplication: Application(){
    override fun onCreate() {
        super.onCreate()
        Sync.initialize(this)
    }
}