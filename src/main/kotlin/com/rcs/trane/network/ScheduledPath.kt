package org.example.com.rcs.trane.network

import java.time.Duration

data class ScheduledPath(val segments: List<ScheduledPathSegment>) {

    fun timeWaitingMillis(): Long {
        return segments.indices.sumOf { i ->
            if (i + 1 < segments.size)
                Duration.between(
                    segments[i].arrival.toInstantToday(),
                    segments[i+1].departure.toInstantToday())
                    .toMillis()
            else
                0
        }
    }

    fun totalDurationMillis(): Long {
        return segments.sumOf { it.durationMillis() } + timeWaitingMillis()
    }

    fun totalDistance(): Int {
        return segments.sumOf { it.distance }
    }
}
