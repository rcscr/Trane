package org.example.com.rcs.trane.network

import com.rcs.trane.network.PathSegment

data class Path(val segments: List<PathSegment>) {

    companion object {
        val byNumberOfTransfersComparator = compareBy<Path> { it.numberOfTransfers() }
            .thenBy { it.numberOfStops() }
            .thenBy { it.totalDistance() }

        val byNumberOfStopsComparator = compareBy<Path> { it.numberOfStops() }
            .thenBy { it.numberOfTransfers() }
            .thenBy { it.totalDistance() }
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
