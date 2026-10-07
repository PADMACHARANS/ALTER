package com.example.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.model.LessonProgressEntity
import com.example.data.model.PracticeSessionEntity
import com.example.data.model.ScreenerHistoryEntity
import com.example.data.model.SignProgressEntity
import com.example.data.model.UserProfile

@Database(
    entities = [
        UserProfile::class,
        LessonProgressEntity::class,
        ScreenerHistoryEntity::class,
        SignProgressEntity::class,
        PracticeSessionEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "readease_database.db"
                ).fallbackToDestructiveMigration(dropAllTables = true).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
