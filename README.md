## Trane

TRAnsportation NEtworks; pronounced _train_.

Also a nod to John Coltrane, my favorite musician.

<hr>

Trane is a library for modeling transportation networks (buses, subways, trains) that are composed of multiple interconnecting routes, daily schedules, and even delays.

It implements a modified version of Dijkstra's algorithm to efficiently discover the:

- lightest path by distance (a simple "weight")
- lightest path by number of routes (a complex "weight")
- lightest path by duration, considering the desired departure time, the transport schedule, and any vehicle delays (a very complex "weight")

These are calculated by leveraging a single generic, dynamically-modifiable base algorithm. In this modular algorithm, custom lambdas are passed in as arguments to aggregate the "weight" between two nodes; the "weight" is then used to rank possible paths. This concept is know as the "strategy" design pattern, wherein parts of an algorithm can be injected and swapped. By simply implementing said lambdas, anyone can extend this library to handle new use cases, including beyond the transportation domain.

<hr>

### Example

<pre>
Discovering quickest path (by duration) from stop 0 to stop 4
Desired departure time: 2024-06-05T12:10
Quickest path:
ScheduledPathSegment(route=A, stops=[0, 1, 2], distance=6, departure=2024-06-05T12:30, arrival=2024-06-05T14:30, delayMillis=0)
ScheduledPathSegment(route=B, stops=[2, 3, 4], distance=7, departure=2024-06-05T15:00, arrival=2024-06-05T17:00, delayMillis=900000)
Total distance: 13km
Total delays: 15m
Time spent waiting for first train: 20m
Time spend waiting between trains: 30m
Trip duration (excluding initial wait time): 4h 30m
</pre>