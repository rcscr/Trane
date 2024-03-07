package com.rcs.trane.graph

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class NumericallyWeightedUnidirectionalGraphTest {

    @Test
    fun `getting lightest path (considers the weight)`() {
        // Arrange
        val target = NumericallyWeightedUnidirectionalGraph<String, Int>()
        target.addNode("A")
        target.addNode("B")
        target.addNode("C")
        target.addEdge("A", "B", 1)
        target.addEdge("B", "C", 1)
        target.addEdge("A", "C", 10)

        // Act
        val lightestPath = target.getLightestPath("A", "C")!!

        // Assert
        assertThat(lightestPath.path).containsExactly("A", "B", "C")
        assertThat(lightestPath.weight).isEqualTo(2)
    }

    @Test
    fun `getting shortest path (considers the number of nodes)`() {
        // Arrange
        val target = NumericallyWeightedUnidirectionalGraph<String, Int>()
        target.addNode("A")
        target.addNode("B")
        target.addNode("C")
        target.addEdge("A", "B", 1)
        target.addEdge("B", "C", 1)
        target.addEdge("A", "C", 10)

        // Act
        val shortestPath = target.getShortestPathWithWeight("A", "C")!!

        // Assert
        assertThat(shortestPath.path).containsExactly("A", "C")
        assertThat(shortestPath.weight).isEqualTo(10)
    }
}