package com.example.data.repository

import com.example.data.db.UserDao
import com.example.data.model.AACCard
import com.example.data.model.AACCategory
import com.example.data.model.AccessibilityPreferences
import com.example.data.model.CommonSignPhrase
import com.example.data.model.DyslexiaStory
import com.example.data.model.ISLSignItem
import com.example.data.model.LessonProgressEntity
import com.example.data.model.LetterMnemonic
import com.example.data.model.PracticeSessionEntity
import com.example.data.model.ReadAlongItem
import com.example.data.model.ScreenerArea
import com.example.data.model.ScreenerHistoryEntity
import com.example.data.model.ScreeningQuestion
import com.example.data.model.ScreeningReport
import com.example.data.model.SignLetter
import com.example.data.model.SignProgressEntity
import com.example.data.model.SoundStepExercise
import com.example.data.model.SyllableWord
import com.example.data.model.UserProfile
import com.example.data.model.WordBuilderPuzzle
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.firstOrNull
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FieldValue
import org.json.JSONArray
import org.json.JSONObject

enum class OperationType(val value: String) {
    CREATE("create"),
    UPDATE("update"),
    DELETE("delete"),
    LIST("list"),
    GET("get"),
    WRITE("write"),
}

fun handleFirestoreError(exception: Exception, operationType: OperationType, path: String?): String {
    val auth = FirebaseAuth.getInstance()
    val currentUser = auth.currentUser

    val providerInfoList = currentUser?.providerData?.map { provider ->
        JSONObject().apply {
            put("providerId", provider.providerId)
            put("email", provider.email)
        }
    } ?: emptyList()

    val authInfoJson = JSONObject().apply {
        put("userId", currentUser?.uid)
        put("email", currentUser?.email)
        put("emailVerified", currentUser?.isEmailVerified)
        put("tenantId", currentUser?.tenantId)
        put("providerInfo", JSONArray(providerInfoList))
    }

    val errorInfoJson = JSONObject().apply {
        put("error", exception.message ?: exception.toString())
        put("operationType", operationType.value)
        put("path", path)
        put("authInfo", authInfoJson)
    }

    val jsonString = errorInfoJson.toString()
    Log.e("FirestoreError", "Firestore Error: $jsonString")
    return jsonString
}

