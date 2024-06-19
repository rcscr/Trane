package com.rcs.trane.graph

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import kotlin.test.assertFailsWith

class UnidirectionalPropertyGraphTest {

    @Test
    fun `adding a node`() {
        // Arrange
        val target = UnidirectionalPropertyGraph<Int, Unit>()

        // Act
        target.addNode(1, Unit)

        // Assert
        assertThat(target.contains(1)).isTrue()
    }

    @Test
    fun `adding a node with connections`() {
        // Arrange
        val target = UnidirectionalPropertyGraph<Int, Unit>()
        target.addNode(2, Unit)
        target.addNode(3, Unit)

        // Act
        target.addNode(1, Unit, linkedSetOf(2, 3))

        // Assert
        assertThat(target.contains(1)).isTrue()
        assertThat(target.getConnections(1)).containsExactly(2, 3)
    }

    @Test
    fun `getting transient connections`() {
        // Arrange
        val target = UnidirectionalPropertyGraph<Int, Unit>()
        target.addNode(1, Unit)
        target.addNode(2, Unit)
        target.addNode(3, Unit)
        target.addNode(4, Unit)
        target.addNode(5, Unit)
        target.addNode(6, Unit)

        target.addEdge(1, 2)
        target.addEdge(2, 3)
        target.addEdge(3, 4)
        target.addEdge(5, 6)

        // Act
        val result = target.getTransientConnections(1)

        // Assert
        assertThat(result).containsExactlyInAnyOrder(2, 3, 4)
    }

    @Test
    fun `adding a node with connections that don't exist`() {
        // Arrange
        val target = UnidirectionalPropertyGraph<Int, Unit>()
        target.addNode(2, Unit)

        // Act & Assert
        val exception = assertFailsWith<NoSuchElementException> {
            target.addNode(1, Unit, linkedSetOf(2, 3))
        }
        assertThat(exception.message).doesNotContain("2")
        assertThat(exception.message).contains("3")
    }

    @Test
    fun `adding a node with a value`() {
        // Arrange
        val target = UnidirectionalPropertyGraph<Int, Int>()
        target.addNode(1, 123)

        // Act
        val value = target.getValue(1)

        // Assert
        assertThat(value).isEqualTo(123)
    }

    @Test
    fun `testing adding an edge`() {
        // Arrange
        val target = UnidirectionalPropertyGraph<Int, Unit>()
        target.addNode(1, Unit)
        target.addNode(2, Unit)

        // Act
        target.addEdge(1, 2)

        // Assert
        assertThat(target.getConnections(1)).containsOnly(2)
        assertThat(target.getConnections(2)).isEmpty()
    }

    @Test
    fun `adding an edge between nodes that do not exist`() {
        // Arrange
        val target = UnidirectionalPropertyGraph<Int, Unit>()
        target.addNode(1, Unit)

        // Act & Assert
        val exception = assertFailsWith<NoSuchElementException> { target.addEdge(1, 3) }
        assertThat(exception.message).contains("3")
    }

    @Test
    fun `adding an edge to the same node`() {
        // Arrange
        val target = UnidirectionalPropertyGraph<Int, Unit>()
        target.addNode(1, Unit)

        // Act & Assert
        assertFailsWith<IllegalArgumentException> { target.addEdge(1, 1) }
    }

    @Test
    fun `removing an edge between two nodes`() {
        // Arrange
        val target = UnidirectionalPropertyGraph<Int, Unit>()
        target.addNode(1, Unit)
        target.addNode(2, Unit)
        target.addEdge(1, 2)
        target.addEdge(2, 1)

        // Act
        target.removeEdge(1, 2)

        // Assert
        assertThat(target.getConnections(1)).isEmpty()
        assertThat(target.getConnections(2)).containsOnly(1)
    }

    @Test
    fun `removing a node and all its connections`() {
        // Arrange
        val target = UnidirectionalPropertyGraph<Int, Unit>()
        target.addNode(1, Unit)
        target.addNode(2, Unit)
        target.addEdge(1, 2)
        target.addEdge(2, 1)

        // Act
        val removed = target.removeNodeAndConnections(2)

        // Assert
        assertThat(removed).isTrue()
        assertThat(target.contains(2)).isFalse()
        assertThat(target.getConnections(1)).isEmpty()
    }

    @Test
    fun `removing a node that does not exist`() {
        // Arrange
        val target = UnidirectionalPropertyGraph<Int, Unit>()
        target.addNode(1, Unit)
        target.addNode(2, Unit)

        // Act
        val removed = target.removeNodeAndConnections(3)

        // Assert
        assertThat(removed).isFalse()
    }

    @Test
    fun `getting degree of separation between two nodes`() {
        // Arrange
        val target = UnidirectionalPropertyGraph<Int, Unit>()
        target.addNode(1, Unit)
        target.addNode(2, Unit)
        target.addNode(3, Unit)
        target.addEdge(1, 2)
        target.addEdge(2, 3)

        // Act
        val degree = target.getDegreeOfSeparation(1, 3)

        // Assert
        assertThat(degree).isEqualTo(2)
    }

    @Test
    fun `getting degree of separation will consider the shortest path`() {
        // Arrange
        val target = UnidirectionalPropertyGraph<Int, Unit>()
        target.addNode(1, Unit)
        target.addNode(2, Unit)
        target.addNode(3, Unit)
        target.addEdge(1, 2)
        target.addEdge(2, 3)
        target.addEdge(1, 3)

        // Act
        val degree = target.getDegreeOfSeparation(1, 3)

        // Assert
        assertThat(degree).isEqualTo(1)
    }

