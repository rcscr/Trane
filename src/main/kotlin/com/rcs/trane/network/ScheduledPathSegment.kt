package org.example.com.rcs.trane.network

import java.time.Duration

data class ScheduledPathSegment(
    val route: String,
    val stops: List<Int>,
    val distance: Int,
    val departure: LocalTime,
    val arrival: LocalTime
) {
    fun durationMillis(): Long {
        return Duration.between(departure.toInstantToday(), arrival.toInstantToday()).toMillis()
    }
}