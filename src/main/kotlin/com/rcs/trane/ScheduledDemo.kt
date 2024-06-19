package org.example.com.rcs.trane

import com.rcs.trane.network.RouteType
import org.example.com.rcs.trane.network.ScheduledRouteNetwork
import java.time.Duration
import java.time.LocalDate
import java.time.LocalTime
import kotlin.time.toKotlinDuration

fun main() {
    val routeNetwork = ScheduledRouteNetwork()

    routeNetwork.addScheduledRoute(
        "A",
        RouteType.Unidirectional,
        linkedSetOf(0, 1, 2),
        listOf(3, 3),
        listOf(listOf(LocalTime.parse("12:30")), listOf(LocalTime.parse("13:30")), listOf(LocalTime.parse("14:30")))
    )

    routeNetwork.addScheduledRoute(
        "B",
        RouteType.Unidirectional,
        linkedSetOf(2, 3, 4),
        listOf(2, 5),
        listOf(listOf(LocalTime.parse("14:45")), listOf(LocalTime.parse("15:45")), listOf(LocalTime.parse("16:45")))
    )

    routeNetwork.addScheduledRoute(
        "C",
        RouteType.Unidirectional,
        linkedSetOf(2, 4),
        listOf(7),
        listOf(listOf(LocalTime.parse("15:30")), listOf(LocalTime.parse("16:30")))
    )

    val desiredDepartureTime = LocalTime.parse("12:10").atDate(LocalDate.now())

    // B is 15 minutes delayed, so will arrive at final destination at 17:00
    routeNetwork.addDelay("B", 0, desiredDepartureTime.toLocalDate(), Duration.ofMinutes(15).toMillis())

    // C, though originally scheduled to arrive sooner than B,
    // is 1 hour delayed, so will arrive at final destination at 17:30
    routeNetwork.addDelay("C", 0, desiredDepartureTime.toLocalDate(), Duration.ofHours(3).toMillis())

    val path = routeNetwork.getShortestPathByTime(0, 4, desiredDepartureTime)!!
    val initialWaitTime = Duration.between(desiredDepartureTime, path.segments.first().departure).toKotlinDuration()
    val waitTimeBetween = Duration.ofMillis(path.timeWaitingBetweenMillis()).toKotlinDuration()
    val totalDuration = Duration.ofMillis(path.totalDurationMillis()).toKotlinDuration()

    println("Discovering quickest path (by duration) from stop 0 to stop 4")
    println("Desired departure time: $desiredDepartureTime")
    println("Quickest path:")
    path.segments.forEach { println(it) }
    println("Total distance: ${path.totalDistance()}km")
    println("Total delays: ${Duration.ofMillis(path.totalDelayMillis()).toKotlinDuration()}")
    println("Time spent waiting for first train: $initialWaitTime")
    println("Time spend waiting between trains: $waitTimeBetween")
    println("Trip duration (excluding initial wait time): $totalDuration")
}