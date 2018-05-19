package YelpDataPathfinding;

import java.util.LinkedList;

public class Node implements Comparable {

    //Variables
    int id;
    Business business;
    double minDistance = Double.POSITIVE_INFINITY;
    LinkedList<Node> path;
    Node previous;
    Edge[] edges;

    static class Edge {
        Node destination;
        double weight;
        Edge(Node destination, double weight) {
            this.destination = destination;
            this.weight = weight;
        }
    }

    Node(Business b) {
        this.business = b;
        path = new LinkedList<>();
        edges = new Edge[4];
        edges[0] = new Edge(null, Double.MAX_VALUE - 3);
        edges[1] = new Edge(null, Double.MAX_VALUE - 2);
        edges[2] = new Edge(null, Double.MAX_VALUE - 1);
        edges[3] = new Edge(null, Double.MAX_VALUE);
    }

    @Override
    public int compareTo(Object other){
        return Double.compare(minDistance,((Node)other).minDistance);
    }

    private static double haversine(double val) {
        return Math.pow(Math.sin(val / 2), 2);
    }

    //Finds haversine distance with two (longitude, latitude) coordinates
    private double getDistance(Node dest) {
        if (this != dest) {
            double dLat  = Math.toRadians((dest.business.getLatitude() - this.business.getLatitude()));
            double dLong = Math.toRadians((dest.business.getLongitude() - this.business.getLongitude()));

            double startLat = Math.toRadians(this.business.getLatitude());
            double endLat   = Math.toRadians(dest.business.getLatitude());

            double a = haversine(dLat) + Math.cos(startLat) * Math.cos(endLat) * haversine(dLong);
            double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

            return 6371 * c; // <-- d
        } else {
            return 0;
        }
    }

    //Assigns edge to a node if the destination node is closer than any of it's existing neighbors
    void placeEdge(Node dest) {
        Edge edge = new Edge(dest, this.getDistance(dest));
        if (this == dest) {
            return;
        } else if (edge.weight < this.edges[0].weight) {
            this.edges[2] = this.edges[3];
            this.edges[1] = this.edges[2];
            this.edges[0] = this.edges[1];
            this.edges[0] = edge;
        } else if (edge.weight < this.edges[1].weight) {
            this.edges[2] = this.edges[3];
            this.edges[1] = this.edges[2];
            this.edges[1] = edge;
        } else if (edge.weight < this.edges[2].weight) {
            this.edges[2] = this.edges[3];
            this.edges[2] = edge;
        } else if (edge.weight < this.edges[3].weight) {
            this.edges[3] = edge;
        } else {
            return;
        }
    }

}
