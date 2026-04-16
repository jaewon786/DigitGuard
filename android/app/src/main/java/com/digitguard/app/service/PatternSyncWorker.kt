package com.digitguard.app.service

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.*
import com.digitguard.app.data.local.dao.ThreatPatternDao
import com.digitguard.app.data.local.entity.LocalThreatPattern
import com.digitguard.app.data.remote.api.ThreatApi
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.util.concurrent.TimeUnit

@HiltWorker
class PatternSyncWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val threatApi: ThreatApi,
    private val threatPatternDao: ThreatPatternDao,
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            val response = threatApi.syncPatterns()
            val patterns = response.patterns.mapIndexed { index, dto ->
                LocalThreatPattern(
                    id = "server-$index",
                    pattern = dto.pattern,
                    patternType = "regex",
                    threatLevel = dto.level,
                    category = dto.category,
                    lastSyncedAt = System.currentTimeMillis(),
                )
            }
            if (patterns.isNotEmpty()) {
                // REPLACE 전략이므로 기존 패턴을 덮어쓰고, 삭제 후 삽입 사이 빈 상태 방지
                threatPatternDao.insertAll(patterns)
            }
            Result.success()
        } catch (e: Exception) {
            if (runAttemptCount < 3) Result.retry() else Result.failure()
        }
    }

    companion object {
        const val WORK_NAME = "pattern_sync"

        fun schedule(context: Context) {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            val request = PeriodicWorkRequestBuilder<PatternSyncWorker>(
                6, TimeUnit.HOURS,
                30, TimeUnit.MINUTES,
            )
                .setConstraints(constraints)
                .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 15, TimeUnit.MINUTES)
                .build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                request,
            )
        }
    }
}
