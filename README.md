## Trane

i.e. "Train"

Also a nod to John Contrane, my favorite musician.

<hr>

Trane is a library for modeling transportation networks (buses, subways, trains) that are composed of multiple interconnecting routes.

It uses a modified version of Dijkstra's algorithm to efficiently discover the:

- shortest path by number of stops
- lightest path by weight (i.e. distance)
- lightest path by number of route transfers 

All of these are calculated with a single dynamically-modifiable base algorithm. In this modular algorithm, custom lambdas are passed in as arguments to calculate the "weight" between two nodes. This concept is know as the "strategy" design pattern, wherein parts of an algorithm can be injected and swapped.


Future work includes:

- taking into account the schedule of individual routes, proving the shortest path by duration