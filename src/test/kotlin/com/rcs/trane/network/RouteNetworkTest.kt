package com.rcs.trane.network

import org.assertj.core.api.Assertions.assertThat
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
}