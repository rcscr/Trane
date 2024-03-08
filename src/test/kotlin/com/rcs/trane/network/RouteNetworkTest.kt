package com.rcs.trane.network

import org.assertj.core.api.Assertions.assertThat
import org.example.com.rcs.trane.network.RouteNetwork
import org.junit.jupiter.api.Test

class RouteNetworkTest {

    @Test
    fun `adding a UNIDIRECTIONAL route`() {
        // Arrange
        val target = RouteNetwork()

        // Act
        target.addRoute("A", RouteType.UNIDIRECTIONAL, linkedSetOf(1, 2, 3), listOf(10, 20))

        // Assert
        assertThat(target.graph.getNodes()).containsOnly(1, 2, 3)

        assertThat(target.graph.getConnections(1)).containsOnly(2)
        assertThat(target.graph.getConnections(2)).containsOnly(3)
        assertThat(target.graph.getConnections(3)).isEmpty()

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
                mutableMapOf(),
                mutableMapOf()))
    }

    @Test
    fun `adding a UNIDIRECTIONAL_CIRCULAR route`() {
        // Arrange
        val target = RouteNetwork()

        // Act
        target.addRoute("A", RouteType.UNIDIRECTIONAL_CIRCULAR, linkedSetOf(1, 2, 3), listOf(10, 20, 30))

        // Assert
        assertThat(target.graph.getNodes()).containsOnly(1, 2, 3)

        assertThat(target.graph.getConnections(1)).containsOnly(2)
        assertThat(target.graph.getConnections(2)).containsOnly(3)
        assertThat(target.graph.getConnections(3)).containsOnly(1)

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
                mutableMapOf(1 to 30),
                mutableMapOf(1 to mutableSetOf("A"))))
    }

    @Test
    fun `adding a BIDIRECTIONAL route`() {
        // Arrange
        val target = RouteNetwork()

        // Act
        target.addRoute("A", RouteType.BIDIRECTIONAL, linkedSetOf(1, 2, 3), listOf(10, 20))

        // Assert
        assertThat(target.graph.getNodes()).containsOnly(1, 2, 3)

        assertThat(target.graph.getConnections(1)).containsOnly(2)
        assertThat(target.graph.getConnections(2)).containsOnly(1, 3)
        assertThat(target.graph.getConnections(3)).containsOnly(2)

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
                mutableMapOf(2 to 20),
                mutableMapOf(2 to mutableSetOf("A"))))
    }

    @Test
    fun `adding a BIDIRECTIONAL_CIRCULAR route`() {
        // Arrange
        val target = RouteNetwork()

        // Act
        target.addRoute("A", RouteType.BIDIRECTIONAL_CIRCULAR, linkedSetOf(1, 2, 3), listOf(10, 20, 30))

        // Assert
        assertThat(target.graph.getNodes()).containsOnly(1, 2, 3)

        assertThat(target.graph.getConnections(1)).containsOnly(2, 3)
        assertThat(target.graph.getConnections(2)).containsOnly(1, 3)
        assertThat(target.graph.getConnections(3)).containsOnly(2, 1)

        assertThat(target.graph.getValue(1))
            .isEqualTo(StopData(
                mutableMapOf(2 to 10, 3 to 30),
                mutableMapOf(2 to mutableSetOf("A"), 3 to mutableSetOf("A"))))

        assertThat(target.graph.getValue(2))
            .isEqualTo(StopData(
                mutableMapOf(3 to 20, 1 to 10),
                mutableMapOf(3 to mutableSetOf("A"), 1 to mutableSetOf("A"))))

        assertThat(target.graph.getValue(3))
            .isEqualTo(StopData(
                mutableMapOf(2 to 20, 1 to 30),
                mutableMapOf(1 to mutableSetOf("A"), 2 to mutableSetOf("A"))))
    }
}