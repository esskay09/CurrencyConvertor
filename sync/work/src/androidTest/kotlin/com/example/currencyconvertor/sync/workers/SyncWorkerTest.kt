package com.example.currencyconvertor.sync.workers

import android.util.Log
import androidx.test.platform.app.InstrumentationRegistry
import androidx.work.Configuration
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.testing.SynchronousExecutor
import androidx.work.testing.WorkManagerTestInitHelper
import com.example.currencyconvertor.sync.workers.SyncWorker
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals

@HiltAndroidTest
class SyncWorkerTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    private val context get() = InstrumentationRegistry.getInstrumentation().context

    @Before
    fun setup() {
        val config = Configuration.Builder()
            .setMinimumLoggingLevel(Log.DEBUG)
            .setExecutor(SynchronousExecutor())
            .build()

        WorkManagerTestInitHelper.initializeTestWorkManager(context, config)
    }

    @Test
    fun testSyncWork() {
        val request = SyncWorker.startUpSyncWork()

        val workManager = WorkManager.getInstance(context)
        val testDriver = WorkManagerTestInitHelper.getTestDriver(context)!!

        workManager.enqueue(request).result.get()

        val preRunWorkInfo = workManager.getWorkInfoById(request.id).get()

        assertEquals(WorkInfo.State.ENQUEUED, preRunWorkInfo.state)

        testDriver.setAllConstraintsMet(request.id)

        val postRequirementWorkInfo = workManager.getWorkInfoById(request.id).get()
        assertEquals(WorkInfo.State.RUNNING, postRequirementWorkInfo.state)
    }
}
