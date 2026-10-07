package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Indian Sign Language (ISL) Standard Datasets
 * Sourced from:
 * 1. Indian Sign Language Research and Training Centre (ISLRTC), Govt. of India (https://islrtc.nic.in/isl-dictionary/)
 * 2. NCERT / CIET ISL Educational Resources (https://ciet.ncert.gov.in/sign)
 */

enum class HandRequirement(val displayName: String) {
    ONE_HAND("Dominant Hand (1-Hand)"),
    TWO_HANDS("Both Hands (2-Hand)")
}

enum class MotionDirection(val label: String) {
    NONE("Hold Steady"),
    UPWARD("Move Upward ↑"),
    DOWNWARD("Move Downward ↓"),
    OUTWARD("Extend Outward ↗"),
    INWARD("Bring Toward Body ↙"),
    CIRCULAR_CLOCKWISE("Circular Motion ↻"),
    JOIN_TOGETHER("Bring Palms Together →|←"),
    WAVE_SIDE_TO_SIDE("Wave Side-to-Side ↔"),
    TAP_CHIN("Tap Chin Outward ↗"),
    FORWARD_PUSH("Push Forward ➔")
}

enum class ISLHandshapeType {
    NAMASTE_JOINED_PALMS,
    FLAT_PALM_WAVE,
    CHIN_TOUCH_FORWARD,
    CHEST_CIRCLE_FLAT,
    FIST_THUMBS_UP,
    OPEN_CUP_TO_MOUTH,
    TWO_HANDS_ROOF_HOUSE,
    TWO_HANDS_BOOK_OPEN,
    PINCH_FINGERS_FOOD,
    INDEX_POINT_FORWARD,
    TWO_HANDS_FAMILY_CIRCLE,
    INDEX_PALM_CROSS_HELP,
    TWO_FINGERS_VICTORY_PEACE,
    INDEX_SHAKE_NO,
    PALM_HEART_FEELING,
    OPEN_FIVE_PALM_STOP,
    FINGER_COUNT_ONE,
    FINGER_COUNT_TWO,
    FINGER_COUNT_THREE,
    FINGER_COUNT_FOUR,
    FINGER_COUNT_FIVE,
    ISL_ALPHABET_POSE
}

data class SignStepDetail(
    val stepNumber: Int,
    val title: String,
    val instruction: String,
    val handshapeType: ISLHandshapeType,
    val motionDirection: MotionDirection,
    val motionDescription: String,
    val anatomicalFocus: String,
    val visualAidTip: String
)

data class ISLSignItem(
    val id: String,
    val term: String,
    val hindiEquivalent: String,
    val englishMeaning: String,
    val language: String = "ISL",
    val standard: String = "ISLRTC Standard (Govt. of India)",
    val category: String, // Greetings, Basics, Everyday, Emergency, Family, Food, Education, Numbers, Fingerspelling
    val difficulty: String, // Beginner, Intermediate
    val handsRequired: HandRequirement,
    val palmOrientation: String,
    val wristOrientation: String,
    val movementDirection: String,
    val description: String,
    val steps: List<SignStepDetail>,
    val contextOfUse: String,
    val sourceName: String = "Indian Sign Language Research and Training Centre (ISLRTC)",
    val sourceUrl: String = "https://islrtc.nic.in/isl-dictionary/",
    val secondarySource: String = "NCERT / CIET Indian Sign Language Educational Resource",
    val secondarySourceUrl: String = "https://ciet.ncert.gov.in/sign",
    val verified: Boolean = true,
    val signVariations: List<String> = emptyList()
)

@Entity(tableName = "sign_progress")
data class SignProgressEntity(
    @PrimaryKey val signId: String,
    val attempts: Int = 0,
    val successfulAttempts: Int = 0,
    val accuracyPercent: Float = 0f,
    val masteryScore: Int = 0, // 0 to 100 App Learning Mastery
    val completed: Boolean = false,
    val mastered: Boolean = false,
    val lastPracticedAt: Long = 0L,
    val totalPracticeTimeSec: Int = 0,
    val currentStep: Int = 1,
    val completedSteps: Int = 0
)

@Entity(tableName = "practice_sessions")
data class PracticeSessionEntity(
    @PrimaryKey val sessionId: String,
    val userId: String,
    val signId: String,
    val signTerm: String,
    val timestamp: Long = System.currentTimeMillis(),
    val attemptNumber: Int = 1,
    val detected: Boolean = true,
    val confidence: Float = 0.85f,
    val score: Int = 85,
    val durationSeconds: Int = 15,
    val completed: Boolean = true,
    val feedback: String = "Great hand position!"
)

// Backwards compatibility classes for letter & quiz references
data class SignLetter(
    val letter: Char,
    val handshapeName: String,
    val description: String,
    val memoryTip: String,
    val commonWord: String,
    val fingerPositions: List<String>
)

data class CommonSignPhrase(
    val id: String,
    val phrase: String,
    val category: String,
    val description: String,
    val gestureGuide: String,
    val iconEmoji: String,
    val contextOfUse: String
)

data class SignQuizQuestion(
    val targetLetterOrPhrase: String,
    val prompt: String,
    val options: List<String>,
    val correctIndex: Int,
    val explanation: String
)
