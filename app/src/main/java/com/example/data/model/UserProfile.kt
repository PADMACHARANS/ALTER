package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserProfile(
    @PrimaryKey val id: Int = 1,
    val name: String = "Learner",
    val avatarEmoji: String = "🌟",
    val level: Int = 1,
    val currentXp: Int = 0,
    val streakDays: Int = 0,
    val totalLessonsCompleted: Int = 0,
    val badgesUnlocked: String = "Starter", // Comma-separated badges
    val lastActiveTimestamp: Long = System.currentTimeMillis(),
    val uid: String = "",
    val email: String = "",
    val fullName: String = "",
    val preferredLanguage: String = "English",
    val learningLevel: String = "Beginner",
    val guidelinesAccepted: Boolean = false,
    val onboardingCompleted: Boolean = false
)

@Entity(tableName = "lesson_progress")
data class LessonProgressEntity(
    @PrimaryKey(autoGenerate = true) val recordId: Long = 0,
    val lessonId: String,
    val category: String,
    val scorePercent: Int,
    val completedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "screener_history")
data class ScreenerHistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val totalScore: Int,
    val severityLevel: String,
    val breakdownJson: String,
    val completedAt: Long = System.currentTimeMillis()
)
