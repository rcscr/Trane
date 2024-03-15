package org.example.com.rcs.trane.network

import java.time.Duration
import java.time.LocalDateTime

data class ScheduledPathSegment(
    val route: String,
    val stops: List<Int>,
    val distance: Int,
    val departure: LocalDateTime,
    val arrival: LocalDateTime,
    val delayMillis: Long
) {
    fun durationMillis(): Long {
        return Duration.between(departure, arrival).toMillis()
    }
}