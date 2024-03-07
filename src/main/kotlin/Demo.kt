package org.example

import com.rcs.trane.network.RouteType
import org.example.com.rcs.trane.network.RouteNetwork

fun main() {
    val routeNetwork = RouteNetwork()

    routeNetwork.addRoute("A", RouteType.BIDIRECTIONAL_CIRCULAR, linkedSetOf(0, 1, 2), listOf(1, 1, 1))
    routeNetwork.addRoute("B", RouteType.UNIDIRECTIONAL, linkedSetOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13), listOf(1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1))
    routeNetwork.addRoute("C", RouteType.BIDIRECTIONAL_CIRCULAR, linkedSetOf(2, 3, 4), listOf(1, 1, 1))
    routeNetwork.addRoute("D", RouteType.BIDIRECTIONAL_CIRCULAR, linkedSetOf(3, 4, 5), listOf(1, 1, 1))
    routeNetwork.addRoute("E", RouteType.BIDIRECTIONAL_CIRCULAR, linkedSetOf(5, 6, 7, 8, 9), listOf(1, 1, 1, 1, 1))
    routeNetwork.addRoute("F", RouteType.BIDIRECTIONAL_CIRCULAR, linkedSetOf(9, 10, 11, 12, 13), listOf(1, 1, 1, 1, 1))
    routeNetwork.addRoute("G", RouteType.BIDIRECTIONAL_CIRCULAR, linkedSetOf(3, 13), listOf(1, 1))
    routeNetwork.addRoute("H", RouteType.BIDIRECTIONAL_CIRCULAR, linkedSetOf(19, 20), listOf(1, 1))

    println("Path with least stops: " + routeNetwork.getShortestPathByStops(0, 13))
    println("Path with least routes: " + routeNetwork.getShortestPathByRoutes(0, 13))
    println("Path with shortest distance: " + routeNetwork.getShortestPathByDistance(0, 13))
}