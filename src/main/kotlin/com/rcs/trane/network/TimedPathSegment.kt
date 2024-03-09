package org.example.com.rcs.trane.network

import java.time.Duration
import java.time.Instant

data class TimedPathSegment(
    val route: String,
    val stops: List<Int>,
    val distance: Int,
    val departure: Instant,
    val arrival: Instant
) {
    fun durationMillis(): Long {
        return Duration.between(departure, arrival).toMillis()
    }
}