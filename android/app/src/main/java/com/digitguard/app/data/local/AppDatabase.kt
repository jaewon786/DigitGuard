package com.digitguard.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.digitguard.app.data.local.dao.ThreatPatternDao
import com.digitguard.app.data.local.entity.LocalThreatLog
import com.digitguard.app.data.local.entity.LocalThreatPattern

@Database(
    entities = [LocalThreatPattern::class, LocalThreatLog::class],
    version = 1,
    exportSchema = false,
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun threatPatternDao(): ThreatPatternDao
}
