package com.example.model

import kotlinx.serialization.Serializable

@Serializable
data class BenchmarkScore(
    val cpuScore: Int,
    val gpuScore: Int,
    val memScore: Int,
    val uxScore: Int,
    val totalAntutuScore: Int,
    
    // DXOMARK Camera Breakdown
    val photoScore: Int,
    val videoScore: Int,
    val zoomScore: Int,
    val nightScore: Int,
    val totalDxomarkScore: Int,
    
    // Geekbench Style Single / Multi Core
    val geekbenchSingle: Int,
    val geekbenchMulti: Int,
    
    // Battery & Thermal Test
    val screenOnTimeHours: Float,
    val peakTempCelsius: Float,
    val thermalThrottlingPercent: Int,
    val durabilityScore: Int
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
    val antutuScore: Int,
    val dxomarkScore: Int,
    val geekbenchSingle: Int,
    val geekbenchMulti: Int,
    val batteryLifeHours: Float,
    val peakTempCelsius: Float,
    val socName: String,
    val cameraSummary: String,
    val verdict: String
)
