package com.digitguard.app.service

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.*
import com.digitguard.app.data.local.AppDatabase
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.util.concurrent.TimeUnit

@HiltWorker
class ThreatLogSyncWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val database: AppDatabase,
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            // TODO: 미동기화 로그를 서버로 배치 업로드
            // val unsyncedLogs = database.threatLogDao().getUnsynced()
            // threatApi.logThreats(unsyncedLogs)
            // database.threatLogDao().markAsSynced(unsyncedLogs.map { it.id })
            Result.success()
        } catch (e: Exception) {
            if (runAttemptCount < 3) Result.retry() else Result.failure()
        }
    }

    companion object {
        const val WORK_NAME = "threat_log_sync"

        fun schedule(context: Context) {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            val request = PeriodicWorkRequestBuilder<ThreatLogSyncWorker>(
                1, TimeUnit.HOURS,
                15, TimeUnit.MINUTES,
            )
                .setConstraints(constraints)
                .build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                request,
            )
        }
    }
}
