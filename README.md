## Trane

TRAnsportation NEtworks; pronounced _train_.

Also a nod to John Coltrane, my favorite musician.

<hr>

Trane is a library for modeling transportation networks (buses, subways, trains) that are composed of multiple interconnecting routes.

It implements a modified version of Dijkstra's algorithm to efficiently discover the:

- lightest path by distance (a simple "weight")
- lightest path by number of routes (a complex "weight")
- lightest path by duration, considering the desired departure time and the transport schedule (a very complex "weight")

These are calculated by leveraging a single generic, dynamically-modifiable base algorithm. In this modular algorithm, custom lambdas are passed in as arguments to calculate the "weight" between two nodes; the "weight" is then used to rank possible paths. This concept is know as the "strategy" design pattern, wherein parts of an algorithm can be injected and swapped. By simply implementing said lambdas, anyone can extend this library to handle new use cases, including beyond the transportation domain.

<hr>

### Example

<pre>
Discovering quickest path (by duration) from stop 0 to stop 4
Desired departure time: LocalTime(localTime=12:10)
Quickest path:
ScheduledPathSegment(route=A, stops=[0, 1, 2], distance=6, departure=LocalTime(localTime=12:30), arrival=LocalTime(localTime=14:30))
ScheduledPathSegment(route=B, stops=[2, 3, 4], distance=6, departure=LocalTime(localTime=14:45), arrival=LocalTime(localTime=16:45))
Total distance: 12 km
Time spent waiting for first train: 20m minutes
Time spend waiting between trains: 15m
Total duration (excluding initial wait time): 4h 15m
</pre>