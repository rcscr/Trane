## Trane

i.e. "Train"

Also a nod to John Contrane, my favorite musician.

<hr>

Trane is a library for modeling transportation networks (buses, subways, trains) that are composed of multiple interconnecting routes.

It uses a dynamically-modifiable (i.e. via lambdas passed in as arguments) version of Dijkstra's algorithm to efficiently discover the:

- shortest path by number of stops
- lightest path by weight (i.e. distance)
- lightest path by number of route transfers 


Future work includes:

- taking into account the schedule of individual routes, proving the shortest path by duration