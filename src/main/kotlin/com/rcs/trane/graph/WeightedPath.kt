package com.rcs.trane.graph

import java.util.*

data class WeightedPath<K, W>(val path: SequencedSet<K>, val weight: W)