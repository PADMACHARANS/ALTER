package com.example.data.repository

import com.example.data.model.HandRequirement
import com.example.data.model.ISLHandshapeType
import com.example.data.model.ISLSignItem
import com.example.data.model.MotionDirection
import com.example.data.model.SignStepDetail

/**
 * Authoritative Indian Sign Language (ISL) Dataset
 * Primary Reference: Indian Sign Language Research and Training Centre (ISLRTC), Govt. of India (https://islrtc.nic.in/isl-dictionary/)
 * Educational Partner: NCERT CIET (https://ciet.ncert.gov.in/sign)
 */
object ISLSignDictionary {

    val allISLSigns: List<ISLSignItem> = listOf(
        // --- 1. GREETINGS & BASICS ---
        ISLSignItem(
            id = "isl_namaste",
            term = "NAMASTE (नमस्ते)",
            hindiEquivalent = "नमस्ते / प्रणाम",
            englishMeaning = "Traditional Indian Greeting / Hello",
            category = "Greetings",
            difficulty = "Beginner",
            handsRequired = HandRequirement.TWO_HANDS,
            palmOrientation = "Palms pressed flat together facing each other",
            wristOrientation = "Neutral upright at chest level",
            movementDirection = "Bring palms together smoothly with gentle head bow",
            description = "Join both flat palms in front of your chest in the traditional Anjali Mudra and bow slightly forward.",
            steps = listOf(
                SignStepDetail(
                    stepNumber = 1,
                    title = "Starting Position",
                    instruction = "Raise both open hands to chest level, palms facing each other approximately 15 cm apart.",
                    handshapeType = ISLHandshapeType.NAMASTE_JOINED_PALMS,
                    motionDirection = MotionDirection.NONE,
                    motionDescription = "Both hands upright at chest",
                    anatomicalFocus = "Relax shoulders, wrists straight",
                    visualAidTip = "Keep hands symmetrical at center"
                ),
                SignStepDetail(
                    stepNumber = 2,
                    title = "Align Fingers",
                    instruction = "Straighten all five fingers on both hands and keep fingers touching closely without gaps.",
                    handshapeType = ISLHandshapeType.NAMASTE_JOINED_PALMS,
                    motionDirection = MotionDirection.NONE,
                    motionDescription = "Fingers straight, thumbs pointing toward chest",
                    anatomicalFocus = "Finger extension without stiffness",
                    visualAidTip = "Fingertips aligned evenly"
                ),
                SignStepDetail(
                    stepNumber = 3,
                    title = "Join Palms Together",
                    instruction = "Bring both palms and fingertips firmly together at chest level in Anjali Mudra.",
                    handshapeType = ISLHandshapeType.NAMASTE_JOINED_PALMS,
                    motionDirection = MotionDirection.JOIN_TOGETHER,
                    motionDescription = "Palms touch flatly in center",
                    anatomicalFocus = "Palms pressed smoothly",
                    visualAidTip = "Gentle contact at sternum level"
                ),
                SignStepDetail(
                    stepNumber = 4,
                    title = "Complete Gesture",
                    instruction = "Hold the joined palms steady and offer a subtle, respectful nod of the head.",
                    handshapeType = ISLHandshapeType.NAMASTE_JOINED_PALMS,
                    motionDirection = MotionDirection.NONE,
                    motionDescription = "Hold posture with gentle nod",
                    anatomicalFocus = "Maintain steady hold for 1.5s",
                    visualAidTip = "Warm smile and respectful bow"
                )
            ),
            contextOfUse = "Respectful greeting to elders, teachers, guests, and peers across India.",
            signVariations = listOf("Standard ISLRTC 2-Hand Anjali Mudra", "One-hand slight forehead touch greeting")
        ),

        ISLSignItem(
            id = "isl_hello",
            term = "HELLO (नमस्ते / Hello)",
            hindiEquivalent = "नमस्ते / हैलो",
            englishMeaning = "Casual or Friendly Greeting",
            category = "Greetings",
            difficulty = "Beginner",
            handsRequired = HandRequirement.ONE_HAND,
            palmOrientation = "Palm facing outward toward listener",
            wristOrientation = "Upright at temple / shoulder height",
            movementDirection = "Friendly wave motion left to right",
            description = "Raise dominant flat open hand near temple and wave gently side-to-side with a warm smile.",
            steps = listOf(
                SignStepDetail(
                    stepNumber = 1,
                    title = "Raise Dominant Hand",
                    instruction = "Raise dominant hand to shoulder/temple level, palm facing outward toward the viewer.",
                    handshapeType = ISLHandshapeType.FLAT_PALM_WAVE,
                    motionDirection = MotionDirection.UPWARD,
                    motionDescription = "Dominant hand raised smoothly",
                    anatomicalFocus = "Elbow bent comfortably at 90 degrees",
                    visualAidTip = "Keep hand open and clearly visible"
                ),
                SignStepDetail(
                    stepNumber = 2,
                    title = "Open Palm & Extended Fingers",
                    instruction = "Spread all 5 fingers slightly in a natural, relaxed open palm orientation.",
                    handshapeType = ISLHandshapeType.FLAT_PALM_WAVE,
                    motionDirection = MotionDirection.NONE,
                    motionDescription = "All five fingers extended",
                    anatomicalFocus = "Wrist straight and relaxed",
                    visualAidTip = "Palm facing forward"
                ),
                SignStepDetail(
                    stepNumber = 3,
                    title = "Wave Side-to-Side",
                    instruction = "Wave your hand smoothly from left to right twice in a welcoming arc.",
                    handshapeType = ISLHandshapeType.FLAT_PALM_WAVE,
                    motionDirection = MotionDirection.WAVE_SIDE_TO_SIDE,
                    motionDescription = "Gentle rhythmic wave arc",
                    anatomicalFocus = "Wrist pivot motion",
                    visualAidTip = "Two clear side-to-side waves"
                ),
                SignStepDetail(
                    stepNumber = 4,
                    title = "Complete & Hold",
                    instruction = "Pause with an open hand and make direct eye contact with a welcoming expression.",
                    handshapeType = ISLHandshapeType.FLAT_PALM_WAVE,
                    motionDirection = MotionDirection.NONE,
                    motionDescription = "Hold friendly open hand",
                    anatomicalFocus = "Steady final posture",
                    visualAidTip = "Smiling facial expression"
                )
            ),
            contextOfUse = "Meeting friends, starting a conversation, waving goodbye.",
            signVariations = listOf("ISLRTC Standard open palm wave", "Temple-to-outward salute variation")
        ),

        ISLSignItem(
            id = "isl_thank_you",
            term = "THANK YOU (धन्यवाद)",
            hindiEquivalent = "धन्यवाद / शुक्रिया",
            englishMeaning = "Expressing Gratitude and Thanks",
            category = "Greetings",
            difficulty = "Beginner",
            handsRequired = HandRequirement.ONE_HAND,
            palmOrientation = "Palm facing toward chin/mouth, then moving outward",
            wristOrientation = "Flat hand resting near lower lip/chin",
            movementDirection = "Move forward and slightly upward toward the listener",
            description = "Touch flat fingertips to your chin/lips, then extend your hand outward and slightly upward toward the person.",
            steps = listOf(
                SignStepDetail(
                    stepNumber = 1,
                    title = "Position Hand at Chin",
                    instruction = "Bring dominant flat hand with fingers closed together to touch your chin with fingertips.",
                    handshapeType = ISLHandshapeType.CHIN_TOUCH_FORWARD,
                    motionDirection = MotionDirection.INWARD,
                    motionDescription = "Fingertips gently touch lower chin",
                    anatomicalFocus = "Flat palm facing inwards",
                    visualAidTip = "Start right below the lower lip"
                ),
                SignStepDetail(
                    stepNumber = 2,
                    title = "Keep Fingers Flat",
                    instruction = "Ensure all four fingers are pressed together smoothly with thumb resting alongside.",
                    handshapeType = ISLHandshapeType.CHIN_TOUCH_FORWARD,
                    motionDirection = MotionDirection.NONE,
                    motionDescription = "Flat hand shape maintained",
                    anatomicalFocus = "Fingers aligned flatly",
                    visualAidTip = "No bent knuckles"
                ),
                SignStepDetail(
                    stepNumber = 3,
                    title = "Extend Hand Outward",
                    instruction = "Move your flat hand smoothly forward and slightly upward toward the person you are thanking.",
                    handshapeType = ISLHandshapeType.CHIN_TOUCH_FORWARD,
                    motionDirection = MotionDirection.OUTWARD,
                    motionDescription = "Hand flows forward open-palmed",
                    anatomicalFocus = "Arm extends outward smoothly",
                    visualAidTip = "Motion directs gratitude to recipient"
                ),
                SignStepDetail(
                    stepNumber = 4,
                    title = "Final Gesture Hold",
                    instruction = "Hold your open palm facing slightly upward with a polite, appreciative facial expression.",
                    handshapeType = ISLHandshapeType.CHIN_TOUCH_FORWARD,
                    motionDirection = MotionDirection.NONE,
                    motionDescription = "Hold extended open hand",
                    anatomicalFocus = "Hold steady for 1.5s",
                    visualAidTip = "Nod with an appreciative expression"
                )
            ),
            contextOfUse = "Expressing gratitude, receiving assistance, being polite in daily interactions."
        ),

        ISLSignItem(
            id = "isl_please",
            term = "PLEASE (कृपया)",
            hindiEquivalent = "कृपया",
            englishMeaning = "Polite Request / Asking with Kindness",
            category = "Greetings",
            difficulty = "Beginner",
            handsRequired = HandRequirement.ONE_HAND,
            palmOrientation = "Flat open palm resting on chest / heart area",
            wristOrientation = "Relaxed on upper torso",
            movementDirection = "Small clockwise circular rubbing motion over chest",
            description = "Place your flat dominant palm over your heart/chest and rub in small gentle clockwise circles.",
            steps = listOf(
                SignStepDetail(
                    stepNumber = 1,
                    title = "Place Hand on Chest",
                    instruction = "Place your dominant open palm flat against the center of your chest near your heart.",
                    handshapeType = ISLHandshapeType.CHEST_CIRCLE_FLAT,
                    motionDirection = MotionDirection.NONE,
                    motionDescription = "Palm flat on chest",
                    anatomicalFocus = "Fingers together, flat against sternum",
                    visualAidTip = "Center over heart area"
                ),
                SignStepDetail(
                    stepNumber = 2,
                    title = "Fingers Together",
                    instruction = "Keep all 5 fingers flat and relaxed against your torso.",
                    handshapeType = ISLHandshapeType.CHEST_CIRCLE_FLAT,
                    motionDirection = MotionDirection.NONE,
                    motionDescription = "Smooth contact on shirt",
                    anatomicalFocus = "Relaxed wrist",
                    visualAidTip = "Do not curl fingers into a fist"
                ),
                SignStepDetail(
                    stepNumber = 3,
                    title = "Circular Motion",
                    instruction = "Move your palm in gentle clockwise circles twice without losing contact with your chest.",
                    handshapeType = ISLHandshapeType.CHEST_CIRCLE_FLAT,
                    motionDirection = MotionDirection.CIRCULAR_CLOCKWISE,
                    motionDescription = "Clockwise rotation on chest",
                    anatomicalFocus = "Shoulder and wrist circular rotation",
                    visualAidTip = "Smooth circular rubbing motion"
                ),
                SignStepDetail(
                    stepNumber = 4,
                    title = "Hold & Inquire",
                    instruction = "Pause with hand over chest and show a gentle, polite pleading facial expression.",
                    handshapeType = ISLHandshapeType.CHEST_CIRCLE_FLAT,
                    motionDirection = MotionDirection.NONE,
                    motionDescription = "Hold hand on heart",
                    anatomicalFocus = "Soft facial expression",
                    visualAidTip = "Slight head tilt with polite smile"
                )
            ),
            contextOfUse = "Making polite requests, asking for help, classroom manners."
        ),

        // --- 2. EMERGENCY & SAFETY ---
        ISLSignItem(
            id = "isl_help",
            term = "HELP (मदद)",
            hindiEquivalent = "मदद / सहायता",
            englishMeaning = "Requesting Assistance or Emergency Support",
            category = "Emergency",
            difficulty = "Beginner",
            handsRequired = HandRequirement.TWO_HANDS,
            palmOrientation = "Non-dominant flat palm facing upward; dominant hand thumbs-up fist on top",
            wristOrientation = "Both hands centered in front of torso",
            movementDirection = "Lift both hands upward together smoothly",
            description = "Place a 'thumbs up' fist of your dominant hand onto your other flat open palm, then lift both hands upward together.",
            steps = listOf(
                SignStepDetail(
                    stepNumber = 1,
                    title = "Base Palm Support",
                    instruction = "Extend non-dominant hand flat in front of your stomach, palm facing straight up.",
                    handshapeType = ISLHandshapeType.INDEX_PALM_CROSS_HELP,
                    motionDirection = MotionDirection.NONE,
                    motionDescription = "Non-dominant hand creates supportive base",
                    anatomicalFocus = "Flat horizontal palm",
                    visualAidTip = "Acts as the platform"
                ),
                SignStepDetail(
                    stepNumber = 2,
                    title = "Dominant Thumbs-Up Fist",
                    instruction = "Make a firm fist with dominant hand, thumb pointing straight up (A-bar / thumbs-up handshape).",
                    handshapeType = ISLHandshapeType.FIST_THUMBS_UP,
                    motionDirection = MotionDirection.NONE,
                    motionDescription = "Dominant hand forms thumbs-up",
                    anatomicalFocus = "Firm fist, upright thumb",
                    visualAidTip = "Thumb points directly skyward"
                ),
                SignStepDetail(
                    stepNumber = 3,
                    title = "Rest Fist on Base Palm",
                    instruction = "Rest the bottom of your fist directly onto the flat palm of your non-dominant hand.",
                    handshapeType = ISLHandshapeType.INDEX_PALM_CROSS_HELP,
                    motionDirection = MotionDirection.NONE,
                    motionDescription = "Fist sits on open palm",
                    anatomicalFocus = "Both hands in direct contact",
                    visualAidTip = "Clear visual contact between hands"
                ),
                SignStepDetail(
                    stepNumber = 4,
                    title = "Lift Upward Together",
                    instruction = "Lift both connected hands upward 10-15 cm together in a supportive lifting motion.",
                    handshapeType = ISLHandshapeType.INDEX_PALM_CROSS_HELP,
                    motionDirection = MotionDirection.UPWARD,
                    motionDescription = "Both hands rise in unison",
                    anatomicalFocus = "Arms lift smoothly together",
                    visualAidTip = "Clear lifting action symbolizing support"
                )
            ),
            contextOfUse = "Emergency requests, needing academic or physical assistance, distress signal."
        ),

        ISLSignItem(
            id = "isl_stop",
            term = "STOP (रुको / Stop)",
            hindiEquivalent = "रुकिए / बंद करो",
            englishMeaning = "Halt, Pause, or Cease Action",
            category = "Emergency",
            difficulty = "Beginner",
            handsRequired = HandRequirement.TWO_HANDS,
            palmOrientation = "Non-dominant flat palm up; dominant flat hand chops vertically into palm",
            wristOrientation = "Perpendicular hand contact at chest level",
            movementDirection = "Decisive downward chop onto base palm",
            description = "Hold non-dominant palm flat facing up, and bring the pinky edge of your dominant flat hand down sharply onto the palm.",
            steps = listOf(
                SignStepDetail(
                    stepNumber = 1,
                    title = "Horizontal Base Palm",
                    instruction = "Hold non-dominant hand flat horizontally at chest level, palm facing up.",
                    handshapeType = ISLHandshapeType.OPEN_FIVE_PALM_STOP,
                    motionDirection = MotionDirection.NONE,
                    motionDescription = "Flat base palm ready",
                    anatomicalFocus = "Steady non-dominant hand",
                    visualAidTip = "Open flat surface"
                ),
                SignStepDetail(
                    stepNumber = 2,
                    title = "Vertical Chopping Hand",
                    instruction = "Raise dominant flat hand vertically with fingers closed and thumb folded.",
                    handshapeType = ISLHandshapeType.OPEN_FIVE_PALM_STOP,
                    motionDirection = MotionDirection.UPWARD,
                    motionDescription = "Dominant hand held knife-edge",
                    anatomicalFocus = "Fingers straight, knife-edge orientation",
                    visualAidTip = "Edge of hand points downward"
                ),
                SignStepDetail(
                    stepNumber = 3,
                    title = "Decisive Contact",
                    instruction = "Bring the pinky edge of your dominant hand down firmly onto the center of your open palm.",
                    handshapeType = ISLHandshapeType.OPEN_FIVE_PALM_STOP,
                    motionDirection = MotionDirection.DOWNWARD,
                    motionDescription = "Sharp, firm chop onto palm",
                    anatomicalFocus = "Definitive stop impact",
                    visualAidTip = "Firm stopping gesture"
                ),
                SignStepDetail(
                    stepNumber = 4,
                    title = "Hold & Serious Expression",
                    instruction = "Hold both hands locked together with an alert, serious facial expression.",
                    handshapeType = ISLHandshapeType.OPEN_FIVE_PALM_STOP,
                    motionDirection = MotionDirection.NONE,
                    motionDescription = "Hold locked stop position",
                    anatomicalFocus = "Static hold",
                    visualAidTip = "Serious, alert face"
                )
            ),
            contextOfUse = "Safety halts, stopping dangerous activity, setting boundaries."
        ),

        // --- 3. EVERYDAY CONVERSATIONS ---
        ISLSignItem(
            id = "isl_water",
            term = "WATER (पानी)",
            hindiEquivalent = "पानी / जल",
            englishMeaning = "Water or Thirst",
            category = "Everyday",
            difficulty = "Beginner",
            handsRequired = HandRequirement.ONE_HAND,
            palmOrientation = "Cupped or W-shape hand near chin/mouth",
            wristOrientation = "Tilted near mouth",
            movementDirection = "Tap index finger/cupped thumb near corner of mouth twice",
            description = "Make a cupped hand or 'W' shape and tap the side of your mouth/chin twice gently, indicating drinking water.",
            steps = listOf(
                SignStepDetail(
                    stepNumber = 1,
                    title = "Cup Handshape",
                    instruction = "Form a gentle cup or three-finger 'W' handshape with dominant hand.",
                    handshapeType = ISLHandshapeType.OPEN_CUP_TO_MOUTH,
                    motionDirection = MotionDirection.NONE,
                    motionDescription = "Hand shaped like drinking vessel or W",
                    anatomicalFocus = "Curved fingers and thumb",
                    visualAidTip = "Natural drinking handshape"
                ),
                SignStepDetail(
                    stepNumber = 2,
                    title = "Bring to Mouth",
                    instruction = "Bring the thumb and index finger edge close to the corner of your lower lip.",
                    handshapeType = ISLHandshapeType.OPEN_CUP_TO_MOUTH,
                    motionDirection = MotionDirection.INWARD,
                    motionDescription = "Hand approaches mouth",
                    anatomicalFocus = "Wrist tilted upward slightly",
                    visualAidTip = "Close to chin / lip level"
                ),
                SignStepDetail(
                    stepNumber = 3,
                    title = "Tap Mouth Twice",
                    instruction = "Tap the corner of your lower lip twice with a gentle tilting drinking motion.",
                    handshapeType = ISLHandshapeType.OPEN_CUP_TO_MOUTH,
                    motionDirection = MotionDirection.TAP_CHIN,
                    motionDescription = "Two gentle taps at mouth",
                    anatomicalFocus = "Subtle wrist rotation",
                    visualAidTip = "Indicates drinking water"
                ),
                SignStepDetail(
                    stepNumber = 4,
                    title = "Complete Sign",
                    instruction = "Lower hand slightly and hold position.",
                    handshapeType = ISLHandshapeType.OPEN_CUP_TO_MOUTH,
                    motionDirection = MotionDirection.NONE,
                    motionDescription = "Hold ready hand",
                    anatomicalFocus = "Relaxed hand posture",
                    visualAidTip = "Recognized across ISL regions"
                )
            ),
            contextOfUse = "Asking for drinking water, meal times, hydration."
        ),

        ISLSignItem(
            id = "isl_food",
            term = "FOOD / EAT (खाना)",
            hindiEquivalent = "खाना / भोजन",
            englishMeaning = "Food, Meal, or Eating",
            category = "Food",
            difficulty = "Beginner",
            handsRequired = HandRequirement.ONE_HAND,
            palmOrientation = "Fingertips pinched together facing mouth",
            wristOrientation = "Tilted near lips",
            movementDirection = "Tap pinched fingertips toward mouth twice",
            description = "Pinch all five fingertips of your dominant hand together (morsel shape) and tap gently toward your mouth twice.",
            steps = listOf(
                SignStepDetail(
                    stepNumber = 1,
                    title = "Form Morsel Pinch",
                    instruction = "Bring all 5 fingertips together so they touch in a point (flattened O / morsel shape).",
                    handshapeType = ISLHandshapeType.PINCH_FINGERS_FOOD,
                    motionDirection = MotionDirection.NONE,
                    motionDescription = "All fingertips pinched together",
                    anatomicalFocus = "Thumb meets index, middle, ring, pinky",
                    visualAidTip = "Looks like holding a bite of food"
                ),
                SignStepDetail(
                    stepNumber = 2,
                    title = "Position Near Mouth",
                    instruction = "Raise pinched hand in front of your mouth with fingertips pointing inward.",
                    handshapeType = ISLHandshapeType.PINCH_FINGERS_FOOD,
                    motionDirection = MotionDirection.INWARD,
                    motionDescription = "Hand near lips",
                    anatomicalFocus = "Wrist angled at 45 degrees",
                    visualAidTip = "2 cm from lips"
                ),
                SignStepDetail(
                    stepNumber = 3,
                    title = "Tap Toward Mouth Twice",
                    instruction = "Tap your pinched fingertips toward your lips twice in a gentle eating motion.",
                    handshapeType = ISLHandshapeType.PINCH_FINGERS_FOOD,
                    motionDirection = MotionDirection.TAP_CHIN,
                    motionDescription = "Two light eating taps",
                    anatomicalFocus = "Smooth back-and-forth wrist pulse",
                    visualAidTip = "Universal food gesture in ISL"
                ),
                SignStepDetail(
                    stepNumber = 4,
                    title = "Hold & Complete",
                    instruction = "Hold hand near mouth for 1.5 seconds.",
                    handshapeType = ISLHandshapeType.PINCH_FINGERS_FOOD,
                    motionDirection = MotionDirection.NONE,
                    motionDescription = "Hold position",
                    anatomicalFocus = "Satisfied expression",
                    visualAidTip = "Clear morsel pinch"
                )
            ),
            contextOfUse = "Lunch time, expressing hunger, discussing meals."
        ),

        ISLSignItem(
            id = "isl_home",
            term = "HOME / HOUSE (घर)",
            hindiEquivalent = "घर / मकान",
            englishMeaning = "Home or House Building",
            category = "Everyday",
            difficulty = "Beginner",
            handsRequired = HandRequirement.TWO_HANDS,
            palmOrientation = "Palms angled facing each other forming a roof triangular apex",
            wristOrientation = "Tilted inward at 45 degrees",
            movementDirection = "Touch fingertips together to form a triangular roof",
            description = "Bring both flat hands together at a 45-degree angle so the fingertips touch, forming a triangular roof shape.",
            steps = listOf(
                SignStepDetail(
                    stepNumber = 1,
                    title = "Raise Both Flat Hands",
                    instruction = "Raise both hands to eye level, palms facing each other with fingers straight and closed.",
                    handshapeType = ISLHandshapeType.TWO_HANDS_ROOF_HOUSE,
                    motionDirection = MotionDirection.UPWARD,
                    motionDescription = "Both flat hands raised symmetrically",
                    anatomicalFocus = "Wrists straight and aligned",
                    visualAidTip = "Hands at eye level"
                ),
                SignStepDetail(
                    stepNumber = 2,
                    title = "Angle Hands at 45°",
                    instruction = "Angle both hands inward at 45 degrees toward each other.",
                    handshapeType = ISLHandshapeType.TWO_HANDS_ROOF_HOUSE,
                    motionDirection = MotionDirection.NONE,
                    motionDescription = "Diagonal hand alignment",
                    anatomicalFocus = "Symmetrical diagonal slant",
                    visualAidTip = "Creates slopes of a roof"
                ),
                SignStepDetail(
                    stepNumber = 3,
                    title = "Touch Fingertips Together",
                    instruction = "Touch the fingertips of both hands together at the top to form a clear triangular roof apex.",
                    handshapeType = ISLHandshapeType.TWO_HANDS_ROOF_HOUSE,
                    motionDirection = MotionDirection.JOIN_TOGETHER,
                    motionDescription = "Fingertips meet at center peak",
                    anatomicalFocus = "Fingertip contact at apex",
                    visualAidTip = "Sharp triangular roof peak"
                ),
                SignStepDetail(
                    stepNumber = 4,
                    title = "Hold Roof Structure",
                    instruction = "Hold the triangular roof shape steady for 1.5 seconds.",
                    handshapeType = ISLHandshapeType.TWO_HANDS_ROOF_HOUSE,
                    motionDirection = MotionDirection.NONE,
                    motionDescription = "Steady house shape",
                    anatomicalFocus = "Maintain roof geometry",
                    visualAidTip = "Clear representation of home"
                )
            ),
            contextOfUse = "Talking about home, going back after school, family residence."
        ),

        ISLSignItem(
            id = "isl_book",
            term = "BOOK (किताब / पुस्तक)",
            hindiEquivalent = "किताब / पुस्तक",
            englishMeaning = "Book or Reading Material",
            category = "Education",
            difficulty = "Beginner",
            handsRequired = HandRequirement.TWO_HANDS,
            palmOrientation = "Palms pressed flat together, then hinged open like a book",
            wristOrientation = "Palms facing up when opened",
            movementDirection = "Open hands like opening a hardcover book",
            description = "Place both palms flat together vertically, then hinge them open along the pinky edges like opening a book.",
            steps = listOf(
                SignStepDetail(
                    stepNumber = 1,
                    title = "Closed Book Hands",
                    instruction = "Place both flat palms touching together vertically in front of your chest (closed book).",
                    handshapeType = ISLHandshapeType.TWO_HANDS_BOOK_OPEN,
                    motionDirection = MotionDirection.NONE,
                    motionDescription = "Palms closed flat against each other",
                    anatomicalFocus = "Fingers upright and touching",
                    visualAidTip = "Represents a closed book"
                ),
                SignStepDetail(
                    stepNumber = 2,
                    title = "Hinge Along Pinky Edges",
                    instruction = "Keep the pinky edges of both palms touching while separating the thumbs and index fingers.",
                    handshapeType = ISLHandshapeType.TWO_HANDS_BOOK_OPEN,
                    motionDirection = MotionDirection.OUTWARD,
                    motionDescription = "Palms hinge open smoothly",
                    anatomicalFocus = "Pinkies remain joined as book spine",
                    visualAidTip = "Spine of book stays touching"
                ),
                SignStepDetail(
                    stepNumber = 3,
                    title = "Open Palms Upward",
                    instruction = "Open both palms fully so they face up toward the ceiling like an open book being read.",
                    handshapeType = ISLHandshapeType.TWO_HANDS_BOOK_OPEN,
                    motionDirection = MotionDirection.OUTWARD,
                    motionDescription = "Open palms side by side",
                    anatomicalFocus = "Palms flat, facing upward",
                    visualAidTip = "Looks like an open book"
                ),
                SignStepDetail(
                    stepNumber = 4,
                    title = "Hold Reading View",
                    instruction = "Hold the open book palms steady and look down slightly at the open pages.",
                    handshapeType = ISLHandshapeType.TWO_HANDS_BOOK_OPEN,
                    motionDirection = MotionDirection.NONE,
                    motionDescription = "Hold open book gesture",
                    anatomicalFocus = "Eye gaze directed at open palms",
                    visualAidTip = "Reading posture"
                )
            ),
            contextOfUse = "Reading time, library sessions, studying, literacy lessons."
        ),

        ISLSignItem(
            id = "isl_school",
            term = "SCHOOL (स्कूल / विद्यालय)",
            hindiEquivalent = "स्कूल / विद्यालय",
            englishMeaning = "School, Educational Institution",
            category = "Education",
            difficulty = "Intermediate",
            handsRequired = HandRequirement.TWO_HANDS,
            palmOrientation = "Palms flat; dominant palm claps gently onto non-dominant palm twice",
            wristOrientation = "Horizontal palms at chest level",
            movementDirection = "Two gentle horizontal claps symbolizing study and discipline",
            description = "Hold non-dominant palm flat facing up, and clap your dominant flat palm down onto it twice smoothly.",
            steps = listOf(
                SignStepDetail(
                    stepNumber = 1,
                    title = "Prepare Base Palm",
                    instruction = "Extend non-dominant palm flat horizontally in front of chest, palm facing up.",
                    handshapeType = ISLHandshapeType.FLAT_PALM_WAVE,
                    motionDirection = MotionDirection.NONE,
                    motionDescription = "Base palm horizontal",
                    anatomicalFocus = "Steady forearm",
                    visualAidTip = "Table / desk surface"
                ),
                SignStepDetail(
                    stepNumber = 2,
                    title = "Raise Dominant Palm",
                    instruction = "Raise dominant flat palm 10 cm above base palm, palm facing down.",
                    handshapeType = ISLHandshapeType.FLAT_PALM_WAVE,
                    motionDirection = MotionDirection.UPWARD,
                    motionDescription = "Top palm positioned above",
                    anatomicalFocus = "Parallel palms",
                    visualAidTip = "Aligned directly over bottom hand"
                ),
                SignStepDetail(
                    stepNumber = 3,
                    title = "Clap Twice Gently",
                    instruction = "Clap dominant palm down onto bottom palm twice in a crisp, rhythmic motion.",
                    handshapeType = ISLHandshapeType.FLAT_PALM_WAVE,
                    motionDirection = MotionDirection.DOWNWARD,
                    motionDescription = "Two gentle claps",
                    anatomicalFocus = "Wrist and palm contact",
                    visualAidTip = "Crisp double clap"
                ),
                SignStepDetail(
                    stepNumber = 4,
                    title = "Hold & Complete",
                    instruction = "Rest hands in open position.",
                    handshapeType = ISLHandshapeType.FLAT_PALM_WAVE,
                    motionDirection = MotionDirection.NONE,
                    motionDescription = "Hold completed sign",
                    anatomicalFocus = "Calm posture",
                    visualAidTip = "Standard ISLRTC school sign"
                )
            ),
            contextOfUse = "School schedule, classroom conversations, teacher interactions."
        ),

        // --- 4. EMOTIONS & FEELINGS ---
        ISLSignItem(
            id = "isl_happy",
            term = "HAPPY (खुश / प्रसन्न)",
            hindiEquivalent = "खुश / आनंदित",
            englishMeaning = "Happy, Joyful, or Delighted",
            category = "Emotions",
            difficulty = "Beginner",
            handsRequired = HandRequirement.TWO_HANDS,
            palmOrientation = "Open flat palms facing chest",
            wristOrientation = "Tilted upward near chest",
            movementDirection = "Brush upward repeatedly against chest in circular rising joy motion",
            description = "Brush flat open palms upward against your chest repeatedly in rising arcs accompanied by a broad joyful smile.",
            steps = listOf(
                SignStepDetail(
                    stepNumber = 1,
                    title = "Palms at Lower Chest",
                    instruction = "Place both flat palms near lower chest with fingers together pointing inward.",
                    handshapeType = ISLHandshapeType.PALM_HEART_FEELING,
                    motionDirection = MotionDirection.NONE,
                    motionDescription = "Palms ready at chest",
                    anatomicalFocus = "Symmetrical hands",
                    visualAidTip = "Starts near lower ribs"
                ),
                SignStepDetail(
                    stepNumber = 2,
                    title = "Brush Upward",
                    instruction = "Brush both palms upward against your chest in a light rising motion.",
                    handshapeType = ISLHandshapeType.PALM_HEART_FEELING,
                    motionDirection = MotionDirection.UPWARD,
                    motionDescription = "Upward sweeping motion",
                    anatomicalFocus = "Chest contact brush",
                    visualAidTip = "Rising feeling of joy"
                ),
                SignStepDetail(
                    stepNumber = 3,
                    title = "Repeat Rising Arc",
                    instruction = "Perform the upward brushing motion a second time with enthusiastic energy.",
                    handshapeType = ISLHandshapeType.PALM_HEART_FEELING,
                    motionDirection = MotionDirection.UPWARD,
                    motionDescription = "Second rising brush",
                    anatomicalFocus = "Continuous positive flow",
                    visualAidTip = "Double upward stroke"
                ),
                SignStepDetail(
                    stepNumber = 4,
                    title = "Radiant Smile",
                    instruction = "Hold hands at upper chest and display a warm, radiant smile.",
                    handshapeType = ISLHandshapeType.PALM_HEART_FEELING,
                    motionDirection = MotionDirection.NONE,
                    motionDescription = "Hold joyful pose",
                    anatomicalFocus = "Broad happy facial expression",
                    visualAidTip = "Smile is essential in ISL emotion signs"
                )
            ),
            contextOfUse = "Expressing happiness, celebrating success, emotional check-in."
        ),

        ISLSignItem(
            id = "isl_friend",
            term = "FRIEND (दोस्त / मित्र)",
            hindiEquivalent = "दोस्त / मित्र",
            englishMeaning = "Friend, Companion, or Buddy",
            category = "Family",
            difficulty = "Beginner",
            handsRequired = HandRequirement.TWO_HANDS,
            palmOrientation = "Index fingers hooked together, alternating sides",
            wristOrientation = "Interlocking hook at chest level",
            movementDirection = "Hook index fingers together, then reverse and hook the other way",
            description = "Hook dominant index finger over non-dominant index finger, then reverse and hook from the other side, symbolizing friendship.",
            steps = listOf(
                SignStepDetail(
                    stepNumber = 1,
                    title = "Form Index Hooks",
                    instruction = "Curl both index fingers into curved hooks (X-handshape), other fingers in fists.",
                    handshapeType = ISLHandshapeType.TWO_HANDS_FAMILY_CIRCLE,
                    motionDirection = MotionDirection.NONE,
                    motionDescription = "Both index fingers hooked",
                    anatomicalFocus = "Curved index knuckles",
                    visualAidTip = "Hook shapes ready"
                ),
                SignStepDetail(
                    stepNumber = 2,
                    title = "Hook Left Over Right",
                    instruction = "Hook your dominant curved index finger over the top of the non-dominant index finger.",
                    handshapeType = ISLHandshapeType.TWO_HANDS_FAMILY_CIRCLE,
                    motionDirection = MotionDirection.JOIN_TOGETHER,
                    motionDescription = "First interlocking hook",
                    anatomicalFocus = "Interlocking finger connection",
                    visualAidTip = "First link formed"
                ),
                SignStepDetail(
                    stepNumber = 3,
                    title = "Reverse & Re-Hook",
                    instruction = "Unhook, flip hands, and hook with the other index finger on top.",
                    handshapeType = ISLHandshapeType.TWO_HANDS_FAMILY_CIRCLE,
                    motionDirection = MotionDirection.JOIN_TOGETHER,
                    motionDescription = "Reversed interlocking hook",
                    anatomicalFocus = "Reciprocal link",
                    visualAidTip = "Shows mutual friendship"
                ),
                SignStepDetail(
                    stepNumber = 4,
                    title = "Hold Bond & Smile",
                    instruction = "Hold the linked fingers together and smile warmly.",
                    handshapeType = ISLHandshapeType.TWO_HANDS_FAMILY_CIRCLE,
                    motionDirection = MotionDirection.NONE,
                    motionDescription = "Hold connected fingers",
                    anatomicalFocus = "Steady connection",
                    visualAidTip = "Symbolizes strong bond"
                )
            ),
            contextOfUse = "Introducing friends, talking about classmates, building social bonds."
        ),

        ISLSignItem(
            id = "isl_yes",
            term = "YES (हाँ)",
            hindiEquivalent = "हाँ / जी हाँ",
            englishMeaning = "Agreement, Affirmation, or Confirmation",
            category = "Basics",
            difficulty = "Beginner",
            handsRequired = HandRequirement.ONE_HAND,
            palmOrientation = "Fist facing forward like a head",
            wristOrientation = "Nodding wrist up and down",
            movementDirection = "Bob fist up and down twice like a nodding head",
            description = "Make a firm fist with dominant hand and nod your wrist up and down twice like a person nodding their head in agreement.",
            steps = listOf(
                SignStepDetail(
                    stepNumber = 1,
                    title = "Form S-Fist",
                    instruction = "Make a firm fist with your dominant hand, thumb folded across your front fingers.",
                    handshapeType = ISLHandshapeType.FIST_THUMBS_UP,
                    motionDirection = MotionDirection.NONE,
                    motionDescription = "Firm fist formed",
                    anatomicalFocus = "Knuckles facing forward",
                    visualAidTip = "Fist represents a head"
                ),
                SignStepDetail(
                    stepNumber = 2,
                    title = "Nod Downward",
                    instruction = "Bend your wrist downward smoothly so your fist dips forward like a nod.",
                    handshapeType = ISLHandshapeType.FIST_THUMBS_UP,
                    motionDirection = MotionDirection.DOWNWARD,
                    motionDescription = "Wrist bends down",
                    anatomicalFocus = "Wrist hinge flexion",
                    visualAidTip = "First downward nod"
                ),
                SignStepDetail(
                    stepNumber = 3,
                    title = "Nod Up and Down Again",
                    instruction = "Raise fist back up and nod downward a second time.",
                    handshapeType = ISLHandshapeType.FIST_THUMBS_UP,
                    motionDirection = MotionDirection.UPWARD,
                    motionDescription = "Second rhythmic nod",
                    anatomicalFocus = "Smooth nodding cadence",
                    visualAidTip = "Clear double nod"
                ),
                SignStepDetail(
                    stepNumber = 4,
                    title = "Affirmative Head Nod",
                    instruction = "Hold the fist and simultaneously nod your own head in agreement.",
                    handshapeType = ISLHandshapeType.FIST_THUMBS_UP,
                    motionDirection = MotionDirection.NONE,
                    motionDescription = "Hold affirmative position",
                    anatomicalFocus = "Synchronized head nod",
                    visualAidTip = "Clear confirmation"
                )
            ),
            contextOfUse = "Confirming answers, agreeing to questions, positive responses."
        ),

        ISLSignItem(
            id = "isl_no",
            term = "NO (नहीं)",
            hindiEquivalent = "नहीं / मत",
            englishMeaning = "Negation, Disagreement, or Refusal",
            category = "Basics",
            difficulty = "Beginner",
            handsRequired = HandRequirement.ONE_HAND,
            palmOrientation = "Index finger pointing up, shaking side to side, or index+middle snapping onto thumb",
            wristOrientation = "Upright at chest level",
            movementDirection = "Shake index finger side to side twice or snap onto thumb",
            description = "Hold up index finger and shake side-to-side with a slight head shake, or snap index and middle fingers down onto the thumb.",
            steps = listOf(
                SignStepDetail(
                    stepNumber = 1,
                    title = "Raise Index Finger",
                    instruction = "Raise dominant index finger straight up with other fingers curled in a fist.",
                    handshapeType = ISLHandshapeType.INDEX_SHAKE_NO,
                    motionDirection = MotionDirection.UPWARD,
                    motionDescription = "Index finger upright",
                    anatomicalFocus = "Index straight, palm out",
                    visualAidTip = "Single finger point"
                ),
                SignStepDetail(
                    stepNumber = 2,
                    title = "Shake to the Left",
                    instruction = "Shake your index finger to the left side.",
                    handshapeType = ISLHandshapeType.INDEX_SHAKE_NO,
                    motionDirection = MotionDirection.WAVE_SIDE_TO_SIDE,
                    motionDescription = "Finger moves left",
                    anatomicalFocus = "Wrist pivot left",
                    visualAidTip = "Starts side-to-side shake"
                ),
                SignStepDetail(
                    stepNumber = 3,
                    title = "Shake to the Right",
                    instruction = "Shake your index finger back across to the right side.",
                    handshapeType = ISLHandshapeType.INDEX_SHAKE_NO,
                    motionDirection = MotionDirection.WAVE_SIDE_TO_SIDE,
                    motionDescription = "Finger moves right",
                    anatomicalFocus = "Wrist pivot right",
                    visualAidTip = "Clear negation wave"
                ),
                SignStepDetail(
                    stepNumber = 4,
                    title = "Head Shake Negation",
                    instruction = "Hold position and shake your head side-to-side with a firm negative expression.",
                    handshapeType = ISLHandshapeType.INDEX_SHAKE_NO,
                    motionDirection = MotionDirection.NONE,
                    motionDescription = "Hold negative gesture",
                    anatomicalFocus = "Synchronized head shake",
                    visualAidTip = "Non-manual marker in ISL"
                )
            ),
            contextOfUse = "Declining, setting boundaries, answering negative questions."
        )
    )

    fun getByCategory(category: String): List<ISLSignItem> {
        return allISLSigns.filter { it.category.equals(category, ignoreCase = true) }
    }

    fun getSignById(id: String): ISLSignItem? {
        return allISLSigns.find { it.id == id }
    }

    val availableCategories = listOf(
        "All Signs",
        "Greetings",
        "Emergency",
        "Everyday",
        "Education",
        "Emotions",
        "Family",
        "Food"
    )
}
