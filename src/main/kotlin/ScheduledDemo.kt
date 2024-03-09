package org.example

import com.rcs.trane.network.RouteType
import org.example.com.rcs.trane.network.ScheduledRouteNetwork
import java.time.Duration
import java.time.Instant
import java.time.temporal.ChronoUnit
import kotlin.time.toKotlinDuration

fun nextNDays(time: Instant, days: Int): List<Instant> {
    return (0..<days).map { i -> time.plus(i.toLong(), ChronoUnit.DAYS) }
}

fun main() {
    val routeNetwork = ScheduledRouteNetwork()

    routeNetwork.addScheduledRoute(
        "A",
        RouteType.Unidirectional,
        linkedSetOf(0, 1, 2),
        listOf(3, 3),
        listOf(
            nextNDays(Instant.parse("2024-03-09T12:30:00Z"), 3),
            nextNDays(Instant.parse("2024-03-09T13:30:00Z"), 3),
            nextNDays(Instant.parse("2024-03-09T14:30:00Z"), 3),
        )
    )

    routeNetwork.addScheduledRoute(
        "B",
        RouteType.Unidirectional,
        linkedSetOf(2, 3, 4),
        listOf(3, 3),
        listOf(
            nextNDays(Instant.parse("2024-03-09T14:45:00Z"), 3),
            nextNDays(Instant.parse("2024-03-09T15:45:00Z"), 3),
            nextNDays(Instant.parse("2024-03-09T16:45:00Z"), 3),
        )
    )

    val desiredDepartureTime = Instant.parse("2024-03-10T12:10:00Z")

    val path = routeNetwork.getShortestPathByTime(0, 4, desiredDepartureTime)!!

    println("Discovering quickest path (by duration) from stop 0 to stop 4")
    println("Desired departure time: $desiredDepartureTime")
    println("Quickest path:")
    path.segments.forEach { println(it) }
    println("Total distance: ${path.totalDistance()} km")
    println("Time spent waiting for first train: ${Duration.between(desiredDepartureTime, path.segments.first().departure).toKotlinDuration()} minutes")
    println("Time spend waiting between trains: ${Duration.ofMillis(path.timeWaitingMillis()).toKotlinDuration()}")
    println("Total duration (excluding initial wait time): ${Duration.ofMillis(path.totalDurationMillis()).toKotlinDuration()}")
}