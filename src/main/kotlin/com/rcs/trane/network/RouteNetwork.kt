package org.example.com.rcs.trane.network

import com.rcs.trane.graph.UnidirectionalPropertyGraph
import com.rcs.trane.network.PathSegment
import com.rcs.trane.network.RouteType
import com.rcs.trane.network.StopData
import java.util.SequencedSet

class RouteNetwork {

    private val pathBuilder = { path: Path, nodeA: Int, nodeB: Int ->
        val nodeAValue = graph.getValue(nodeA)!!
        nodeAValue.routes[nodeB]!!
            .map {
                val newPathSegment = PathSegment(it, listOf(nodeA, nodeB), nodeAValue.distances[nodeB]!!)
                Path(
                    mergePathSegmentIntoPath(path.segments, newPathSegment),
                    path.totalDistance + nodeAValue.distances[nodeB]!!)
            }
    }

    private val pathByNumberOfRoutes = { pathA: Path, pathB: Path ->
        pathA.numberOfRoutes().compareTo(pathB.numberOfRoutes())
    }

    private val pathByNumberOfStops = { pathA: Path, pathB: Path ->
        pathA.numberOfStops().compareTo(pathB.numberOfStops())
    }

    private val pathByDistance = { pathA: Path, pathB: Path ->
        pathA.totalDistance.compareTo(pathB.totalDistance)
    }

    /**
     * Stops are ID'ed by integers here, but it can be any data type
     */
    internal val graph = UnidirectionalPropertyGraph<Int, StopData>()

    /**
     * distances is a list such that distances[i]
     * corresponds to the distance between stops[i] and stops[i+1]
     */
    fun addRoute(route: String, routeType: RouteType, stops: SequencedSet<Int>, distances: List<Int>) {
        validate(routeType, stops, distances)

        // add nodes
        for (stop in stops) {
            graph.addNode(
                stop,
                graph.getValue(stop) ?: StopData(mutableMapOf(), mutableMapOf()),
                graph.getConnections(stop) ?: mutableSetOf())
        }

        // add edges
        for (i in 0..stops.size-2) {
            val curr = stops.elementAt(i)
            val next = stops.elementAt(i + 1)
            connect(curr, next, route, distances[i])
        }

        // if bi-directional, add edges backwards
        if (routeType == RouteType.Bidirectional || routeType == RouteType.BidirectionalCircular) {
            for (i in (1..<stops.size).reversed()) {
                val curr = stops.elementAt(i)
                val prev = stops.elementAt(i - 1)
                connect(curr, prev, route, distances[i - 1])
            }
        }

        // if circular, connect last to first
        if (routeType == RouteType.UnidirectionalCircular || routeType == RouteType.BidirectionalCircular) {
            connect(stops.last(), stops.first(), route, distances.last())
        }

        // if bi-directional circular, connect first to last
        if (routeType == RouteType.BidirectionalCircular) {
            connect(stops.first(), stops.last(), route, distances.last())
        }
    }

    fun getShortestPathByDistance(start: Int, end: Int): Path? {
        return graph.getLightestPathComplex(start, end, pathBuilder, pathByDistance, Path(listOf(), 0))?.weight
    }

    fun getShortestPathByRoutes(start: Int, end: Int): Path? {
        return graph.getLightestPathComplex(start, end, pathBuilder, pathByNumberOfRoutes, Path(listOf(), 0))?.weight
    }

    fun getShortestPathByStops(start: Int, end: Int): Path? {
        return graph.getLightestPathComplex(start, end, pathBuilder, pathByNumberOfStops, Path(listOf(), 0))?.weight
    }

    private fun mergePathSegmentIntoPath(path: List<PathSegment>, pathSegment: PathSegment): List<PathSegment> {
        if (path.isNotEmpty() && path.last().route == pathSegment.route) {
            val lastSegment = path.last()

            // this should never happen as used internally
            if (lastSegment.stops.last() != pathSegment.stops.first()) {
                throw AssertionError(
                    "The last element of the path must be equal to " +
                            "the first element of the path segment being added.")
            }

            val pathWithoutLastSegment = path.subList(0, path.size - 1)
            val newSegmentWithoutFirstStop = pathSegment.stops.subList(1, pathSegment.stops.size)
            val mergedSegment = PathSegment(
                pathSegment.route,
                lastSegment.stops + newSegmentWithoutFirstStop,
                lastSegment.distance + pathSegment.distance)

            return pathWithoutLastSegment + mergedSegment
        }

        return path + pathSegment
    }

    private fun validate(routeType: RouteType, stops: SequencedSet<Int>, distances: List<Int>) {
        if (stops.size < 2) {
            throw IllegalArgumentException("Routes must be composed of at least two stops")
        }
        when (routeType) {
            RouteType.Bidirectional, RouteType.Unidirectional -> {
                if (stops.size != distances.size + 1) {
                    throw IllegalArgumentException("In non-circular routes, distances.size must equal to stops.size - 1")
                }
            }
            else -> {
                if (stops.size != distances.size) {
                    throw IllegalArgumentException("In circular routes, stops.size must equal to distances.size")
                }
            }
        }
    }

    private fun connect(stopA: Int, stopB: Int, route: String, distance: Int) {
        graph.addEdge(stopA, stopB)
        graph.getValue(stopA)?.let {
            it.distances[stopB] = distance
            when (it.routes[stopB]) {
                null -> it.routes[stopB] = linkedSetOf(route)
                else -> it.routes[stopB]!!.add(route)
            }
        }
    }
}