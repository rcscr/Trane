package org.example.com.rcs.trane.network

import com.rcs.trane.network.RouteType
import com.rcs.trane.network.StopData
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.temporal.ChronoUnit
import java.util.SequencedSet

class ScheduledRouteNetwork: RouteNetwork() {

    // maps a Pair(route, tripIndex, date) to its delay in millis
    private val delays = mutableMapOf<Triple<String, Int, LocalDate>, Long>()

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

    /**
     * tripIndex corresponds to the i'th entry (trip) in the stop's timetable
     */
    fun addDelay(route: String, tripIndex: Int, day: LocalDate, delayMillis: Long) {
        delays[Triple(route, tripIndex, day)] = delayMillis
    }

    fun getShortestPathByTime(start: Int, end: Int, depart: LocalDateTime): ScheduledPath? {
        return graph.getLightestPathComplex(
            start,
            end,
            scheduledPathBuilder(depart),
            ScheduledPath.byDurationComparator,
            ScheduledPath(listOf()))
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
                currentTime = depart
                    .plus(Duration.between(depart, path.segments.first().departure).toMillis(), ChronoUnit.MILLIS)
                    .plus(path.totalDurationMillis(), ChronoUnit.MILLIS)
            }

            val stopDataA = graph.getValue(nodeA)!!
            val stopDataB = graph.getValue(nodeB)!!

            stopDataA.routes[nodeB]!!.map { route ->
                val timesForThisRouteA = stopDataA.times!![route]!!
                val timesForThisRouteB = stopDataB.times!![route]!!

                val departure = findNextTime(route, currentTime, timesForThisRouteA)
                val arrival = findNextTime(route, departure.second, timesForThisRouteB)

                val newPathSegment = ScheduledPathSegment(
                    route,
                    listOf(nodeA, nodeB),
                    stopDataA.distances[nodeB]!!,
                    departure.second,
                    arrival.second,
                    Duration.between(departure.first, departure.second).toMillis()
                )

                ScheduledPath(mergePathSegmentIntoPath(path.segments, newPathSegment))
            }
        }
    }

    private fun mergePathSegmentIntoPath(path: List<ScheduledPathSegment>, pathSegment: ScheduledPathSegment): List<ScheduledPathSegment> {
        if (path.isNotEmpty() && path.last().route == pathSegment.route) {
            val lastSegment = path.last()

            // this should never happen as used internally, but it's useful for debugging
            if (lastSegment.stops.last() != pathSegment.stops.first() || lastSegment.arrival != pathSegment.departure) {
                throw AssertionError("The last element of the path must be equal to " +
                        "the first element of the path segment being merged.")
            }

            val newSegmentWithoutFirstStop = pathSegment.stops.subList(1, pathSegment.stops.size)

            val mergedSegment = ScheduledPathSegment(
                pathSegment.route,
                lastSegment.stops + newSegmentWithoutFirstStop,
                lastSegment.distance + pathSegment.distance,
                lastSegment.departure,
                pathSegment.arrival,
                pathSegment.delayMillis // takes the latest delay
            )

            return path.subList(0, path.size - 1) + mergedSegment
        }

        return path + pathSegment
    }

    /**
     * If a time is found today, return that time, else return the soonest available time tomorrow.
     * This method considers any possible delays and returns a:
     * Pair<scheduled departure time, actual departure time>
     * If there is no delay, pair.first == pair.second
     */
    private fun findNextTime(
        route: String,
        start: LocalDateTime,
        timetable: List<LocalTime>
    ): Pair<LocalDateTime, LocalDateTime> {

        return (0..1)
            .firstNotNullOf { day ->
                timetable
                    .map {
                        it.atDate(start.toLocalDate()).plus(day.toLong(), ChronoUnit.DAYS)
                    }
                    .mapIndexed { index, scheduledTime ->
                        Pair(scheduledTime, getActualTime(route, index, scheduledTime))
                    }
                    .firstOrNull {
                        it.second >= start
                    }
            }
    }

    /**
     * Returns the actual time considering delays.
     * If there are no delays, returns the scheduled time.
     */
    private fun getActualTime(route: String, tripIndex: Int, scheduledTime: LocalDateTime): LocalDateTime {
        return delays[Triple(route, tripIndex, scheduledTime.toLocalDate())]
            ?.let { scheduledTime.plus(it, ChronoUnit.MILLIS) }
            ?: scheduledTime
    }
}