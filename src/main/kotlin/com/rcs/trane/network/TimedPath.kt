package org.example.com.rcs.trane.network

import java.time.Duration

data class TimedPath(val segments: List<TimedPathSegment>) {

    fun timeWaitingMillis(): Long {
        return segments.indices.sumOf { i ->
            if (i + 1 < segments.size)
                Duration.between(segments[i].arrival, segments[i + 1].departure).toMillis()
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
