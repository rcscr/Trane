package org.example.com.rcs.trane.network

import com.rcs.trane.network.RouteType
import com.rcs.trane.network.StopData
import java.time.Duration
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.temporal.ChronoUnit
import java.util.SequencedSet

class ScheduledRouteNetwork: RouteNetwork() {

    fun addScheduledRoute(
        route: String,
        routeType: RouteType,
        stops: SequencedSet<Int>,
        distances: List<Int>,
        times: List<List<LocalTime>>
    ) {
        validate(routeType, stops, times)
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

    fun getShortestPathByTime(start: Int, end: Int, depart: LocalDateTime): ScheduledPath? {
        return graph.getLightestPathComplex(
            start,
            end,
            scheduledPathBuilder(depart),
            ScheduledPath.byDurationComparator,
            initialTimedPath())
            ?.weight
    }

    private fun validate(routeType: RouteType, stops: SequencedSet<Int>, times: List<List<LocalTime>>) {
        if (routeType != RouteType.Unidirectional) {
            throw UnsupportedOperationException("TimedRouteNetwork currently only supports Unidirectional routes")
        }
        if (stops.size != times.size) {
            throw IllegalArgumentException("Each stop must have a corresponding timetable")
        }
    }

    private fun scheduledPathBuilder(depart: LocalDateTime): (path: ScheduledPath, nodeA: Int, nodeB: Int) -> List<ScheduledPath> {
        return { path: ScheduledPath, nodeA: Int, nodeB: Int ->
            var currentTime = depart

            if (path.segments.isNotEmpty()) {
                currentTime = currentTime
                    .plus(Duration.between(depart, path.segments.first().departure).toMillis(), ChronoUnit.MILLIS)
                    .plus(path.totalDurationMillis(), ChronoUnit.MILLIS)
            }

            val stopDataA = graph.getValue(nodeA)!!
            val stopDataB = graph.getValue(nodeB)!!

            stopDataA.routes[nodeB]!!.map { route ->
                val timesForThisRouteA = stopDataA.times!![route]!!
                val timesForThisRouteB = stopDataB.times!![route]!!

                val departure = findNextTime(currentTime, timesForThisRouteA)
                val arrival = findNextTime(departure, timesForThisRouteB)

                val newPathSegment = ScheduledPathSegment(
                    route,
                    listOf(nodeA, nodeB),
                    stopDataA.distances[nodeB]!!,
                    departure,
                    arrival
                )
                ScheduledPath(mergePathSegmentIntoPath(path.segments, newPathSegment))
            }
        }
    }

    private fun initialTimedPath(): ScheduledPath {
        return ScheduledPath(listOf())
    }

    private fun mergePathSegmentIntoPath(path: List<ScheduledPathSegment>, pathSegment: ScheduledPathSegment): List<ScheduledPathSegment> {
        if (path.isNotEmpty() && path.last().route == pathSegment.route) {
            val lastSegment = path.last()

            // this should never happen as used internally
            if (lastSegment.stops.last() != pathSegment.stops.first() || lastSegment.arrival != pathSegment.departure) {
                throw AssertionError(
                    "The last element of the path must be equal to the first element of the path segment being added.")
            }

            val pathWithoutLastSegment = path.subList(0, path.size - 1)
            val newSegmentWithoutFirstStop = pathSegment.stops.subList(1, pathSegment.stops.size)
            val mergedSegment = ScheduledPathSegment(
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

    /**
     * If a time is found today, return that time, else return the soonest available time tomorrow
     */
    private fun findNextTime(start: LocalDateTime, timetable: List<LocalTime>): LocalDateTime {
        return (0..1)
            .firstNotNullOf { day ->
                timetable
                    .map { it.atDate(start.toLocalDate()).plus(day.toLong(), ChronoUnit.DAYS) }
                    .firstOrNull { it >= start }
            }
    }
}