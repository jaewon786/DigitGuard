package com.digitguard.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "local_threat_patterns")
data class LocalThreatPattern(
    @PrimaryKey val id: String,
    val pattern: String,
    val patternType: String,
    val threatLevel: String,
    val category: String,
    val lastSyncedAt: Long,
)
