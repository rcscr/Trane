package com.rcs.trane.network

import org.assertj.core.api.Assertions.assertThat
import org.example.com.rcs.trane.network.LocalTime
import org.example.com.rcs.trane.network.ScheduledPath
import org.example.com.rcs.trane.network.ScheduledPathSegment
import org.example.com.rcs.trane.network.ScheduledRouteNetwork
import org.junit.jupiter.api.Test
import java.time.Duration
import kotlin.test.assertFailsWith

class ScheduledRouteNetworkTest {

    @Test
    fun `correctly calculates the lightest path by duration`(): Unit = with(scheduledNetwork()) {
        // Act
        val scheduledPath = target.getShortestPathByTime(0, 4, desiredDeparture)!!

        // Assert
        assertThat(scheduledPath).isEqualTo(expectedLightestPathByTime)
        assertThat(scheduledPath.totalDurationMillis()).isEqualTo(expectedTotalDurationMillis)
        assertThat(scheduledPath.timeWaitingMillis()).isEqualTo(expectedTimeWaitingMillis)
    }

    @Test
    fun `route with scheduled stop at stop{i} but no corresponding arrival at stop{i+1} should throw an illegal state exception`
                (): Unit = with(scheduledNetworkIllegalState()) {
        // Act & Assert
        assertFailsWith<IllegalStateException> {
            target.getShortestPathByTime(0, 4, desiredDeparture)
        }
    }

    private fun scheduledNetwork(): TimedNetworkTestData {
        // Arrange
        val target = ScheduledRouteNetwork()

        val desiredDeparture = LocalTime("12:10")

        target.addScheduledRoute(
            "A",
            RouteType.Unidirectional,
            linkedSetOf(0, 1, 2),
            listOf(2, 2),
            listOf(
                listOf(LocalTime("12:30")),
                listOf(LocalTime("13:30")),
                listOf(LocalTime("14:30"))
            )
        )

        target.addScheduledRoute(
            "B",
            RouteType.Unidirectional,
            linkedSetOf(2, 3, 4),
            listOf(2, 2),
            listOf(
                listOf(LocalTime("16:30")),
                listOf(LocalTime("17:30")),
                listOf(LocalTime("18:30"))
            )
        )

        target.addScheduledRoute(
            "C",
            RouteType.Unidirectional,
            linkedSetOf(2, 3, 4),
            listOf(2, 2),
            listOf(
                listOf(LocalTime("15:30")),
                listOf(LocalTime("16:30")),
                listOf(LocalTime("17:30"))
            )
        )

        val expectedLightestPathByTime = ScheduledPath(
            listOf(
                ScheduledPathSegment("A", listOf(0, 1, 2), 4, LocalTime("12:30"), LocalTime("14:30")),
                ScheduledPathSegment("C", listOf(2, 3, 4), 4, LocalTime("15:30"), LocalTime("17:30"))
            )
        )

        val expectedTotalDurationMillis = Duration.ofHours(5).toMillis()

        val expectedTimeWaitingMillis = Duration.ofHours(1).toMillis()

        return TimedNetworkTestData(
            target,
            desiredDeparture,
            expectedLightestPathByTime,
            expectedTotalDurationMillis,
            expectedTimeWaitingMillis
        )
    }

    private fun scheduledNetworkIllegalState(): TimedNetworkIllegalStateTestData {
        // Arrange
        val target = ScheduledRouteNetwork()

        val desiredDeparture = LocalTime("15:30")

        target.addScheduledRoute(
            "A",
            RouteType.Unidirectional,
            linkedSetOf(0, 1, 2),
            listOf(2, 2),
            listOf(
                listOf(LocalTime("15:30")),
                listOf(LocalTime("16:30")),
                listOf(LocalTime("16:00"))
            )
        )

        return TimedNetworkIllegalStateTestData(target, desiredDeparture)
    }

    data class TimedNetworkIllegalStateTestData(
        val target: ScheduledRouteNetwork,
        val desiredDeparture: LocalTime,
    )

    data class TimedNetworkTestData(
        val target: ScheduledRouteNetwork,
        val desiredDeparture: LocalTime,
        val expectedLightestPathByTime: ScheduledPath,
        val expectedTotalDurationMillis: Long,
        val expectedTimeWaitingMillis: Long
    )
}