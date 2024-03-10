package com.rcs.trane.network

import java.time.LocalTime

data class StopData(
    // distances[S] is the distance between this stop and stop S
    val distances: MutableMap<Int, Int>,
    // routes[S] is a list of all routes connecting this stop to stop S
    val routes: MutableMap<Int, MutableSet<String>>,
    // times[R] is a list of times that route R comes to this stop, repeated every day
    val times: MutableMap<String, List<LocalTime>>? = null
)