    @Test
    fun `getting all paths`() {
        // Arrange
        val target = UnidirectionalPropertyGraph<Int, Unit>()
        target.addNode(1, Unit)
        target.addNode(2, Unit)
        target.addNode(3, Unit)
        target.addEdge(1, 2)
        target.addEdge(2, 3)
        target.addEdge(1, 3)

        // Act
        val paths = target.getAllPaths(1, 3)

        // Assert
        assertThat(paths).containsOnly(linkedSetOf(1, 2, 3), linkedSetOf(1, 3))
    }

    @Test
    fun `getting all paths when none exist`() {
        // Arrange
        val target = UnidirectionalPropertyGraph<Int, Unit>()
        target.addNode(1, Unit)
        target.addNode(2, Unit)
        target.addNode(3, Unit)

        // Act
        val paths = target.getAllPaths(1, 3)

        // Assert
        assertThat(paths).isEmpty()
    }

    @Test
    fun `getting shortest path`() {
        // Arrange
        val target = UnidirectionalPropertyGraph<Int, Unit>()
        target.addNode(1, Unit)
        target.addNode(2, Unit)
        target.addNode(3, Unit)
        target.addEdge(1, 2)
        target.addEdge(2, 3)
        target.addEdge(1, 3)

        // Act
        val path = target.getShortestPath(1, 3)!!

        // Assert
        assertThat(path).containsExactly(1, 3)
    }

    @Test
    fun `getting shortest path when none exist`() {
        // Arrange
        val target = UnidirectionalPropertyGraph<Int, Unit>()
        target.addNode(1, Unit)
        target.addNode(2, Unit)
        target.addNode(3, Unit)

        // Act
        val path = target.getShortestPath(1, 3)

        // Assert
        assertThat(path).isNull()
    }

    @Test
    fun `get shortest path with included dynamically computed weight`() {
        // Arrange
        val target = UnidirectionalPropertyGraph<Int, Unit>()
        target.addNode(1, Unit)
        target.addNode(2, Unit)
        target.addNode(3, Unit)
        target.addEdge(1, 2)
        target.addEdge(2, 3)

        // Act
        val path = target.getShortestPathWithWeight(1, 3, { weight, _, _ -> weight + 1 }, 0)!!

        // Assert
        assertThat(path.path).containsExactly(1, 2, 3)
        assertThat(path.weight).isEqualTo(2)
    }

    @Test
    fun `get lightest path by dynamically computed weight (simple)`() {
        // Arrange
        val target = UnidirectionalPropertyGraph<Int, Map<Int, Int>>()
        target.addNode(1, mapOf(2 to 2, 3 to 10))
        target.addNode(2, mapOf(3 to 2))
        target.addNode(3, mapOf())
        target.addEdge(1, 2)
        target.addEdge(2, 3)
        target.addEdge(1, 3)

        // Act
        val path = target.getLightestPathSimple(
            1,
            3,
            { weight, nodeA, nodeB -> weight + target.getValue(nodeA)!![nodeB]!! },
            { weightA, weightB -> weightA.compareTo(weightB) },
            0
        )!!

        // Assert
        assertThat(path.path).containsExactly(1, 2, 3)
        assertThat(path.weight).isEqualTo(4)
    }

    @Test
    fun `get lightest path by dynamically computed weight (complex) (one route)`() {
        // Arrange
        val target = UnidirectionalPropertyGraph<Int, Map<Int, Set<String>>>()
        target.addNode(1, mapOf(2 to setOf("A"), 5 to setOf("F")))
        target.addNode(2, mapOf(3 to setOf("A", "C")))
        target.addNode(3, mapOf(4 to setOf("B", "D")))
        target.addNode(4, mapOf(5 to setOf("B", "E")))
        target.addNode(5, mapOf())
        target.addEdge(1, 2)
        target.addEdge(2, 3)
        target.addEdge(3, 4)
        target.addEdge(4, 5)
        target.addEdge(1, 5)

        // Act
        val path = target.getLightestPathComplex<Set<String>>(
            1,
            5,
            { weight, nodeA, nodeB -> target.getValue(nodeA)!![nodeB]!!.map { weight + it } },
            { weightA, weightB -> weightA.size.compareTo(weightB.size) },
            setOf()
        )!!

        // Assert
        assertThat(path.path).containsExactly(1, 5)
        assertThat(path.weight).isEqualTo(linkedSetOf("F"))
    }

    @Test
    fun `get lightest path by dynamically computed weight (complex) (two routes)`() {
        // Arrange
        val target = UnidirectionalPropertyGraph<Int, Map<Int, Set<String>>>()
        target.addNode(1, mapOf(2 to setOf("A")))
        target.addNode(2, mapOf(3 to setOf("A", "C")))
        target.addNode(3, mapOf(4 to setOf("B", "D")))
        target.addNode(4, mapOf(5 to setOf("B", "E")))
        target.addNode(5, mapOf())
        target.addEdge(1, 2)
        target.addEdge(2, 3)
        target.addEdge(3, 4)
        target.addEdge(4, 5)

        // Act
        val path = target.getLightestPathComplex<Set<String>>(
            1,
            5,
            { weight, nodeA, nodeB -> target.getValue(nodeA)!![nodeB]!!.map { weight + it } },
            { weightA, weightB -> weightA.size.compareTo(weightB.size) },
            setOf()
        )!!

        // Assert
        assertThat(path.path).containsExactly(1, 2, 3, 4, 5)
        assertThat(path.weight).isEqualTo(linkedSetOf("A", "B"))
    }
}