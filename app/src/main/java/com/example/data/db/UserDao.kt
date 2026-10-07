package com.example.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.LessonProgressEntity
import com.example.data.model.PracticeSessionEntity
import com.example.data.model.ScreenerHistoryEntity
import com.example.data.model.SignProgressEntity
import com.example.data.model.UserProfile
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM user_profile WHERE id = 1 LIMIT 1")
    fun getUserProfile(): Flow<UserProfile?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveUserProfile(profile: UserProfile)

    @Query("SELECT * FROM lesson_progress ORDER BY completedAt DESC")
    fun getAllLessonProgress(): Flow<List<LessonProgressEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLessonProgress(progress: LessonProgressEntity)

    @Query("SELECT * FROM screener_history ORDER BY completedAt DESC LIMIT 1")
    fun getLatestScreener(): Flow<ScreenerHistoryEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertScreenerResult(result: ScreenerHistoryEntity)

    // --- Indian Sign Language Progress & Practice Sessions ---
    @Query("SELECT * FROM sign_progress")
    fun getAllSignProgress(): Flow<List<SignProgressEntity>>

    @Query("SELECT * FROM sign_progress WHERE signId = :signId LIMIT 1")
    fun getSignProgress(signId: String): Flow<SignProgressEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveSignProgress(progress: SignProgressEntity)

    @Query("SELECT * FROM practice_sessions ORDER BY timestamp DESC")
    fun getAllPracticeSessions(): Flow<List<PracticeSessionEntity>>

    @Query("SELECT * FROM practice_sessions WHERE signId = :signId ORDER BY timestamp DESC")
    fun getPracticeSessionsForSign(signId: String): Flow<List<PracticeSessionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPracticeSession(session: PracticeSessionEntity)
}
