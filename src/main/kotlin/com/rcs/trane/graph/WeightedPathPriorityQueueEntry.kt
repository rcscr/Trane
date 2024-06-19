package com.rcs.trane.graph

import java.util.*
import kotlin.Comparator

internal data class WeightedPathPriorityQueueEntry<K, W>(
    val path: SequencedSet<K>,
    val weight: W,
    private val comparator: Comparator<W>
): Comparable<WeightedPathPriorityQueueEntry<K, W>> {

    override fun compareTo(other: WeightedPathPriorityQueueEntry<K, W>): Int {
        return comparator.compare(weight, other.weight)
    }
}