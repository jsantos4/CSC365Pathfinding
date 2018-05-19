package YelpDataPathfinding;

import java.util.*;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

class Graph {
    private ArrayList<Node> nodes;

    Graph() {
        nodes = new ArrayList<>();
    }

    //Initializes a graph with a set of nodes
    void init(ArrayList<Node> nodelist) {
        int count = 0;
        for (Node node : nodelist) {
            nodes.add(node);
            node.id = count;
            count++;
        }
        assignEdges();
    }

    //Assigns all edges in the graph
    private void assignEdges() {
        for (Node node : nodes) {
            for (Node dest: nodes) {
                node.placeEdge(dest);
            }
        }
    }

    //Returns minimum spanning tree of a node
    ArrayList<String> findSpanningTree(Node src) {
        src.minDistance = 0;
        ArrayList<String> spanningTree = new ArrayList<>();
        PriorityQueue<Node> queue = new PriorityQueue<>();
        queue.add(src);

        while (!queue.isEmpty()) {
            Node u = queue.poll();

            for (Node.Edge e : u.edges) {
                Double newDistance = u.minDistance + e.weight;
                Node check = e.destination;

                if (check.minDistance > newDistance) {
                    Node looking = e.destination;
                    queue.remove(looking);
                    looking.minDistance = newDistance;

                    looking.path.clear();
                    looking.path.addAll(u.path);
                    looking.path.add(u);
                    check.previous = u;

                    spanningTree.add(e.destination.business.toString());
                    queue.add(looking);

                }
            }
        }
        return spanningTree;
    }

    //Finds path from one node to another
    LinkedList<Node> dijkstra(Node src, Node dest) {
        findSpanningTree(src);
        if (!dest.path.isEmpty())
            return dest.path;
        else {
            System.out.println("No path found");
            return dest.path;
        }
    }

    //Writes all nodes in the graph into a file
    void serialize(String file) {
        for(Node node : nodes) {
            writeNode(node, file);
        }
    }

    //Writes a node into a file at a position corresponding with its ID
    private void writeNode(Node n, String NODE_FILE) {
        try {
            RandomAccessFile file = new RandomAccessFile(NODE_FILE, "rw");
            file.seek(n.id*2048);
            FileChannel fc = file.getChannel();
            ByteBuffer bb = ByteBuffer.allocate(2048);
            Business business = n.business;

            bb.putInt(n.id);

            System.out.println("writing node: " + n.id);

            //Enters ID into bytebuffer
            byte[] id = business.getId().getBytes();
            bb.putInt(id.length);
            bb.put(id);

            //Enters name into bytebuffer
            byte[] name = business.getName().getBytes();
            bb.putInt(name.length);
            bb.put(name);

            //Enters city into bytebuffer
            byte[] city = business.getCity().getBytes();
            bb.putInt(city.length);
            bb.put(city);

            //Enters state into bytebuffer
            byte[] state = business.getState().getBytes();
            bb.putInt(state.length);
            bb.put(state);

            //Enters category score into bb
            bb.putDouble(business.categoryScore);

            //Allocates bytes for all categories in bb
            bb.putInt(business.categories.size());

            //Enters each category into bb
            for (String c : business.categories) {
                byte[] catBytes = c.getBytes();
                bb.putInt(catBytes.length);
                bb.put(catBytes);
            }

            //put dest id of edges so reader knows where to go read, and put weight
            bb.putInt(n.edges.length);
            for (int i = 0; i < n.edges.length; i++) {
                bb.putInt(n.edges[i].destination.id);
                bb.putDouble(n.edges[i].weight);
            }

            bb.flip();
            fc.write(bb);
            bb.clear();
            fc.close();
            file.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