class ReadEaseRepository(
    private val userDao: UserDao,
    private val context: android.content.Context
) {

    private val db: FirebaseFirestore? by lazy {
        try {
            val dbId = context.applicationContext.getString(com.example.R.string.firestore_database_id)
            if (dbId.isNotBlank()) {
                FirebaseFirestore.getInstance(dbId)
            } else {
                FirebaseFirestore.getInstance()
            }
        } catch (e: Exception) {
            Log.w("ReadEaseRepository", "Firestore not initialized: ${e.message}")
            null
        }
    }

    private val auth: FirebaseAuth? by lazy {
        try {
            FirebaseAuth.getInstance()
        } catch (e: Exception) {
            Log.w("ReadEaseRepository", "FirebaseAuth not initialized: ${e.message}")
            null
        }
    }

    // Reactive accessibility preferences state
    private val _accessibilityPreferences = MutableStateFlow(AccessibilityPreferences())
    val accessibilityPreferences = _accessibilityPreferences.asStateFlow()

    fun updateAccessibilityPreferences(prefs: AccessibilityPreferences) {
        _accessibilityPreferences.value = prefs
    }

    // User profile from Room DB
    val userProfile: Flow<UserProfile> = userDao.getUserProfile().map { dbProfile ->
        dbProfile ?: UserProfile()
    }

    suspend fun saveUserProfile(profile: UserProfile) {
        userDao.saveUserProfile(profile)
        
        // Sync to Firestore
        val firebaseUser = auth?.currentUser
        val firestoreDb = db
        if (firebaseUser != null && firestoreDb != null) {
            val uid = firebaseUser.uid
            val userRef = firestoreDb.collection("users").document(uid)
            val docData = mapOf(
                "uid" to uid,
                "email" to (firebaseUser.email ?: profile.email.ifBlank { "learner@example.com" }),
                "displayName" to profile.name,
                "fullName" to profile.fullName,
                "preferredLanguage" to profile.preferredLanguage,
                "learningLevel" to profile.learningLevel,
                "guidelinesAccepted" to profile.guidelinesAccepted,
                "createdAt" to com.google.firebase.Timestamp(java.util.Date(profile.lastActiveTimestamp)),
                "updatedAt" to FieldValue.serverTimestamp(),
                "progress" to mapOf(
                    "level" to profile.level,
                    "currentXp" to profile.currentXp,
                    "streakDays" to profile.streakDays,
                    "totalLessonsCompleted" to profile.totalLessonsCompleted,
                    "badgesUnlocked" to profile.badgesUnlocked,
                    "onboardingCompleted" to profile.onboardingCompleted
                )
            )
            try {
                userRef.set(docData).addOnFailureListener { exception ->
                    handleFirestoreError(exception, OperationType.WRITE, userRef.path)
                }
            } catch (e: Exception) {
                Log.e("ReadEaseRepository", "Firestore set profile failed", e)
            }
        }
    }

    suspend fun addXpAndStreak(xpToAdd: Int) {
        val current = userDao.getUserProfile().firstOrNull() ?: UserProfile()
        val newXp = current.currentXp + xpToAdd
        val newLevel = 1 + (newXp / 200)
        val updated = current.copy(
            level = newLevel,
            currentXp = newXp,
            totalLessonsCompleted = current.totalLessonsCompleted + 1,
            lastActiveTimestamp = System.currentTimeMillis()
        )
        saveUserProfile(updated)
    }

    suspend fun recordLessonCompletion(lessonId: String, category: String, score: Int) {
        userDao.insertLessonProgress(
            LessonProgressEntity(
                lessonId = lessonId,
                category = category,
                scorePercent = score
            )
        )
        // Sync progress in Firestore
        val firebaseUser = auth?.currentUser
        val firestoreDb = db
        if (firebaseUser != null && firestoreDb != null) {
            val uid = firebaseUser.uid
            val progressRef = firestoreDb.collection("users").document(uid).collection("progress").document(lessonId)
            val data = mapOf(
                "lessonId" to lessonId,
                "category" to category,
                "scorePercent" to score,
                "completedAt" to FieldValue.serverTimestamp()
            )
            try {
                progressRef.set(data).addOnFailureListener { exception ->
                    handleFirestoreError(exception, OperationType.WRITE, progressRef.path)
                }
            } catch (e: Exception) {
                Log.e("ReadEaseRepository", "Firestore progress failed", e)
            }
        }
    }

    suspend fun saveScreeningResult(report: ScreeningReport) {
        userDao.insertScreenerResult(
            ScreenerHistoryEntity(
                totalScore = report.totalScore,
                severityLevel = report.severityLevel,
                breakdownJson = "Score: ${report.totalScore}/${report.maxScore}"
            )
        )
        // Sync screening in Firestore
        val firebaseUser = auth?.currentUser
        val firestoreDb = db
        if (firebaseUser != null && firestoreDb != null) {
            val uid = firebaseUser.uid
            val screenerRef = firestoreDb.collection("users").document(uid).collection("screener").document("latest")
            val data = mapOf(
                "totalScore" to report.totalScore,
                "maxScore" to report.maxScore,
                "percentage" to report.percentage,
                "severityLevel" to report.severityLevel,
                "insights" to report.personalizedInsights,
                "recommendedTools" to report.recommendedTools,
                "completedAt" to FieldValue.serverTimestamp()
            )
            try {
                screenerRef.set(data).addOnFailureListener { exception ->
                    handleFirestoreError(exception, OperationType.WRITE, screenerRef.path)
                }
            } catch (e: Exception) {
                Log.e("ReadEaseRepository", "Firestore screening failed", e)
            }
        }
    }

    // --- Indian Sign Language (ISL) Datasets & Persistence ---
    val islSigns: List<ISLSignItem> = ISLSignDictionary.allISLSigns
    val allSignProgress: Flow<List<SignProgressEntity>> = userDao.getAllSignProgress()
    val allPracticeSessions: Flow<List<PracticeSessionEntity>> = userDao.getAllPracticeSessions()

    fun getSignProgress(signId: String): Flow<SignProgressEntity?> = userDao.getSignProgress(signId)

    suspend fun saveSignProgress(progress: SignProgressEntity) {
        userDao.saveSignProgress(progress)
        val firebaseUser = auth?.currentUser
        val firestoreDb = db
        if (firebaseUser != null && firestoreDb != null) {
            val uid = firebaseUser.uid
            val progressDoc = firestoreDb.collection("users").document(uid).collection("isl_progress").document(progress.signId)
            val docData = mapOf(
                "signId" to progress.signId,
                "attempts" to progress.attempts,
                "successfulAttempts" to progress.successfulAttempts,
                "accuracyPercent" to progress.accuracyPercent,
                "masteryScore" to progress.masteryScore,
                "completed" to progress.completed,
                "mastered" to progress.mastered,
                "lastPracticedAt" to FieldValue.serverTimestamp(),
                "currentStep" to progress.currentStep,
                "completedSteps" to progress.completedSteps
            )
            try {
                progressDoc.set(docData).addOnFailureListener { e ->
                    Log.e("ReadEaseRepository", "Firestore ISL progress save failed", e)
                }
            } catch (e: Exception) {
                Log.e("ReadEaseRepository", "Firestore error", e)
            }
        }
    }

    suspend fun recordSignPracticeAttempt(
        signId: String,
        score: Int,
        confidence: Float,
        feedback: String,
        currentStep: Int = 4
    ) {
        val currentProgress = userDao.getSignProgress(signId).firstOrNull() ?: SignProgressEntity(signId = signId)
        val newAttempts = currentProgress.attempts + 1
        val newSuccess = currentProgress.successfulAttempts + if (score >= 60) 1 else 0
        val newAccuracy = (newSuccess.toFloat() / newAttempts.toFloat()) * 100f
        val newMastery = (newAccuracy * 0.7f + (newSuccess * 5f).coerceAtMost(30f)).toInt().coerceIn(0, 100)
        val isCompleted = currentProgress.completed || score >= 60
        val isMastered = newMastery >= 90

        val updatedProgress = currentProgress.copy(
            attempts = newAttempts,
            successfulAttempts = newSuccess,
            accuracyPercent = newAccuracy,
            masteryScore = newMastery,
            completed = isCompleted,
            mastered = isMastered,
            lastPracticedAt = System.currentTimeMillis(),
            currentStep = currentStep,
            completedSteps = maxOf(currentProgress.completedSteps, currentStep)
        )
        saveSignProgress(updatedProgress)

        // Record practice session
        val sessionId = "session_${System.currentTimeMillis()}_${(100..999).random()}"
        val signItem = ISLSignDictionary.getSignById(signId)
        val term = signItem?.term ?: signId
        val session = PracticeSessionEntity(
            sessionId = sessionId,
            userId = auth?.currentUser?.uid ?: "local_user",
            signId = signId,
            signTerm = term,
            timestamp = System.currentTimeMillis(),
            attemptNumber = newAttempts,
            detected = true,
            confidence = confidence,
            score = score,
            durationSeconds = 15,
            completed = isCompleted,
            feedback = feedback
        )
        userDao.insertPracticeSession(session)

        // Award XP
        addXpAndStreak(if (score >= 60) 25 else 10)
    }

    // --- Curated Dyslexia Letter Mnemonics ---
    val letterMnemonics = listOf(
        LetterMnemonic(
            letter = 'b',
            counterpart = 'd',
            title = "Letter 'b' vs 'd' (The Bed Rule)",
            visualMnemonic = "🛏️ In the word 'bed', 'b' is the headboard and 'd' is the footboard!",
            hintRule = "'b' has a bat first, then a ball! Hold up your left hand thumbs up.",
            memoryTrick = "Make two fists with thumbs up. Left hand = b, Right hand = d. It spells 'bed'!",
            exampleWord = "bat, ball, bird, book",
            iconDescription = "Bat and ball"
        ),
        LetterMnemonic(
            letter = 'p',
            counterpart = 'q',
            title = "Letter 'p' vs 'q' (Pop & Queen)",
            visualMnemonic = "🎈 'p' pops to the right! 'q' bows to the Queen on the left.",
            hintRule = "'p' has its circle facing right like a parachute. 'q' points left.",
            memoryTrick = "Left hand thumb down = p. Right hand thumb down = q.",
            exampleWord = "pop, pen, puppy, plant",
            iconDescription = "Parachute and crown"
        ),
        LetterMnemonic(
            letter = 'm',
            counterpart = 'w',
            title = "Letter 'm' vs 'w' (Mountains & Water)",
            visualMnemonic = "⛰️ 'm' looks like two Mountain peaks. 'w' is like Water waves!",
            hintRule = "'m' reaches for the sky like mountains. 'w' dips down like water waves.",
            memoryTrick = "M = Mountain summits (up). W = Water troughs (down).",
            exampleWord = "moon, mountain, magic",
            iconDescription = "Mountains and waves"
        ),
        LetterMnemonic(
            letter = 'n',
            counterpart = 'u',
            title = "Letter 'n' vs 'u' (Nest & Umbrella)",
            visualMnemonic = "🪺 'n' arches over like a bird's Nest. 'u' catches rain like an Umbrella cup!",
            hintRule = "'n' makes a tunnel. 'u' holds liquid.",
            memoryTrick = "U can hold water in a cup!",
            exampleWord = "nest, night, umbrella",
            iconDescription = "Nest and cup"
        ),
        LetterMnemonic(
            letter = 's',
            counterpart = 'z',
            title = "Letter 's' vs 'z' (Snake & Zig-Zag)",
            visualMnemonic = "🐍 's' curves smoothly like a slithering Snake. 'z' has sharp Zig-Zag corners!",
            hintRule = "'s' is round and curvy without sharp corners. 'z' has straight zigzag lines.",
            memoryTrick = "Smooth curve = SSS Snake. Sharp zig-zag = ZZZ Zipper.",
            exampleWord = "sun, snake, star, smile",
            iconDescription = "Snake and zigzag"
        ),
        LetterMnemonic(
            letter = 'c',
            counterpart = 'k',
            title = "Letter 'c' vs 'k' (Cat & Kite)",
            visualMnemonic = "🐱 'c' curls like a Cat's tail. 'k' has kicking legs like flying a Kite!",
            hintRule = "'c' curls in a round half-circle. 'k' has a tall stick with two kicking arms.",
            memoryTrick = "C curls like a cat. K kicks like a karate kite.",
            exampleWord = "cat, cup, cake, clap",
            iconDescription = "Cat and kite"
        )
    )

    // --- Curated Syllables & Word Simplifications ---
    val syllableWords = listOf(
        SyllableWord(
            word = "butterfly",
            syllables = listOf("but", "ter", "fly"),
            phoneticSpelling = "/ˈbʌt.ər.flaɪ/",
            definition = "A colorful winged insect that flies from flower to flower.",
            category = "Nature",
            sampleSentence = "A yellow but-ter-fly landed softly on the flower."
        ),
        SyllableWord(
            word = "fantastic",
            syllables = listOf("fan", "tas", "tic"),
            phoneticSpelling = "/fænˈtæs.tɪk/",
            definition = "Extremely good, wonderful, or exciting.",
            category = "Feelings",
            sampleSentence = "You did a fan-tas-tic job reading today!"
        ),
        SyllableWord(
            word = "community",
            syllables = listOf("com", "mu", "ni", "ty"),
            phoneticSpelling = "/kəˈmjuː.nə.ti/",
            definition = "A group of people living together or sharing common interests.",
            category = "Social",
            sampleSentence = "Our com-mu-ni-ty library has wonderful books."
        ),
        SyllableWord(
            word = "understand",
            syllables = listOf("un", "der", "stand"),
            phoneticSpelling = "/ˌʌn.dərˈstænd/",
            definition = "To perceive the intended meaning of words or ideas.",
            category = "Thinking",
            sampleSentence = "Take your time so you un-der-stand each part."
        ),
        SyllableWord(
            word = "adventure",
            syllables = listOf("ad", "ven", "ture"),
            phoneticSpelling = "/ədˈven.tʃər/",
            definition = "An exciting or daring journey.",
            category = "Stories",
            sampleSentence = "Every book is a new ad-ven-ture waiting to be explored."
        )
    )

    // --- Curated Inclusive Dyslexia Stories ---
    val dyslexiaStories = listOf(
        DyslexiaStory(
            id = "story_1",
            title = "The Brave Little Fox",
            summary = "A friendly fox discovers that moving at his own pace helps him find hidden river treasures.",
            content = "Once upon a time, a small golden fox named Finn lived near the whispering woods. While the rabbits dashed quickly, Finn walked slowly, examining every pebble and mossy tree trunk. He noticed sparkles between the stones that the fast runners missed. Slow reading helps us see the magic in every single word.",
            simplifiedContent = "Finn is a golden fox. He lives in the woods.\n\nOther animals run fast.\nFinn walks slowly.\n\nFinn looks at every stone.\nHe finds shiny river treasures.\n\nReading slow is a superpower!",
            keywords = listOf("Finn", "Fox", "Woods", "Treasure", "Pebble"),
            category = "Confidence",
            readingLevel = "Level 1"
        ),
        DyslexiaStory(
            id = "story_2",
            title = "The Starlight Compass",
            summary = "Maya learns that stars look scattered until she connects them line by line.",
            content = "Maya looked up at the vast night sky. To her eyes, the stars danced like dancing letters on a page. Her grandmother gave her a wooden frame with a glass ruler. When Maya placed the ruler across the sky, she could track one constellation at a time without getting overwhelmed.",
            simplifiedContent = "Maya looks at the starry sky.\nThe stars dance like letters.\n\nHer grandma gives her a focus ruler.\nMaya looks at one star line.\n\nNow the path is clear and calm.",
            keywords = listOf("Maya", "Stars", "Compass", "Ruler", "Night"),
            category = "Techniques",
            readingLevel = "Level 2"
        ),
        DyslexiaStory(
            id = "story_3",
            title = "The Gentle Whale",
            summary = "Barnaby the blue whale speaks in deep, soothing musical hums across the ocean.",
            content = "Under the blue ocean, Barnaby sang melodies that traveled through miles of water. He didn't speak with rapid clicks; his sounds were deep, steady, and full of warmth. All the sea creatures gathered to listen to his calm rhythm. Clear and steady sounds make the biggest impact.",
            simplifiedContent = "Barnaby is a big blue whale.\nHe sings low, calm songs.\n\nHis songs travel far in the sea.\nFish gather to hear him.\n\nSteady words are strong words.",
            keywords = listOf("Whale", "Ocean", "Songs", "Rhythm", "Calm"),
            category = "Sensory",
            readingLevel = "Level 1"
        )
    )

    // --- Practice: Word Builder Puzzles ---
    val wordBuilderPuzzles = listOf(
        WordBuilderPuzzle(
            id = "wb_1",
            targetWord = "STAR",
            scrambledLetters = listOf("T", "S", "R", "A"),
            hint = "A glowing ball of light in the night sky ✨",
            category = "Phonics - Blends",
            phonemes = listOf("/s/", "/t/", "/ɑːr/"),
            explanation = "Blend the 'ST' sound with 'AR' to make STAR!"
        ),
        WordBuilderPuzzle(
            id = "wb_2",
            targetWord = "BOAT",
            scrambledLetters = listOf("A", "O", "B", "T"),
            hint = "A vessel that floats across water ⛵",
            category = "Phonics - Vowel Teams",
            phonemes = listOf("/b/", "/oʊ/", "/t/"),
            explanation = "'OA' work together to make the long 'O' sound in BOAT."
        ),
        WordBuilderPuzzle(
            id = "wb_3",
            targetWord = "FISH",
            scrambledLetters = listOf("H", "F", "I", "S"),
            hint = "An animal that swims with fins in the sea 🐟",
            category = "Phonics - Digraphs",
            phonemes = listOf("/f/", "/ɪ/", "/ʃ/"),
            explanation = "'SH' makes the quiet shhh sound at the end of FISH."
        ),
        WordBuilderPuzzle(
            id = "wb_4",
            targetWord = "CAMP",
            scrambledLetters = listOf("M", "C", "P", "A"),
            hint = "Sleeping in a tent outdoors under trees ⛺",
            category = "Phonics - End Blends",
            phonemes = listOf("/k/", "/æ/", "/m/", "/p/"),
            explanation = "'MP' blend smoothly at the end of CAMP."
        )
    )

    // --- Practice: Sound Steps (Phonemic Blending) ---
    val soundStepsExercises = listOf(
        SoundStepExercise(
            id = "ss_1",
            fullWord = "SUN",
            soundSegments = listOf("/s/", "/ʌ/", "/n/"),
            stepDescriptions = listOf("Hissing sound 's'", "Short vowel 'uh'", "Nasal sound 'nnn'"),
            visualClue = "☀️"
        ),
        SoundStepExercise(
            id = "ss_2",
            fullWord = "CAT",
            soundSegments = listOf("/k/", "/æ/", "/t/"),
            stepDescriptions = listOf("Crisp 'c' sound", "Open mouth 'aah'", "Tapping 't'"),
            visualClue = "🐱"
        ),
        SoundStepExercise(
            id = "ss_3",
            fullWord = "SHIP",
            soundSegments = listOf("/ʃ/", "/ɪ/", "/p/"),
            stepDescriptions = listOf("Quiet 'sh' sound", "Short 'ih' vowel", "Popping 'p'"),
            visualClue = "🚢"
        ),
        SoundStepExercise(
            id = "ss_4",
            fullWord = "BIRD",
            soundSegments = listOf("/b/", "/ɜːr/", "/d/"),
            stepDescriptions = listOf("Bouncing 'b'", "R-controlled 'er'", "Tapping 'd'"),
            visualClue = "🐦"
        )
    )

    // --- Practice: Read-Along Items ---
    val readAlongItems = listOf(
        ReadAlongItem(
            id = "ra_1",
            title = "A Sunny Day in the Garden",
            sentences = listOf(
                "The bright morning sun warms the green grass.",
                "A little red bird chirps high in the oak tree.",
                "Two fluffy rabbits hop across the stone path.",
                "Today is a calm and peaceful day to learn."
            ),
            wordTokens = listOf("The", "bright", "morning", "sun", "warms", "the", "green", "grass.", "A", "little", "red", "bird", "chirps", "high", "in", "the", "oak", "tree.", "Two", "fluffy", "rabbits", "hop", "across", "the", "stone", "path.", "Today", "is", "a", "calm", "and", "peaceful", "day", "to", "learn."),
            readingTip = "Follow along with the highlighted word. Tap any word to hear its pronunciation."
        )
    )

    // --- Sign Language Alphabet & Common Signs ---
    val signLetters = ('A'..'Z').map { letter ->
        when (letter) {
            'A' -> SignLetter(
                letter = 'A',
                handshapeName = "Fist with Thumb Resting at Side",
                description = "Make a gentle fist with your fingers curled in and your thumb resting straight against the index finger.",
                memoryTip = "Think of an Apple held in your fist.",
                commonWord = "Apple, All, Ask",
                fingerPositions = listOf("Fingers curled", "Thumb upright along side", "Palm facing outward")
            )
            'B' -> SignLetter(
                letter = 'B',
                handshapeName = "Flat Hand with Thumb Tucked",
                description = "Hold four fingers straight up touching together, with your thumb folded across your palm.",
                memoryTip = "Like a flat 'B' board or blue sky.",
                commonWord = "Book, Bird, Brave",
                fingerPositions = listOf("Four fingers upright", "Thumb across palm", "Palm facing forward")
            )
            'C' -> SignLetter(
                letter = 'C',
                handshapeName = "Curved Cup",
                description = "Curve all four fingers and your thumb to form the shape of the letter C.",
                memoryTip = "Shaped like a Coffee Cup.",
                commonWord = "Cat, Calm, Clean",
                fingerPositions = listOf("Arched fingers", "Arched thumb", "Side profile shows letter C")
            )
            'D' -> SignLetter(
                letter = 'D',
                handshapeName = "Index Up, Others in Loop",
                description = "Index finger points straight up while the other three fingers touch the thumb tip to make a circular loop.",
                memoryTip = "The tall index finger forms the stem of the 'd'.",
                commonWord = "Dog, Door, Day",
                fingerPositions = listOf("Index straight up", "Middle, ring & pinky touch thumb", "Creates a circle loop")
            )
            'E' -> SignLetter(
                letter = 'E',
                handshapeName = "Curled Claw on Thumb",
                description = "Curl your fingertips tightly down so they rest on top of your folded thumb.",
                memoryTip = "All fingers tucked together like an Egg.",
                commonWord = "Ear, Eye, Easy",
                fingerPositions = listOf("Fingertips bent", "Resting on folded thumb", "Palm facing out")
            )
            else -> SignLetter(
                letter = letter,
                handshapeName = "Letter $letter Handshape",
                description = "ASL fingerspelling representation for letter $letter.",
                memoryTip = "Sign letter $letter clearly with steady hand.",
                commonWord = "$letter word",
                fingerPositions = listOf("Distinct hand position for $letter", "Steady wrist", "Relaxed fingers")
            )
        }
    }

    val commonSignPhrases = listOf(
        CommonSignPhrase(
            id = "sign_hello",
            phrase = "Hello / Greeting",
            category = "Greetings",
            description = "Bring flat hand to temple and salute gently outward with a warm smile.",
            gestureGuide = "Touch temple with right flat palm, then move outward like a friendly salute.",
            iconEmoji = "👋",
            contextOfUse = "Saying hello to friends, teachers, and classmates."
        ),
        CommonSignPhrase(
            id = "sign_thankyou",
            phrase = "Thank You",
            category = "Greetings",
            description = "Touch your fingertips to your chin/lips, then extend your hand forward toward the person.",
            gestureGuide = "Hand touches chin flatly, moves out open-palmed.",
            iconEmoji = "🙏",
            contextOfUse = "Showing gratitude and politeness."
        ),
        CommonSignPhrase(
            id = "sign_please",
            phrase = "Please",
            category = "Greetings",
            description = "Rub flat open hand in small clockwise circles on your chest over your heart.",
            gestureGuide = "Flat hand circles gently on chest.",
            iconEmoji = "💖",
            contextOfUse = "Asking for something with kindness."
        ),
        CommonSignPhrase(
            id = "sign_help",
            phrase = "Help",
            category = "Emergency",
            description = "Place a 'thumbs up' fist onto your other flat palm, then lift both hands slightly upward together.",
            gestureGuide = "Fist with thumb up rests on flat open palm; lift upward together.",
            iconEmoji = "🆘",
            contextOfUse = "Requesting assistance when stuck or in need."
        ),
        CommonSignPhrase(
            id = "sign_water",
            phrase = "Water",
            category = "Everyday",
            description = "Make a 'W' handshape with index, middle, and ring fingers up, and tap index finger against your chin twice.",
            gestureGuide = "Tap 'W' fingers against chin twice gently.",
            iconEmoji = "💧",
            contextOfUse = "When you are thirsty or need a drink."
        ),
        CommonSignPhrase(
            id = "sign_love",
            phrase = "I Love You",
            category = "Emotions",
            description = "Raise thumb, index finger, and pinky finger while keeping middle and ring fingers down.",
            gestureGuide = "Combines letters 'I', 'L', and 'Y' in one universally recognized gesture.",
            iconEmoji = "🤟",
            contextOfUse = "Expressing love and friendship."
        ),
        CommonSignPhrase(
            id = "sign_yes",
            phrase = "Yes",
            category = "Everyday",
            description = "Make a fist and bob your wrist up and down like a nodding head.",
            gestureGuide = "S-hand fist nods up and down.",
            iconEmoji = "✅",
            contextOfUse = "Agreeing or confirming."
        ),
        CommonSignPhrase(
            id = "sign_no",
            phrase = "No",
            category = "Everyday",
            description = "Snap your index and middle fingers down onto your thumb like a quick mouth closing.",
            gestureGuide = "Index and middle finger touch thumb quickly.",
            iconEmoji = "❌",
            contextOfUse = "Declining or setting a boundary."
        )
    )

    // --- Silent Voice AAC (Augmentative Communication) ---
    val aacCards = listOf(
        // Urgent
        AACCard("aac_1", "I need Help", "I need help right now, please.", AACCategory.URGENT, "🆘", priority = true),
        AACCard("aac_2", "Bathroom", "I need to use the bathroom.", AACCategory.URGENT, "🚻", priority = true),
        AACCard("aac_3", "In Pain", "Something hurts and I feel pain.", AACCategory.URGENT, "🩹", priority = true),
        AACCard("aac_4", "Stop please", "Please stop that, I do not like it.", AACCategory.URGENT, "🛑", priority = true),

        // Basic Needs
        AACCard("aac_5", "Water", "May I have some water, please?", AACCategory.NEEDS, "💧"),
        AACCard("aac_6", "Hungry / Food", "I am hungry and would like something to eat.", AACCategory.NEEDS, "🥪"),
        AACCard("aac_7", "Rest / Break", "I need a quiet break to rest.", AACCategory.NEEDS, "🛋️"),
        AACCard("aac_8", "Too Loud", "It is too loud here. Can we lower the sound?", AACCategory.NEEDS, "🎧"),

        // Feelings
        AACCard("aac_9", "Happy", "I am feeling happy and good!", AACCategory.FEELINGS, "😊"),
        AACCard("aac_10", "Tired", "I am feeling very tired.", AACCategory.FEELINGS, "😴"),
        AACCard("aac_11", "Overwhelmed", "I feel overwhelmed and need time to breathe.", AACCategory.FEELINGS, "🌀"),
        AACCard("aac_12", "Excited", "I am excited about this!", AACCategory.FEELINGS, "🎉"),
        AACCard("aac_13", "Frustrated", "I am feeling frustrated with this right now.", AACCategory.FEELINGS, "😤"),

        // Responses
        AACCard("aac_14", "Yes", "Yes, I agree.", AACCategory.RESPONSES, "👍"),
        AACCard("aac_15", "No", "No, thank you.", AACCategory.RESPONSES, "👎"),
        AACCard("aac_16", "Thank you", "Thank you very much!", AACCategory.RESPONSES, "🙏"),
        AACCard("aac_17", "More time", "I need a little more time, please.", AACCategory.RESPONSES, "⏳"),
        AACCard("aac_18", "I don't know", "I am not sure or I do not know yet.", AACCategory.RESPONSES, "🤷")
    )

    // --- Dyslexia Screener Questions ---
    val screeningQuestions = listOf(
        ScreeningQuestion(
            id = 1,
            area = ScreenerArea.LETTER_REVERSAL,
            question = "Do letters like 'b' and 'd', or 'p' and 'q' look identical or flip around when you read them?",
            contextExample = "Example: Reading 'bog' as 'dog' or 'pat' as 'bat'.",
            promptDescription = "Assesses spatial letter-orientation confusion."
        ),
        ScreeningQuestion(
            id = 2,
            area = ScreenerArea.VISUAL_FATIGUE,
            question = "Do lines of text on a white page seem to crowd together, blur, or cause eye fatigue after a few minutes?",
            contextExample = "Example: Losing your place easily or needing your finger to track the line.",
            promptDescription = "Evaluates visual crowding and scotopic stress."
        ),
        ScreeningQuestion(
            id = 3,
            area = ScreenerArea.PHONOLOGICAL,
            question = "Is it tricky to break unfamiliar words into their separate sound chunks (phonemes)?",
            contextExample = "Example: Sounding out 's-t-r-a-p' sound by sound feels slow or tiring.",
            promptDescription = "Measures phonological decoding processing."
        ),
        ScreeningQuestion(
            id = 4,
            area = ScreenerArea.READING_FLUENCY,
            question = "Do you often have to re-read a sentence two or three times to catch its meaning?",
            contextExample = "Example: Reading all the words correctly, but your mind spent all its energy decoding.",
            promptDescription = "Assesses cognitive decoding load on working memory."
        ),
        ScreeningQuestion(
            id = 5,
            area = ScreenerArea.WORKING_MEMORY,
            question = "When given a multi-step spoken instruction (e.g., 'Open page 12, write question 3, then circle the verb'), do you forget the middle steps?",
            contextExample = "Example: Multi-step auditory instructions slip away quickly.",
            promptDescription = "Checks verbal sequential working memory."
        ),
        ScreeningQuestion(
            id = 6,
            area = ScreenerArea.LETTER_REVERSAL,
            question = "Do you sometimes write numbers or letters backward without noticing at first (like '3' as 'E' or '6' as '9')?",
            contextExample = "Example: Reversing digits when writing phone numbers or math problems.",
            promptDescription = "Evaluates directional graphomotor recall."
        ),
        ScreeningQuestion(
            id = 7,
            area = ScreenerArea.VISUAL_FATIGUE,
            question = "Does reading on a screen or bright white book give you headaches or make you want to look away?",
            contextExample = "Example: Feeling immediate relief when reading on tinted paper or with a ruler overlay.",
            promptDescription = "Assesses glare sensitivity and visual stress."
        ),
        ScreeningQuestion(
            id = 8,
            area = ScreenerArea.PHONOLOGICAL,
            question = "Do you find it easier to understand and remember information when it is spoken or read aloud to you?",
            contextExample = "Example: Audiobooks and spoken lectures feel much clearer than silent printed text.",
            promptDescription = "Evaluates the gap between listening comprehension and printed decoding."
        ),
        ScreeningQuestion(
            id = 9,
            area = ScreenerArea.WORKING_MEMORY,
            question = "Do you experience 'tip-of-the-tongue' word retrieval delays or substitute related words (e.g., saying 'chair' instead of 'desk')?",
            contextExample = "Example: Knowing exactly what you want to say, but retrieving the exact name feels slow under pressure.",
            promptDescription = "Assesses rapid automatized naming (RAN) and lexical retrieval speed."
        ),
        ScreeningQuestion(
            id = 10,
            area = ScreenerArea.PHONOLOGICAL,
            question = "When spelling irregular words, do you spell words phonetically as they sound rather than how they look (e.g., 'sed' for 'said', 'wot' for 'what')?",
            contextExample = "Example: Knowing the sounds, but visual orthographic memory of silent letters is inconsistent.",
            promptDescription = "Measures orthographic spelling memory vs phonetic reliance."
        )
    )

    fun getShuffledScreeningQuestions(): List<ScreeningQuestion> = screeningQuestions.shuffled()

    fun calculateScreeningReport(
        questions: List<ScreeningQuestion> = screeningQuestions,
        answers: Map<Int, Int>
    ): ScreeningReport {
        val total = answers.values.sum()
        val maxScore = (questions.size * 3).coerceAtLeast(1) // 0 to 3 scale (10 * 3 = 30 points max)
        val percentage = ((total.toFloat() / maxScore) * 100).coerceIn(0f, 100f).toInt()

        val severity = when {
            percentage >= 75 -> "High Indicators of Dyslexia (Rating: $percentage/100)"
            percentage >= 50 -> "Moderate Indicators of Dyslexia (Rating: $percentage/100)"
            percentage >= 25 -> "Mild Indicators (Rating: $percentage/100)"
            else -> "Typical Reading Profile (Rating: $percentage/100)"
        }

        val areaScores = mutableMapOf<ScreenerArea, Int>()
        for (q in questions) {
            val score = answers[q.id] ?: 0
            areaScores[q.area] = (areaScores[q.area] ?: 0) + score
        }

        val insights = mutableListOf<String>()
        val tools = mutableListOf<String>()

        if ((areaScores[ScreenerArea.LETTER_REVERSAL] ?: 0) >= 2) {
            insights.add("Frequent letter orientation confusion detected (e.g. b/d reversals).")
            tools.add("Phonics Mnemonics (Bed Rule & Hand Tricks)")
        }
        if ((areaScores[ScreenerArea.VISUAL_FATIGUE] ?: 0) >= 2) {
            insights.add("High visual crowding and screen glare sensitivity observed.")
            tools.add("Reading Ruler Overlay & Pastel Background Tints")
        }
        if ((areaScores[ScreenerArea.PHONOLOGICAL] ?: 0) >= 2) {
            insights.add("Phonemic segmentation takes extra cognitive energy.")
            tools.add("Sound Steps & Syllable Word Simplifier")
        }
        if ((areaScores[ScreenerArea.READING_FLUENCY] ?: 0) >= 2) {
            insights.add("Auditory reinforcement significantly boosts comprehension.")
            tools.add("Read-Along Audio Synchronization & Text-to-Speech")
        }

        if (tools.isEmpty()) {
            tools.add("Practice Hub Word Builder")
            tools.add("Dyslexia Reader with OpenDyslexic Font")
        }

        return ScreeningReport(
            totalScore = total,
            maxScore = maxScore,
            percentage = percentage,
            severityLevel = severity,
            areaBreakdown = areaScores,
            personalizedInsights = insights,
            recommendedTools = tools
        )
    }
}
