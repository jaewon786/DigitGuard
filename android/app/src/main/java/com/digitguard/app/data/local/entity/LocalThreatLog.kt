package com.digitguard.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "local_threat_logs")
data class LocalThreatLog(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val threatType: String,
    val detectedText: String,
    val sourcePackage: String?,
    val actionTaken: String,
    val isSynced: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
)
