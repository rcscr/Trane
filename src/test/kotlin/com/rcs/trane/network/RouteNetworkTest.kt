package com.rcs.trane.network

import org.assertj.core.api.Assertions.assertThat
import org.example.com.rcs.trane.network.Path
import org.example.com.rcs.trane.network.RouteNetwork
import org.junit.jupiter.api.Test

class RouteNetworkTest {

    @Test
    fun `adding multiple overlapping Unidirectional routes populates graph correctly`() {
        // Arrange
        val target = RouteNetwork()

        // Act
        target.addRoute("A", RouteType.Unidirectional, linkedSetOf(1, 2, 3), listOf(10, 20))
        target.addRoute("B", RouteType.Unidirectional, linkedSetOf(3, 4, 5), listOf(30, 40))

        // Assert
        assertThat(target.graph.getNodes()).containsOnly(1, 2, 3, 4, 5)

        assertThat(target.graph.getConnections(1)).containsOnly(2)
        assertThat(target.graph.getConnections(2)).containsOnly(3)
        assertThat(target.graph.getConnections(3)).containsOnly(4)
        assertThat(target.graph.getConnections(4)).containsOnly(5)
        assertThat(target.graph.getConnections(5)).isEmpty()

        assertThat(target.graph.getValue(1))
            .isEqualTo(StopData(
                mutableMapOf(2 to 10),
                mutableMapOf(2 to mutableSetOf("A"))))

        assertThat(target.graph.getValue(2))
            .isEqualTo(StopData(
                mutableMapOf(3 to 20),
                mutableMapOf(3 to mutableSetOf("A"))))

        assertThat(target.graph.getValue(3))
            .isEqualTo(StopData(
                mutableMapOf(4 to 30),
                mutableMapOf(4 to mutableSetOf("B"))))

        assertThat(target.graph.getValue(4))
            .isEqualTo(StopData(
                mutableMapOf(5 to 40),
                mutableMapOf(5 to mutableSetOf("B"))))

        assertThat(target.graph.getValue(5))
            .isEqualTo(StopData(
                mutableMapOf(),
                mutableMapOf()))
    }

    @Test
    fun `adding multiple UnidirectionalCircular routes populates graph correctly`() {
        // Arrange
        val target = RouteNetwork()

        // Act
        target.addRoute("A", RouteType.UnidirectionalCircular, linkedSetOf(1, 2, 3), listOf(10, 20, 30))
        target.addRoute("B", RouteType.UnidirectionalCircular, linkedSetOf(3, 4, 5), listOf(40, 50, 60))

        // Assert
        assertThat(target.graph.getNodes()).containsOnly(1, 2, 3, 4, 5)

        assertThat(target.graph.getConnections(1)).containsOnly(2)
        assertThat(target.graph.getConnections(2)).containsOnly(3)
        assertThat(target.graph.getConnections(3)).containsOnly(1, 4)
        assertThat(target.graph.getConnections(4)).containsOnly(5)
        assertThat(target.graph.getConnections(5)).containsOnly(3)

        assertThat(target.graph.getValue(1))
            .isEqualTo(StopData(
                mutableMapOf(2 to 10),
                mutableMapOf(2 to mutableSetOf("A"))))

        assertThat(target.graph.getValue(2))
            .isEqualTo(StopData(
                mutableMapOf(3 to 20),
                mutableMapOf(3 to mutableSetOf("A"))))

        assertThat(target.graph.getValue(3))
            .isEqualTo(StopData(
                mutableMapOf(1 to 30, 4 to 40),
                mutableMapOf(1 to mutableSetOf("A"), 4 to mutableSetOf("B"))))

        assertThat(target.graph.getValue(4))
            .isEqualTo(StopData(
                mutableMapOf(5 to 50),
                mutableMapOf(5 to mutableSetOf("B"))))

        assertThat(target.graph.getValue(5))
            .isEqualTo(StopData(
                mutableMapOf(3 to 60),
                mutableMapOf(3 to mutableSetOf("B"))))
    }

