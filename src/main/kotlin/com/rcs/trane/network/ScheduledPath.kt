package org.example.com.rcs.trane.network

import java.time.Duration

data class ScheduledPath(val segments: List<ScheduledPathSegment>) {

    fun timeWaitingBetweenMillis(): Long {
        return segments.indices.sumOf { i ->
            if (i + 1 < segments.size) {
                Duration.between(segments[i].arrival, segments[i + 1].departure).toMillis()
            } else {
                0
            }
        }
    }

    fun totalDurationMillis(): Long {
        return segments.sumOf { it.durationMillis() } + timeWaitingBetweenMillis()
    }

    fun totalDistance(): Int {
        return segments.sumOf { it.distance }
    }
}
