package org.example.com.rcs.trane.network

import java.time.Instant
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneOffset

data class LocalTime(private val localTime: String) {

    init {
        try {
            LocalTime.parse(localTime)
        } catch (e: Exception){
            throw IllegalArgumentException("Invalid time")
        }
    }

    fun toInstantToday(): Instant {
        val localTime = LocalTime.parse(localTime)
        val today = LocalDateTime.now()
        val dateTime = LocalDateTime.of(today.toLocalDate(), localTime)
        return dateTime.toInstant(ZoneOffset.UTC)
    }
}