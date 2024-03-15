package org.example.com.rcs.trane.network

import java.time.Duration

data class ScheduledPath(val segments: List<ScheduledPathSegment>) {

    companion object {
        val byDurationComparator = compareBy<ScheduledPath> { it.totalDurationMillis() }
            .thenBy { it.numberOfTransfers() }
            .thenBy { it.numberOfStops() }
            .thenBy { it.totalDistance() }
    }

    fun timeWaitingBetweenMillis(): Long {
        return segments.indices.sumOf { i ->
            if (i + 1 < segments.size) {
                Duration.between(segments[i].arrival, segments[i + 1].departure).toMillis()
            } else {
                0
            }
        }
    }

    fun totalDelayMillis(): Long {
        return segments.sumOf { it.delayMillis }
    }

    fun totalDurationMillis(): Long {
        return segments.sumOf { it.durationMillis() } + timeWaitingBetweenMillis()
    }

    fun totalDistance(): Int {
        return segments.sumOf { it.distance }
    }

    internal fun numberOfTransfers(): Int {
        return segments.size - 1
    }

    internal fun numberOfStops(): Int {
        return segments
            .mapIndexed { i, seg -> if (i == 0) seg.stops.size else seg.stops.size - 1 }
            .sum()
    }
}
