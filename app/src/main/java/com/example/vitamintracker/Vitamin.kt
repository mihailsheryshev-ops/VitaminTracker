package com.example.vitamintracker

data class Vitamin(
    val id: String,
    val name: String,
    val doseMg: Float,
    val normMg: Float,
    val description: String,
    val excessSymptoms: String
)