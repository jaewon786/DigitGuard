package com.digitguard.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.digitguard.app.data.local.entity.LocalThreatPattern

@Dao
interface ThreatPatternDao {
    @Query("SELECT * FROM local_threat_patterns WHERE threatLevel != 'none'")
    suspend fun getActivePatterns(): List<LocalThreatPattern>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(patterns: List<LocalThreatPattern>)

    @Query("DELETE FROM local_threat_patterns")
    suspend fun deleteAll()
}
