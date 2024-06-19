package org.example.com.rcs.trane

import com.rcs.trane.network.RouteType
import org.example.com.rcs.trane.network.RouteNetwork

fun main() {
    val routeNetwork = RouteNetwork()

    routeNetwork.addRoute("A", RouteType.BidirectionalCircular, linkedSetOf(0, 1, 2), listOf(1, 1, 1))
    routeNetwork.addRoute("B", RouteType.Unidirectional, linkedSetOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13), listOf(1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1))
    routeNetwork.addRoute("C", RouteType.BidirectionalCircular, linkedSetOf(2, 3, 4), listOf(1, 1, 1))
    routeNetwork.addRoute("D", RouteType.BidirectionalCircular, linkedSetOf(3, 4, 5), listOf(1, 1, 1))
    routeNetwork.addRoute("E", RouteType.BidirectionalCircular, linkedSetOf(5, 6, 7, 8, 9), listOf(1, 1, 1, 1, 1))
    routeNetwork.addRoute("F", RouteType.BidirectionalCircular, linkedSetOf(9, 10, 11, 12, 13), listOf(1, 1, 1, 1, 1))
    routeNetwork.addRoute("G", RouteType.BidirectionalCircular, linkedSetOf(3, 13), listOf(1, 1))
    routeNetwork.addRoute("H", RouteType.BidirectionalCircular, linkedSetOf(19, 20), listOf(1, 1))

    println("Path with least stops: " + routeNetwork.getShortestPathByStops(0, 13))
    println("Path with least routes: " + routeNetwork.getShortestPathByRoutes(0, 13))
    println("Path with shortest distance: " + routeNetwork.getShortestPathByDistance(0, 13))
}