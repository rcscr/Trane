package com.rcs.trane.graph

/**
 * This is the traditional weighted-graph, implemented using the generic
 * "weighting" framework established in UnidirectionalPropertyGraph.
 *
 * This class is not used by Trane; it has been included here only as a proof of concept.
 */
class NumericallyWeightedUnidirectionalGraph<K, V>: UnidirectionalPropertyGraph<K, MutableMap<K, V>>()
        where V: Number, V: Comparable<V> {

    private val weightAccumulator = { weight: V, nodeA: K, nodeB: K -> weight + nodes[nodeA]!!.value[nodeB]!! }
    private val weightComparator = { weightA: V, weightB: V -> weightA.compareTo(weightB) }

    fun addNode(item: K) {
        super.addNode(item, mutableMapOf())
    }

    fun addEdge(itemA: K, itemB: K, weight: V) {
        super.addEdge(itemA, itemB)
        nodes[itemA]!!.value[itemB] = weight
    }

    fun getShortestPathWithWeight(start: K, end: K): WeightedPath<K, V>? {
        return super.getShortestPathWithWeight(start, end, weightAccumulator, 0 as V)
    }

    fun getLightestPath(start: K, end: K): WeightedPath<K, V>? {
        return super.getLightestPathSimple(start, end, weightAccumulator, weightComparator, 0 as V)
    }

    operator fun Number.plus(other: Number): V {
        return when (this) {
            is Long -> this.toLong() + other.toLong()
            is Int -> this.toInt() + other.toInt()
            is Short -> this.toShort() + other.toShort()
            is Byte -> this.toByte() + other.toByte()
            is Double -> this.toDouble() + other.toDouble()
            is Float -> this.toFloat() + other.toFloat()
            else -> throw RuntimeException("Unknown numeric type")
        } as V
    }
}