    @Test
    fun `adding multiple Bidirectional routes populates graph correctly`() {
        // Arrange
        val target = RouteNetwork()

        // Act
        target.addRoute("A", RouteType.Bidirectional, linkedSetOf(1, 2, 3), listOf(10, 20))
        target.addRoute("B", RouteType.Bidirectional, linkedSetOf(3, 4, 5), listOf(30, 40))

        // Assert
        assertThat(target.graph.getNodes()).containsOnly(1, 2, 3, 4, 5)

        assertThat(target.graph.getConnections(1)).containsOnly(2)
        assertThat(target.graph.getConnections(2)).containsOnly(1, 3)
        assertThat(target.graph.getConnections(3)).containsOnly(2, 4)
        assertThat(target.graph.getConnections(4)).containsOnly(3, 5)
        assertThat(target.graph.getConnections(5)).containsOnly(4)

        assertThat(target.graph.getValue(1))
            .isEqualTo(StopData(
                mutableMapOf(2 to 10),
                mutableMapOf(2 to mutableSetOf("A"))))

        assertThat(target.graph.getValue(2))
            .isEqualTo(StopData(
                mutableMapOf(1 to 10, 3 to 20),
                mutableMapOf(1 to mutableSetOf("A"), 3 to mutableSetOf("A"))))

        assertThat(target.graph.getValue(3))
            .isEqualTo(StopData(
                mutableMapOf(2 to 20, 4 to 30),
                mutableMapOf(2 to mutableSetOf("A"), 4 to mutableSetOf("B"))))

        assertThat(target.graph.getValue(4))
            .isEqualTo(StopData(
                mutableMapOf(3 to 30, 5 to 40),
                mutableMapOf(5 to mutableSetOf("B"), 3 to mutableSetOf("B"))))

        assertThat(target.graph.getValue(5))
            .isEqualTo(StopData(
                mutableMapOf(4 to 40),
                mutableMapOf(4 to mutableSetOf("B"))))
    }

    @Test
    fun `adding multiple BidirectionalCircular routes populates graph correctly`() {
        // Arrange
        val target = RouteNetwork()

        // Act
        target.addRoute("A", RouteType.BidirectionalCircular, linkedSetOf(1, 2, 3), listOf(10, 20, 30))
        target.addRoute("B", RouteType.BidirectionalCircular, linkedSetOf(3, 4, 5), listOf(40, 50, 60))

        // Assert
        assertThat(target.graph.getNodes()).containsOnly(1, 2, 3, 4, 5)

        assertThat(target.graph.getConnections(1)).containsOnly(2, 3)
        assertThat(target.graph.getConnections(2)).containsOnly(1, 3)
        assertThat(target.graph.getConnections(3)).containsOnly(2, 4, 1, 5)
        assertThat(target.graph.getConnections(4)).containsOnly(3, 5)
        assertThat(target.graph.getConnections(5)).containsOnly(4, 3)

        assertThat(target.graph.getValue(1))
            .isEqualTo(StopData(
                mutableMapOf(2 to 10, 3 to 30),
                mutableMapOf(2 to mutableSetOf("A"), 3 to mutableSetOf("A"))))

        assertThat(target.graph.getValue(2))
            .isEqualTo(StopData(
                mutableMapOf(1 to 10, 3 to 20),
                mutableMapOf(1 to mutableSetOf("A"), 3 to mutableSetOf("A"))))

        assertThat(target.graph.getValue(3))
            .isEqualTo(StopData(
                mutableMapOf(2 to 20, 4 to 40, 5 to 60, 1 to 30),
                mutableMapOf(2 to mutableSetOf("A"), 1 to mutableSetOf("A"), 4 to mutableSetOf("B"), 5 to mutableSetOf("B"))))

        assertThat(target.graph.getValue(4))
            .isEqualTo(StopData(
                mutableMapOf(3 to 40, 5 to 50),
                mutableMapOf(5 to mutableSetOf("B"), 3 to mutableSetOf("B"))))

        assertThat(target.graph.getValue(5))
            .isEqualTo(StopData(
                mutableMapOf(4 to 50, 3 to 60),
                mutableMapOf(4 to mutableSetOf("B"), 3 to mutableSetOf("B"))))
    }

