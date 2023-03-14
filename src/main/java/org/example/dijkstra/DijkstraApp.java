package org.example.dijkstra;


import org.example.model.TmpData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DijkstraApp {

    public static void run(String source, List<TmpData> tmpData) {
        System.out.println("\n--------------------------------------");
        System.out.println("Calculant rutes estació " + source);
        System.out.println("--------------------------------------");

        Map<String, Node<String>> list = new HashMap<>();

        // Fill map with stations
        tmpData.forEach(node -> list.put(node.getName(), new Node<>(node.getName())));
        // Add adjacent nodes to each node
        tmpData.forEach(node -> {
            node.getAdjacents().forEach(adjacent -> list.get(node.getName())
                    .addAdjacentNode(list.get(adjacent.getId()), adjacent.getDuration()));
        });

        Dijkstra<String> dijkstra = new Dijkstra<>();
        dijkstra.calculateShortestPath(list.get(source));
        dijkstra.printPaths(new ArrayList<>(list.values()), source);
    }


}
