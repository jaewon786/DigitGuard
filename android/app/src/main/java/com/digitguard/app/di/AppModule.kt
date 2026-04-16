package com.digitguard.app.di

import android.content.Context
import androidx.room.Room
import com.digitguard.app.data.local.AppDatabase
import com.digitguard.app.data.local.dao.ThreatPatternDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "digitguard_db",
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun provideThreatPatternDao(db: AppDatabase): ThreatPatternDao {
        return db.threatPatternDao()
    }
}
