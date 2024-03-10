package org.example.com.rcs.trane.network

import java.time.*
import java.time.LocalTime

data class LocalTime(private val localTime: String) {

    init {
        try {
            LocalTime.parse(localTime)
        } catch (e: Exception){
            throw IllegalArgumentException("Invalid time")
        }
    }

    fun toInstantOnDay(day: Instant): Instant {
        val localTime = LocalTime.parse(localTime)
        val today = LocalDateTime.ofInstant(day, ZoneId.of("UTC"))
        val dateTime = LocalDateTime.of(today.toLocalDate(), localTime)
        return dateTime.toInstant(ZoneOffset.UTC)
    }

    fun toInstantToday(): Instant {
        return toInstantOnDay(Instant.now())
    }
}