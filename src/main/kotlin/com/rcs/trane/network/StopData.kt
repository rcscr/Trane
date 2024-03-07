package com.rcs.trane.network

data class StopData(val distances: MutableMap<Int, Int>, val routes: MutableMap<Int, MutableSet<String>>)