    @Test
    fun `correctly calculates the lightest path by number of routes`(): Unit = with (createCommonNetwork()) {
        // Act
        val path = target.getShortestPathByRoutes(0, 13)!!

        // Assert
        assertThat(path.segments).isEqualTo(expectedShortestPathByNumberOfRoutes.segments)
        assertThat(path.totalDistance).isEqualTo(expectedShortestPathByNumberOfRoutes.totalDistance)
    }

    @Test
    fun `correctly calculates the shortest path by number of stops`(): Unit= with (createCommonNetwork()) {
        // Act
        val path = target.getShortestPathByStops(0, 13)!!

        // Assert
        assertThat(path.segments).isEqualTo(expectedShortestPathByNumberOfStops.segments)
        assertThat(path.totalDistance).isEqualTo(expectedShortestPathByNumberOfStops.totalDistance)
    }

    @Test
    fun `correctly calculates the shortest path by total distance`(): Unit = with (createCommonNetwork()) {
        // Act
        val path = target.getShortestPathByDistance(0, 13)!!

        // Assert
        assertThat(path.segments).isEqualTo(expectedShortestPathByDistance.segments)
        assertThat(path.totalDistance).isEqualTo(expectedShortestPathByDistance.totalDistance)
    }

    /**
     * In this network, there are multiple paths from 0 to 13:
     * one with the least amount of routes, but with the most amount of stops
     * one with the least amount of stops, but with more routes
     * one with the shortest distance, but with the most routes
     */
    private fun createCommonNetwork(): CommonNetworkData {
        // Arrange
        val target = RouteNetwork()

        target.addRoute("A", RouteType.Unidirectional, linkedSetOf(0, 1, 2), listOf(10, 10))
        target.addRoute("B", RouteType.Unidirectional, linkedSetOf(2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13), listOf(10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10))
        target.addRoute("C", RouteType.Unidirectional, linkedSetOf(2, 3, 4), listOf(10, 10))
        target.addRoute("D", RouteType.Unidirectional, linkedSetOf(4, 97, 98), listOf(1, 1))
        target.addRoute("E", RouteType.Unidirectional, linkedSetOf(98, 99, 13), listOf(1, 1))
        target.addRoute("F", RouteType.Unidirectional, linkedSetOf(5, 6, 13), listOf(10, 1))

        // distance: 44; routes: 4; stops: 9
        val expectedShortestPathByDistance = Path(listOf(
            PathSegment("A", listOf(0, 1, 2), 20),
            PathSegment("B", listOf(2, 3, 4), 20),
            PathSegment("D", listOf(4, 97, 98), 2),
            PathSegment("E", listOf(98, 99, 13), 2)),
            44)

        // distance: 71; routes: 3; stops: 8
        val expectedShortestPathByNumberOfStops = Path(listOf(
            PathSegment("A", listOf(0, 1, 2), 20),
            PathSegment("B", listOf(2, 3, 4, 5), 30),
            PathSegment("F", listOf(5, 6, 13), 11)),
            61)

        // distance: 130; routes: 2; stops: 13
        val expectedShortestPathByNumberOfRoutes = Path(listOf(
            PathSegment("A", listOf(0, 1, 2), 20),
            PathSegment("B", listOf(2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13), 110)),
            130)

        return CommonNetworkData(
            target,
            expectedShortestPathByDistance,
            expectedShortestPathByNumberOfStops,
            expectedShortestPathByNumberOfRoutes)
    }

    data class CommonNetworkData(
        val target: RouteNetwork,
        val expectedShortestPathByDistance: Path,
        val expectedShortestPathByNumberOfStops: Path,
        val expectedShortestPathByNumberOfRoutes: Path)
}