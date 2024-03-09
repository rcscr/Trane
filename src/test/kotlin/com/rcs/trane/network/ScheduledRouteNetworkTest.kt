package com.rcs.trane.network

import org.assertj.core.api.Assertions.assertThat
import org.example.com.rcs.trane.network.ScheduledPath
import org.example.com.rcs.trane.network.ScheduledPathSegment
import org.example.com.rcs.trane.network.ScheduledRouteNetwork
import org.junit.jupiter.api.Test
import java.time.Duration
import java.time.Instant
import java.time.temporal.ChronoUnit
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

        val now = Instant.now()

        target.addScheduledRoute(
            "A",
            RouteType.Unidirectional,
            linkedSetOf(0, 1, 2),
            listOf(2, 2),
            listOf(
                listOf(now.plus(1, ChronoUnit.HOURS)),
                listOf(now.plus(2, ChronoUnit.HOURS)),
                listOf(now.plus(3, ChronoUnit.HOURS))
            )
        )

        target.addScheduledRoute(
            "B",
            RouteType.Unidirectional,
            linkedSetOf(2, 3, 4),
            listOf(2, 2),
            listOf(
                listOf(now.plus(4, ChronoUnit.HOURS)),
                listOf(now.plus(6, ChronoUnit.HOURS)),
                listOf(now.plus(8, ChronoUnit.HOURS))
            )
        )

        target.addScheduledRoute(
            "C",
            RouteType.Unidirectional,
            linkedSetOf(2, 3, 4),
            listOf(2, 2),
            listOf(
                listOf(now.plus(4, ChronoUnit.HOURS)),
                listOf(now.plus(5, ChronoUnit.HOURS)),
                listOf(now.plus(6, ChronoUnit.HOURS))
            )
        )

        val expectedLightestPathByTime = ScheduledPath(
            listOf(
                ScheduledPathSegment("A", listOf(0, 1, 2), 4, now.plus(1, ChronoUnit.HOURS), now.plus(3, ChronoUnit.HOURS)),
                ScheduledPathSegment("C", listOf(2, 3, 4), 4, now.plus(4, ChronoUnit.HOURS), now.plus(6, ChronoUnit.HOURS))
            )
        )

        val expectedTotalDurationMillis = Duration.ofHours(5).toMillis()

        val expectedTimeWaitingMillis = Duration.ofHours(1).toMillis()

        return TimedNetworkTestData(
            target,
            now,
            expectedLightestPathByTime,
            expectedTotalDurationMillis,
            expectedTimeWaitingMillis
        )
    }

    private fun scheduledNetworkIllegalState(): TimedNetworkIllegalStateTestData {
        // Arrange
        val target = ScheduledRouteNetwork()

        val now = Instant.now()

        target.addScheduledRoute(
            "A",
            RouteType.Unidirectional,
            linkedSetOf(0, 1, 2),
            listOf(2, 2),
            listOf(
                listOf(now.plus(1, ChronoUnit.HOURS)),
                listOf(now.plus(5, ChronoUnit.HOURS)),
                listOf(now.plus(4, ChronoUnit.HOURS))
            )
        )

        return TimedNetworkIllegalStateTestData(target, now)
    }

    data class TimedNetworkIllegalStateTestData(
        val target: ScheduledRouteNetwork,
        val desiredDeparture: Instant,
    )

    data class TimedNetworkTestData(
        val target: ScheduledRouteNetwork,
        val desiredDeparture: Instant,
        val expectedLightestPathByTime: ScheduledPath,
        val expectedTotalDurationMillis: Long,
        val expectedTimeWaitingMillis: Long
    )
}