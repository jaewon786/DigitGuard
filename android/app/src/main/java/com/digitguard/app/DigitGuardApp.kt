package com.digitguard.app

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.digitguard.app.data.local.PatternSeeder
import com.digitguard.app.service.PatternSyncWorker
import com.digitguard.app.service.ThreatLogSyncWorker
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
class DigitGuardApp : Application(), Configuration.Provider {

    @Inject
    lateinit var patternSeeder: PatternSeeder

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    private val appScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    override fun onCreate() {
        super.onCreate()

        // 로컬 위험 패턴 시딩
        appScope.launch {
            patternSeeder.seedIfEmpty()
        }

        // 주기적 패턴 동기화 (6시간마다)
        PatternSyncWorker.schedule(this)

        // 오프라인 위협 로그 동기화 (1시간마다)
        ThreatLogSyncWorker.schedule(this)
    }
}
