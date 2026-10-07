package com.example.data.model

data class LetterMnemonic(
    val letter: Char,
    val counterpart: Char,
    val title: String,
    val visualMnemonic: String,
    val hintRule: String,
    val memoryTrick: String,
    val exampleWord: String,
    val iconDescription: String
)

data class SyllableWord(
    val word: String,
    val syllables: List<String>,
    val phoneticSpelling: String,
    val definition: String,
    val category: String,
    val sampleSentence: String
)

data class DyslexiaStory(
    val id: String,
    val title: String,
    val summary: String,
    val content: String,
    val simplifiedContent: String,
    val keywords: List<String>,
    val category: String,
    val readingLevel: String
)
