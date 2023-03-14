package org.example.dijkstra;

import java.util.*;
import java.util.stream.Stream;

public class Dijkstra<T> {

    public void calculateShortestPath(Node<T> source) {
        source.setDistance(0);
        Set<Node<T>> settledNodes = new HashSet<>();
        Queue<Node<T>> unsettledNodes = new PriorityQueue<>(Collections.singleton(source));

        while (!unsettledNodes.isEmpty()) {
            Node<T> currentNode = unsettledNodes.poll();
            currentNode.getAdjacentNodes()
                    .entrySet().stream()
                    .filter(entry -> !settledNodes.contains(entry.getKey()))
                    .forEach(entry -> {
                        evaluateDistanceAndPath(entry.getKey(), entry.getValue(), currentNode);
                        unsettledNodes.add(entry.getKey());
                    });
            settledNodes.add(currentNode);
        }
    }

    private void evaluateDistanceAndPath(Node<T> adjacentNode, Integer edgeWeight, Node<T> sourceNode) {
        Integer newDistance = sourceNode.getDistance() + edgeWeight;

        if (newDistance < adjacentNode.getDistance()) {
            adjacentNode.setDistance(newDistance);
            adjacentNode.setShortestPath(Stream.concat(sourceNode.getShortestPath().stream(), Stream.of(sourceNode)).toList());
        }
    }

    public void printPaths(List<Node<T>> nodes, String source) {
        nodes.forEach(node -> {
            String departureStation = source;
            String arrivalStation = node.getName();
            List<String> tmpPath = new ArrayList<>(node.getShortestPath().stream()
                    .map(Node::getName).map(Objects::toString)
                    .toList());
            tmpPath.add(node.getName());
            System.out.println("De "+departureStation+" a "+arrivalStation+" -> "+(tmpPath.size() <= 1 ? "" : tmpPath));
        });

    }
}
