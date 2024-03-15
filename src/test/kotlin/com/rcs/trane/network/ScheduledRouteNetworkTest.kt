package com.rcs.trane.network

import org.assertj.core.api.Assertions.assertThat
import org.example.com.rcs.trane.network.ScheduledPath
import org.example.com.rcs.trane.network.ScheduledPathSegment
import org.example.com.rcs.trane.network.ScheduledRouteNetwork
import org.junit.jupiter.api.Test
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
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
    fun `correctly calculates the lightest path by duration considering delays`(): Unit = with(happyPathWithDelayScenario()) {
        // Act
        val scheduledPath = target.getShortestPathByTime(start, stop, desiredDeparture)!!

        // Assert
        assertThat(scheduledPath).isEqualTo(expectedLightestPathByTime)
        assertThat(scheduledPath.totalDurationMillis()).isEqualTo(expectedTotalDurationMillis)
        assertThat(scheduledPath.timeWaitingBetweenMillis()).isEqualTo(expectedTimeWaitingMillis)
        assertThat(scheduledPath.totalDelayMillis()).isEqualTo(expectedDelayMillis)
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

        val desiredDeparture = LocalTime.parse("12:10").atDate(LocalDate.now())

        target.addScheduledRoute(
            "A",
            RouteType.Unidirectional,
            linkedSetOf(0, 1, 2),
            listOf(2, 2),
            listOf(
                listOf(LocalTime.parse("12:30")),
                listOf(LocalTime.parse("13:30")),
                listOf(LocalTime.parse("14:30"))
            )
        )

        target.addScheduledRoute(
            "B",
            RouteType.Unidirectional,
            linkedSetOf(2, 3, 4),
            listOf(2, 2),
            listOf(
                listOf(LocalTime.parse("16:30")),
                listOf(LocalTime.parse("17:30")),
                listOf(LocalTime.parse("18:30"))
            )
        )

        target.addScheduledRoute(
            "C",
            RouteType.Unidirectional,
            linkedSetOf(2, 3, 4),
            listOf(2, 2),
            listOf(
                listOf(LocalTime.parse("15:30")),
                listOf(LocalTime.parse("16:30")),
                listOf(LocalTime.parse("17:30"))
            )
        )

        val expectedLightestPathByTime = ScheduledPath(
            listOf(
                ScheduledPathSegment("A", listOf(0, 1, 2), 4, LocalTime.parse("12:30").atDate(LocalDate.now()), LocalTime.parse("14:30").atDate(LocalDate.now()), 0),
                ScheduledPathSegment("C", listOf(2, 3, 4), 4, LocalTime.parse("15:30").atDate(LocalDate.now()), LocalTime.parse("17:30").atDate(LocalDate.now()), 0)
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

    private fun happyPathWithDelayScenario(): TimedNetworkTestData = with(happyPathScenario()) {
        val expectedDelayMillis = Duration.ofMinutes(15).toMillis()
        val expectedTotalDurationMillis = Duration.ofHours(6).plusMillis(expectedDelayMillis).toMillis()
        val expectedTimeWaitingMillis = Duration.ofHours(2).plusMillis(expectedDelayMillis).toMillis()

        // this delay to route B only serves to add a delay to the computed path
        target.addDelay("B", 0, desiredDeparture.toLocalDate(), expectedDelayMillis)

        // this delay to route C should force it to no longer be the best route
        target.addDelay("C", 0, desiredDeparture.toLocalDate(), Duration.ofHours(2).toMillis())

        val expectedLightestPathByTime = ScheduledPath(
            listOf(
                ScheduledPathSegment("A", listOf(0, 1, 2), 4, LocalTime.parse("12:30").atDate(LocalDate.now()), LocalTime.parse("14:30").atDate(LocalDate.now()), 0),
                ScheduledPathSegment("B", listOf(2, 3, 4), 4, LocalTime.parse("16:45").atDate(LocalDate.now()), LocalTime.parse("18:45").atDate(LocalDate.now()), expectedDelayMillis)
            )
        )

        return TimedNetworkTestData(
            target,
            0,
            4,
            desiredDeparture,
            expectedLightestPathByTime,
            expectedTotalDurationMillis,
            expectedTimeWaitingMillis,
            expectedDelayMillis
        )
    }

    private fun missedTrainScenario(): TimedNetworkTestData {
        // Arrange
        val target = ScheduledRouteNetwork()

        val desiredDeparture = LocalTime.parse("12:40").atDate(LocalDate.now())

        target.addScheduledRoute(
            "A",
            RouteType.Unidirectional,
            linkedSetOf(0, 1, 2),
            listOf(2, 2),
            listOf(
                listOf(LocalTime.parse("12:30")),
                listOf(LocalTime.parse("13:30")),
                listOf(LocalTime.parse("14:30"))
            )
        )

        target.addScheduledRoute(
            "B",
            RouteType.Unidirectional,
            linkedSetOf(2, 3, 4),
            listOf(2, 2),
            listOf(
                listOf(LocalTime.parse("16:30")),
                listOf(LocalTime.parse("17:30")),
                listOf(LocalTime.parse("18:30"))
            )
        )

        val expectedLightestPathByTime = ScheduledPath(
            listOf(
                ScheduledPathSegment("A", listOf(0, 1, 2), 4, tomorrow("12:30"), tomorrow("14:30"), 0),
                ScheduledPathSegment("B", listOf(2, 3, 4), 4, tomorrow("16:30"), tomorrow("18:30"), 0)
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
        val desiredDeparture: LocalDateTime,
        val expectedLightestPathByTime: ScheduledPath,
        val expectedTotalDurationMillis: Long,
        val expectedTimeWaitingMillis: Long,
        val expectedDelayMillis: Long? = null
    )

    private fun tomorrow(localTime: String): LocalDateTime {
        return LocalTime.parse(localTime).atDate(LocalDate.now()).plus(1, ChronoUnit.DAYS)
    }
}