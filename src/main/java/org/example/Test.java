package org.example;

import org.example.model.Adjacent;
import org.example.model.xml.Station;
import org.example.model.xml.Train;

import java.util.*;

public class Test {

    static Map<String, Set<Adjacent>> mapaBase = new HashMap<>();
    public static void main(String[] args) {

        Set<Station> llista = getAllTrainsLeavingFromStation();
        System.out.println(mapaBase.toString());
    }
    private static Set<Station> getAllTrainsLeavingFromStation() {
        Set<Station> llista = new HashSet<>();
        mapaBase.put("asd",new HashSet<>(Set.of(new Adjacent("adj1",10))));
        mapaBase.put("asd",new HashSet<>(Set.of(new Adjacent("adj2",10))));
        return llista;
    }

}