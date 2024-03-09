package com.rcs.trane.network

import org.assertj.core.api.Assertions.assertThat
import org.example.com.rcs.trane.network.TimedPath
import org.example.com.rcs.trane.network.TimedPathSegment
import org.example.com.rcs.trane.network.TimedRouteNetwork
import org.junit.jupiter.api.Test
import java.time.Duration
import java.time.Instant
import java.time.temporal.ChronoUnit
import kotlin.test.assertFailsWith

class TimedRouteNetworkTest {

    @Test
    fun `correctly calculates the lightest path by duration`(): Unit = with(timedNetwork()) {
        // Act
        val timedPath = target.getShortestPathByTime(0, 4, desiredDeparture)!!

        // Assert
        assertThat(timedPath).isEqualTo(expectedLightestPathByTime)
        assertThat(timedPath.totalDurationMillis()).isEqualTo(expectedTotalDurationMillis)
        assertThat(timedPath.timeWaitingMillis()).isEqualTo(expectedTimeWaitingMillis)
    }

    @Test
    fun `test illegal state exception`(): Unit = with(timedNetworkIllegalState()) {
        // Act & Assert
        assertFailsWith<IllegalStateException> {
            target.getShortestPathByTime(0, 4, desiredDeparture)
        }
    }

    private fun timedNetwork(): TimedNetworkTestData {
        // Arrange
        val target = TimedRouteNetwork()

        val now = Instant.now()

        target.addRouteWithTimetable(
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

        target.addRouteWithTimetable(
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

        target.addRouteWithTimetable(
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

        val expectedLightestPathByTime = TimedPath(
            listOf(
                TimedPathSegment("A", listOf(0, 1, 2), 4, now.plus(1, ChronoUnit.HOURS), now.plus(3, ChronoUnit.HOURS)),
                TimedPathSegment("C", listOf(2, 3, 4), 4, now.plus(4, ChronoUnit.HOURS), now.plus(6, ChronoUnit.HOURS))
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

    private fun timedNetworkIllegalState(): TimedNetworkIllegalStateTestData {
        // Arrange
        val target = TimedRouteNetwork()

        val now = Instant.now()

        target.addRouteWithTimetable(
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
        val target: TimedRouteNetwork,
        val desiredDeparture: Instant,
    )

    data class TimedNetworkTestData(
        val target: TimedRouteNetwork,
        val desiredDeparture: Instant,
        val expectedLightestPathByTime: TimedPath,
        val expectedTotalDurationMillis: Long,
        val expectedTimeWaitingMillis: Long
    )
}