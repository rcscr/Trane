package org.example.com.rcs.trane.network

import com.rcs.trane.network.PathSegment

data class Path(val segments: List<PathSegment>) {
    fun totalDistance(): Int {
        return segments.sumOf { it.distance }
    }

    internal fun numberOfRoutes(): Int {
        return segments.map { it.route }.distinct().count()
    }

    internal fun numberOfStops(): Int {
        return segments
            .mapIndexed { i, seg -> if (i == 0) seg.stops.size else seg.stops.size - 1 }
            .sum()
    }
}
