package com.rcs.trane.graph

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class BidirectionalPropertyGraphTest {

    @ParameterizedTest
    @CsvSource("1, 2", "2, 1")
    fun `adding an edge creates two edges in both directions`(nodeA: Int, nodeB: Int) {
        // Arrange
        val target = BidirectionalPropertyGraph<Int, Unit>()

        // Act
        target.addNode(nodeA, Unit)
        target.addNode(nodeB, Unit)
        target.addEdge(nodeA, nodeB)

        // Assert
        assertThat(target.getConnections(nodeA)).containsOnly(nodeB)
        assertThat(target.getConnections(nodeB)).containsOnly(nodeA)
    }

    @ParameterizedTest
    @CsvSource("1, 2", "2, 1")
    fun `removing an edge removes the two edges in both directions`(nodeA: Int, nodeB: Int) {
        // Arrange
        val target = BidirectionalPropertyGraph<Int, Unit>()
        target.addNode(nodeA, Unit)
        target.addNode(nodeB, Unit)
        target.addEdge(nodeA, nodeB)

        // Act
        target.removeEdge(nodeA, nodeB)

        // Assert
        assertThat(target.getConnections(nodeA)).isEmpty()
        assertThat(target.getConnections(nodeB)).isEmpty()
    }

    @Test
    fun `getting all paths returns paths in both directions`() {
        // Arrange
        val target = BidirectionalPropertyGraph<Int, Unit>()
        target.addNode(1, Unit)
        target.addNode(2, Unit)
        target.addEdge(1, 2)

        // Act
        val paths = target.getAllPaths(1, 2)

        // Assert
        assertThat(paths).containsOnly(linkedSetOf(1, 2), linkedSetOf(2, 1))
    }
}