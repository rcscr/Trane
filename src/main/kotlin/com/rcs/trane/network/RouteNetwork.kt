package org.example.com.rcs.trane.network

import com.rcs.trane.graph.UnidirectionalPropertyGraph
import com.rcs.trane.network.PathSegment
import com.rcs.trane.network.RouteType
import com.rcs.trane.network.StopData
import java.util.SequencedSet

class RouteNetwork {

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
        val lightestPath = graph.getLightestPathSimple(
            start,
            end,
            { weight, nodeA, nodeB -> weight + graph.getValue(nodeA)!!.distances[nodeB]!! },
            { weightA, weightB -> weightA.compareTo(weightB) },
            0)
        return lightestPath?.let { enrichPathWithRoutesAndDistance(it.path) }
    }

    fun getShortestPathByRoutes(start: Int, end: Int): Path? {
        return graph.getLightestPathComplex(
            start,
            end,
            { path, nodeA, nodeB ->
                val nodeAValue = graph.getValue(nodeA)!!
                nodeAValue.routes[nodeB]!!
                    .map {
                        val newPathSegment = PathSegment(it, listOf(nodeA, nodeB), nodeAValue.distances[nodeB]!!)
                        Path(
                            mergePathSegmentIntoPath(path.segments, newPathSegment),
                            path.totalDistance + nodeAValue.distances[nodeB]!!)
                    }
            },
            { pathA, pathB -> pathA.numberOfRoutes().compareTo(pathB.numberOfRoutes()) },
            Path(listOf(), 0))
            ?.weight
    }

    fun getShortestPathByStops(start: Int, end: Int): Path? {
        val shortestPath = graph.getShortestPath(start, end)
        return shortestPath?.let { enrichPathWithRoutesAndDistance(it) }
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

    /**
     * TODO: retire this in favor of build path via weight accumulation, as in getShortestPathByRoutes
     */
    private fun enrichPathWithRoutesAndDistance(path: SequencedSet<Int>): Path {
        val startAndRoute = path.map { Pair(it, graph.getValue(it)!!.routes) }

        val pathWithRoutes = mutableListOf<PathSegment>()
        var i = 0
        val range = 0..startAndRoute.size-2

        while (i in range) {
            var curr = startAndRoute[i]
            var next = startAndRoute[i + 1]

            val routeOptions = curr.second[next.first]!!

            val bestRoute = when {
                // if going from i to i+1 is possible via the same route as from i-1 to i, continue on that route
                pathWithRoutes.isNotEmpty() && routeOptions.contains(pathWithRoutes.last().route) ->
                    pathWithRoutes.last().route
                // else select the route that goes farthest
                else -> {
                    routeOptions.maxBy { route ->
                        var depth = 0
                        var j = i + 1
                        while (j + 1 < startAndRoute.size && startAndRoute[j].second[startAndRoute[j+1].first]!!.contains(route)) {
                            j++
                            depth++
                        }
                        depth
                    }
                }
            }

            val stopsInThisRoute = mutableListOf(curr.first)
            var distance = 0
            while (curr.second[next.first]!!.contains(bestRoute)) {
                stopsInThisRoute.add(next.first)
                distance += graph.getValue(curr.first)!!.distances[next.first]!!
                if (++i in range) {
                    curr = startAndRoute[i]
                    next = startAndRoute[i + 1]
                } else {
                    break
                }
            }

            pathWithRoutes.add(PathSegment(bestRoute, stopsInThisRoute, distance))
        }

        return Path(pathWithRoutes.toList(), pathWithRoutes.sumOf { it.distance })
    }
}