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
Discovering quickest (by duration) path stop 0 to stop 4
Desired departure time: 2024-03-10T12:10:00Z
Quickest path:
ScheduledPathSegment(route=A, stops=[0, 1, 2], distance=6, departure=2024-03-10T12:30:00Z, arrival=2024-03-10T14:30:00Z)
ScheduledPathSegment(route=B, stops=[2, 3, 4], distance=6, departure=2024-03-10T14:45:00Z, arrival=2024-03-10T16:45:00Z)
Total distance: 12 km
Time spent waiting for first train: 20m minutes
Time spend waiting between trains: 15m
Total duration (excluding initial wait time): 4h 15m
</pre>