package com.rcs.trane.graph

/**
 * This class is not used by Trane.
 * it has been included here only as a proof of concept.
 */
class BidirectionalPropertyGraph<K, V>: UnidirectionalPropertyGraph<K, V>() {

    override fun addEdge(keyA: K, keyB: K) {
        super.addEdge(keyA, keyB)
        super.addEdge(keyB, keyA)
    }

    override fun removeEdge(keyA: K, keyB: K) {
        super.removeEdge(keyA, keyB)
        super.removeEdge(keyB, keyA)
    }
}