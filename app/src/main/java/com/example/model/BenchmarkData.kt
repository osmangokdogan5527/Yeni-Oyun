package com.example.model

import kotlinx.serialization.Serializable

@Serializable
data class BenchmarkScore(
    val performanceScore: Int = 0,
    val displayScore: Int = 0,
    val cameraScore: Int = 0,
    val batteryScore: Int = 0,
    val softwareScore: Int = 0,
    val overallScore: Int = 0
)

@Serializable
data class LeaderboardEntry(
    val id: String,
    val rank: Int = 0,
    val modelName: String,
    val brandName: String,
    val brandColorHex: Long = 0xFF2563EB,
    val isPlayer: Boolean = false,
    val releaseYear: Int,
    val price: Int,
    val tierTitle: String,
    val antutuScore: Int = 0,
    val dxomarkScore: Int = 0,
    val geekbenchSingle: Int = 0,
    val geekbenchMulti: Int = 0,
    val batteryLifeHours: Float = 0f,
    val peakTempCelsius: Float = 0f,
    val socName: String = "",
    val cameraSummary: String = "",
    val verdict: String = ""
)
