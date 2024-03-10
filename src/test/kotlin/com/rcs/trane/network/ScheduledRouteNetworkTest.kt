package com.rcs.trane.network

import org.assertj.core.api.Assertions.assertThat
import org.example.com.rcs.trane.network.LocalTime
import org.example.com.rcs.trane.network.ScheduledPath
import org.example.com.rcs.trane.network.ScheduledPathSegment
import org.example.com.rcs.trane.network.ScheduledRouteNetwork
import org.junit.jupiter.api.Test
import java.time.Duration
import java.time.Instant
import java.time.temporal.ChronoUnit

class ScheduledRouteNetworkTest {

    @Test
    fun `correctly calculates the lightest path by duration`(): Unit = with(happyPathScenario()) {
        // Act
        val scheduledPath = target.getShortestPathByTime(start, stop, desiredDeparture)!!

        // Assert
        assertThat(scheduledPath).isEqualTo(expectedLightestPathByTime)
        assertThat(scheduledPath.totalDurationMillis()).isEqualTo(expectedTotalDurationMillis)
        assertThat(scheduledPath.timeWaitingBetweenMillis()).isEqualTo(expectedTimeWaitingMillis)
    }

    @Test
    fun `if there are no transportation options today, finds the soonest option tomorrow`(): Unit = with(missedTrainScenario()) {
        // Act
        val scheduledPath = target.getShortestPathByTime(start, stop, desiredDeparture)!!

        // Assert
        assertThat(scheduledPath).isEqualTo(expectedLightestPathByTime)
        assertThat(scheduledPath.totalDurationMillis()).isEqualTo(expectedTotalDurationMillis)
        assertThat(scheduledPath.timeWaitingBetweenMillis()).isEqualTo(expectedTimeWaitingMillis)
    }

    private fun happyPathScenario(): TimedNetworkTestData {
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
                ScheduledPathSegment("A", listOf(0, 1, 2), 4, LocalTime("12:30").toInstantToday(), LocalTime("14:30").toInstantToday()),
                ScheduledPathSegment("C", listOf(2, 3, 4), 4, LocalTime("15:30").toInstantToday(), LocalTime("17:30").toInstantToday())
            )
        )

        val expectedTotalDurationMillis = Duration.ofHours(5).toMillis()

        val expectedTimeWaitingMillis = Duration.ofHours(1).toMillis()

        return TimedNetworkTestData(
            target,
            0,
            4,
            desiredDeparture,
            expectedLightestPathByTime,
            expectedTotalDurationMillis,
            expectedTimeWaitingMillis
        )
    }

    private fun missedTrainScenario(): TimedNetworkTestData {
        // Arrange
        val target = ScheduledRouteNetwork()

        val desiredDeparture = LocalTime("12:40")

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

        val expectedLightestPathByTime = ScheduledPath(
            listOf(
                ScheduledPathSegment("A", listOf(0, 1, 2), 4, tomorrow("12:30"), tomorrow("14:30")),
                ScheduledPathSegment("B", listOf(2, 3, 4), 4, tomorrow("16:30"), tomorrow("18:30"))
            )
        )

        val expectedTotalDurationMillis = Duration.ofHours(6).toMillis()

        val expectedTimeWaitingMillis = Duration.ofHours(2).toMillis()

        return TimedNetworkTestData(
            target,
            0,
            4,
            desiredDeparture,
            expectedLightestPathByTime,
            expectedTotalDurationMillis,
            expectedTimeWaitingMillis
        )
    }

    data class TimedNetworkTestData(
        val target: ScheduledRouteNetwork,
        val start: Int,
        val stop: Int,
        val desiredDeparture: LocalTime,
        val expectedLightestPathByTime: ScheduledPath,
        val expectedTotalDurationMillis: Long,
        val expectedTimeWaitingMillis: Long
    )

    private fun tomorrow(localTime: String): Instant {
        return LocalTime(localTime).toInstantToday().plus(1, ChronoUnit.DAYS)
    }
}