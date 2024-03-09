## Trane

TRAnsportation NEtworks; pronounced _train_.

Also a nod to John Coltrane, my favorite musician.

<hr>

Trane is a library for modeling transportation networks (buses, subways, trains) that are composed of multiple interconnecting routes.

It implements a modified version of Dijkstra's algorithm to efficiently discover the:

- lightest path by distance (a simple "weight")
- lightest path by number of routes (a complex "weight")
- lightest path by duration, considering the desired departure time and the transport schedule (a very complex "weight")

These are calculated leveraging a single dynamically-modifiable base algorithm. In this modular algorithm, custom lambdas are passed in as arguments to calculate the "weight" between two nodes. This concept is know as the "strategy" design pattern, wherein parts of an algorithm can be injected and swapped. Anyone can reuse this library to handle new use cases by simply providing the lambdas to aggregate the weight.