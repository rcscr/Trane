package org.example.com.rcs.trane.network

import com.rcs.trane.network.RouteType
import com.rcs.trane.network.StopData
import java.time.Duration
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.SequencedSet

class TimedRouteNetwork: RouteNetwork() {

    fun addRouteWithTimetable(
        route: String,
        routeType: RouteType,
        stops: SequencedSet<Int>,
        distances: List<Int>,
        times: List<List<Instant>>
    ) {
        validate(route, routeType, stops, distances, times)
        super.addRoute(route, routeType, stops, distances)

        // add times
        for (i in stops.indices) {
            val stop = stops.elementAt(i)
            val existingValue = graph.getValue(stop)!!
            val existingTimes = existingValue.times?.toMutableMap() ?: mutableMapOf()
            existingTimes[route] = times[i]
            graph.setValue(
                stop,
                StopData(
                    existingValue.distances,
                    existingValue.routes,
                    existingTimes))
        }
    }

    fun getShortestPathByTime(start: Int, end: Int, depart: Instant): TimedPath? {
        return graph.getLightestPathComplex(
            start,
            end,
            timedPathBuilder(depart),
            this::timedPathByDuration,
            initialTimedPath())
            ?.weight
    }

    private fun validate(
        route: String,
        routeType: RouteType,
        stops: SequencedSet<Int>,
        distances: List<Int>,
        times: List<List<Instant>>
    ) {
        if (routeType != RouteType.Unidirectional) {
            throw UnsupportedOperationException("TimedRouteNetwork currently only supports Unidirectional routes")
        }
        if (stops.size != times.size) {
            throw IllegalArgumentException("Each stop must have a corresponding timetable")
        }
        super.validate(route, routeType, stops, distances)
    }

    private fun timedPathBuilder(depart: Instant): (path: TimedPath, nodeA: Int, nodeB: Int) -> List<TimedPath> {
        return { path: TimedPath, nodeA: Int, nodeB: Int ->
            var currentTime = depart

            if (path.segments.isNotEmpty()) {
                currentTime = currentTime
                    .plus(Duration.between(depart, path.segments.first().departure).toMillis(), ChronoUnit.MILLIS)
                    .plus(path.totalDurationMillis(), ChronoUnit.MILLIS)
            }

            val nodeAValue = graph.getValue(nodeA)!!
            val nodeBValue = graph.getValue(nodeB)!!

            nodeAValue.routes[nodeB]!!
                .mapNotNull { route ->
                    val nextDeparture = nodeAValue.times!![route]!!.firstOrNull { it >= currentTime }
                    if (nextDeparture == null) {
                        null
                    } else {
                        val arrival = nodeBValue.times!![route]!!.firstOrNull { it > nextDeparture }

                        if (arrival == null) {
                            throw IllegalStateException("Route $route connection $nodeA and $nodeA has a " +
                                    "departure from $nodeA but no corresponding arrival at $nodeB")
                        }

                        val newPathSegment = TimedPathSegment(
                            route,
                            listOf(nodeA, nodeB),
                            nodeAValue.distances[nodeB]!!,
                            nextDeparture,
                            arrival
                        )
                        TimedPath(mergePathSegmentIntoPath(path.segments, newPathSegment))
                    }
                }
        }
    }

    private fun timedPathByDuration(pathA: TimedPath, pathB: TimedPath): Int {
        return pathA.totalDurationMillis().compareTo(pathB.totalDurationMillis())
    }

    private fun initialTimedPath(): TimedPath {
        return TimedPath(listOf())
    }

    private fun mergePathSegmentIntoPath(path: List<TimedPathSegment>, pathSegment: TimedPathSegment): List<TimedPathSegment> {
        if (path.isNotEmpty() && path.last().route == pathSegment.route) {
            val lastSegment = path.last()

            // this should never happen as used internally
            if (lastSegment.stops.last() != pathSegment.stops.first() || lastSegment.arrival != pathSegment.departure) {
                throw AssertionError(
                    "The last element of the path must be equal to " +
                            "the first element of the path segment being added.")
            }

            val pathWithoutLastSegment = path.subList(0, path.size - 1)
            val newSegmentWithoutFirstStop = pathSegment.stops.subList(1, pathSegment.stops.size)
            val mergedSegment = TimedPathSegment(
                pathSegment.route,
                lastSegment.stops + newSegmentWithoutFirstStop,
                lastSegment.distance + pathSegment.distance,
                lastSegment.departure,
                pathSegment.arrival
            )

            return pathWithoutLastSegment + mergedSegment
        }

        return path + pathSegment
    }
}