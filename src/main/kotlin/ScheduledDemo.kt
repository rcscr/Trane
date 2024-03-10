package org.example

import com.rcs.trane.network.RouteType
import org.example.com.rcs.trane.network.LocalTime
import org.example.com.rcs.trane.network.ScheduledRouteNetwork
import java.time.Duration
import kotlin.time.toKotlinDuration

fun main() {
    val routeNetwork = ScheduledRouteNetwork()

    routeNetwork.addScheduledRoute(
        "A",
        RouteType.Unidirectional,
        linkedSetOf(0, 1, 2),
        listOf(3, 3),
        listOf(
            listOf(LocalTime("12:30")),
            listOf(LocalTime("13:30")),
            listOf(LocalTime("14:30"))
        )
    )

    routeNetwork.addScheduledRoute(
        "B",
        RouteType.Unidirectional,
        linkedSetOf(2, 3, 4),
        listOf(3, 3),
        listOf(
            listOf(LocalTime("14:45")),
            listOf(LocalTime("15:45")),
            listOf(LocalTime("16:45"))
        )
    )

    val desiredDepartureTime = LocalTime("12:10")

    val path = routeNetwork.getShortestPathByTime(0, 4, desiredDepartureTime)!!

    val initialWaitTime = Duration.between(
        desiredDepartureTime.toInstantToday(),
        path.segments.first().departure)
        .toKotlinDuration()

    val waitTimeBetween = Duration.ofMillis(path.timeWaitingBetweenMillis()).toKotlinDuration()

    val totalDuration = Duration.ofMillis(path.totalDurationMillis()).toKotlinDuration()

    println("Discovering quickest path (by duration) from stop 0 to stop 4")
    println("Desired departure time: $desiredDepartureTime")
    println("Quickest path:")
    path.segments.forEach { println(it) }
    println("Total distance: ${path.totalDistance()} km")
    println("Time spent waiting for first train: $initialWaitTime minutes")
    println("Time spend waiting between trains: $waitTimeBetween")
    println("Total duration (excluding initial wait time): $totalDuration")
}