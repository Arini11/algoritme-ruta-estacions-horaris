package org.example.dijkstra;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Node<T> implements Comparable<Node<T>>{

    private final String name;
    private Integer distance = Integer.MAX_VALUE;
    private List<Node<T>> shortestPath = new LinkedList<>();
    private Map<Node<T>, Integer> adjacentNodes = new HashMap<>();

    /*
    Constructor
     */
    public Node(String name) {
        this.name = name;
    }


    /*
    Methods
     */
    public void addAdjacentNode(Node<T> node, int weight){
        adjacentNodes.put(node, weight);
    }

    @Override
    public int compareTo(Node<T> node) {
        return Integer.compare(this.distance, node.getDistance());
    }



    /*
    GETTERS AND SETTERS
     */

    public Integer getDistance() {
        return distance;
    }

    public void setDistance(Integer distance) {
        this.distance = distance;
    }

    public List<Node<T>> getShortestPath() {
        return shortestPath;
    }

    public void setShortestPath(List<Node<T>> shortestPath) {
        this.shortestPath = shortestPath;
    }

    public Map<Node<T>, Integer> getAdjacentNodes() {
        return adjacentNodes;
    }

    public void setAdjacentNodes(Map<Node<T>, Integer> adjacentNodes) {
        this.adjacentNodes = adjacentNodes;
    }

    public String getName() {
        return name;
    }

}
