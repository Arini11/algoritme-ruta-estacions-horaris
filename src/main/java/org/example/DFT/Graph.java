package org.example.DFT;

import org.example.model.Adjacent;

import java.util.*;

// A directed graph using
// adjacency list representation
public class Graph {

    // No. of vertices in graph

    // adjacency list
    private Map<String,Set<Adjacent>> adjList;
    private List<List<String>> llistaFinal;

    public List<List<String>> getLlistaFinal() {
        return llistaFinal;
    }

    // Constructor
    public Graph(Map<String,Set<Adjacent>> adjList) {
        this.adjList = adjList;
        this.llistaFinal = new ArrayList<>();
    }

    // Prints all paths from
    // 's' to 'd'
    public void calculateAllPaths(String s, String d) {
        Set<String> visitedList = new HashSet<>();
        ArrayList<String> pathList = new ArrayList<>();

        // add source to path[]
        pathList.add(s);

        // Call recursive utility
        calculateAllPathsUtil(s, d, visitedList, pathList);
    }

    // A recursive function to print
    // all paths from 'u' to 'd'.
    // isVisited[] keeps track of
    // vertices in current path.
    // localPathList<> stores actual
    // vertices in the current path
    private void calculateAllPathsUtil(String u, String d,
                                   Set<String> visitedList,
                                   List<String> localPathList) {

        if (u.equals(d)) {
            llistaFinal.add(List.copyOf(localPathList));
            return;
        }

//        System.out.println("Visiting node "+u);
//        System.out.println("Marking node "+u+" as visited");
        // Mark the current node
        visitedList.add(u);

        // Recur for all the vertices
        // adjacent to current vertex
//        System.out.println("Looking for all adjacent nodes");
        for (Adjacent adj : adjList.get(u)) {
//            System.out.println("Iterating over adjacent node "+adj.getId()+" of node "+u);
            if (!visitedList.contains(adj.getId())) {
                // store current node
                // in path[]
//                System.out.println("Adding node "+adj.getId()+" to localPath");
                localPathList.add(adj.getId());
                calculateAllPathsUtil(adj.getId(), d, visitedList, localPathList);

                // remove current node
                // in path[]
//                System.out.println("Removing node "+adj.getId()+" from localPath");
                localPathList.remove(adj.getId());
            } else {
//                System.out.println("Ignoring this one because it's already visited");
            }
        }

        // Mark the current node
        visitedList.remove(u);
    }
}