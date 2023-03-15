package org.example;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import org.example.model.Adjacent;
import org.example.model.Ticket;
import org.example.model.xml.Data;
import org.example.model.xml.Station;
import org.example.model.xml.Train;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class Main {

    // In minutes
    private static final int WAIT_TIME = 60;
    private static List<Train> trains = new ArrayList<>();
    private static List<Station> stations = new ArrayList<>();
    private static Map<String, Set<Adjacent>> mapaBase = new HashMap<>();

    public static void main(String[] args) throws JAXBException {
        JAXBContext jaxbContext = JAXBContext.newInstance(Data.class);
        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
        Data data = (Data) jaxbUnmarshaller.unmarshal(new File("generated-xml/tickets.xml"));
        stations = data.getStations().getStations();
        trains = data.getTrains().getTrains();

        generarMapaBase();


        List<Ticket> finalTicketsList = new ArrayList<>();


        //Djikstra al final segurament sobri
//        List<TmpData> tmpData = new ArrayList<>();
//        stations.forEach(station -> {
//                    tmpData.add(new TmpData(station.getId(), mapaBase.get(station.getId())));
//                }
//        );
//        for (TmpData estacio : tmpData) {
//            DijkstraApp.run(estacio.getId(), tmpData);
//        }

    }

    private static String tab() {
        String tab = "";
        for(int i = 0; i<nRecursiva; i++){
            tab += "\t\t";
        }
        return tab;
    }

    static int nRecursiva = 0;
    private static void generarMapaBase() {
        stations.forEach(station -> {
            if(!station.getName().equals("Geneva")) return;
            System.out.println("======================================================");
            // Obtenir trens que surten d'aquí
            List<Train> llistaBase = getAllTrainsLeavingFromStation(station.getId());
            System.out.println("Trens sortint de "+station.getName()+":");
            llistaBase.forEach(train -> {
                System.out.println("\t"+train);
            });
            for (Train t : llistaBase) {
                System.out.println("Generant mapa per "+t+"...");
                Map<Station, List<Adjacent>> mapa = new HashMap<>();
                mapa.put(station, new ArrayList<>());
                System.out.println(station.getName()+" afegit al mapa");
                mapa.get(station).add(new Adjacent(t.getArrivalStation(), t, t.getDuration()));
                System.out.println(t.getArrivalStation()+" afegit al mapa com a adjacent amb duració de "+t.getDuration());
                // Estacio arribada

                System.out.println("Inici de recursiva");
                nRecursiva = 0;
                recursivaTest(t, mapa);

                System.out.println("=============================================");
                System.out.println("MAPA FINAL");
                System.out.println("=============================================");
                mapa.forEach((s, adj) -> {
                    System.out.println(s.getName()+" -> ");
                    adj.forEach(adjacent -> {
                        System.out.println("\t"+adjacent);
                    });
                });
            }
        });
    }

    private static void recursivaTest(Train previousTrain, Map<Station,List<Adjacent>> mapa) {
        nRecursiva++;
        System.out.println(tab()+"---Obtenint trens vàlids desde "+previousTrain.getArrivalStation());
        List<Train> llistaTrens = getValidTrains(previousTrain.getArrivalStation(), previousTrain.getArrivalTime());
        // Si hem arribat a una estacio final, atura la funció recursiva
        if (llistaTrens.isEmpty()) {
            System.out.println(tab()+"No s'han trobat trens, sortint de la recursiva...");
            // Problema. Entra en un bucle infinit, fins que arriba un punt que no hi ha més trens i per tant, la llista serà
            // buida i lògicament sortirà d'aquí. No tenim manera de dir-li que pari a un punt determinat
            System.out.println(tab()+"Afegint "+previousTrain.getArrivalStation()+" al mapa, sense adjacents abans de sortir...");
            mapa.put(previousTrain.getArrivalStation(),new ArrayList<>());
            System.out.println(tab()+"Sortint...");
            nRecursiva--;
            return;
        }

        for (Train t : llistaTrens) {
            System.out.println(tab()+"Iterant sobre tren "+t);
            if (!mapa.containsKey(t.getDepartureStation())) {
                System.out.println(tab()+"Estació "+t.getDepartureStation()+" no existeix al mapa, afegint-la...");
                mapa.put(t.getDepartureStation(), new ArrayList<>());
            }
            // Canviar això a un parse de data ben fet (preguntar Pau)
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy, HH:mm:ss");
            LocalDateTime arrivalTime = LocalDateTime.parse(t.getArrivalTime(), formatter);
            LocalDateTime prevStationArrTime = LocalDateTime.parse(previousTrain.getArrivalTime(), formatter);
            int tempsTrajecteiEspera = (int) Math.abs(ChronoUnit.MINUTES.between(arrivalTime, prevStationArrTime));

            //Obtenir llista adjacents de la estacio actual
            System.out.println(tab()+"Obtenint llista adjacents del mapa...");
            List<Adjacent> adjacentsEstacioAcual = mapa.get(t.getDepartureStation());
            for (Adjacent a : adjacentsEstacioAcual) {
                if (a.getEstacio().equals(t.getArrivalStation()) &&
                        a.getCost() > tempsTrajecteiEspera) {
                    a.setCost(tempsTrajecteiEspera);
                    a.setTrain(t);
                }
            }
            // Si encara no hem passat per la adjacent (estacio2), afegeix-la directament
            if (!mapa.containsKey(t.getArrivalStation())) {
                mapa.get(t.getDepartureStation()).add(new Adjacent(t.getArrivalStation(), t, tempsTrajecteiEspera));
            } else { // Si ja la tenim, mirar si el cost és superior que el d'ara, i substituir-lo
                List<Adjacent> llistaadjacents = mapa.get(t.getDepartureStation());
                for(Adjacent a: llistaadjacents) {
                    if(a.getEstacio().equals(t.getArrivalStation()) &&
                            a.getCost() > tempsTrajecteiEspera){
                        a.setCost(tempsTrajecteiEspera);
                        a.setTrain(t);
                        break;
                    }
                }
            }

            System.out.println(tab()+"Mapa fins ara...");
            System.out.println(tab()+"=============================================");
            mapa.forEach((s, adj) -> {
                System.out.println(tab()+s.getName()+" -> ");
                adj.forEach(adjacent -> {
                    System.out.println(tab()+"\t"+adjacent);
                });
            });
            System.out.println(tab()+"=============================================");

            // Estacio arribada
            recursivaTest(t, mapa);
        }
    }

    private static List<Train> getValidTrains(Station station, String time) {
        List<Train> llista = new ArrayList<>();
        getAllTrainsLeavingFromStation(station.getId()).forEach(t -> {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy, HH:mm:ss");
            LocalDateTime departureTime = LocalDateTime.parse(t.getDepartureTime(), formatter);
            LocalDateTime prevStationArrTime = LocalDateTime.parse(time, formatter);

            // Si el tren en qüestió encara no ha sortit quan naltros arribem
            // llavors afegeix el tren a la llista de vàlids
            if (!departureTime.isBefore(prevStationArrTime)) {
                System.out.println(tab()+"Tren "+t+" és vàlid. Afegint a llista vàlids");
                llista.add(t);
            } else {
                System.out.println(tab()+"Tren "+t+" NO vàlid. Descartat.");
            }
        });
        return llista;
    }

    private static List<Train> getTrainListFollowingRoute(List<String> rutaEstacions, Train primerTrain) {
        List<Train> rutaTrens = new ArrayList<>();

        // Si només hi ha dos estacions, només hi haurà un tren, és a dir, el primer, per tant, retornar una llista amb
        // només aquest.
        if (rutaEstacions.size() <= 2) {
            rutaTrens.add(primerTrain);
            return rutaTrens;
        }
        // Si arribem aquí vol dir que hi haurà més d'un tren, per tant, hem d'obtenir les estacions que surten de cadascun
        // dels següents trens, (menys els de l'últim), i anar buscant els següents.
        rutaTrens.add(primerTrain); // Li posem el primer pq sempre hi és
        int i = 0; // Cursor a 0
        //Obtenim els trens que surten de la primera estació cap a la segona
        List<Train> trainsAux = getAllTrainsGoingFromTo(rutaEstacions.get(i), rutaEstacions.get(i + 1));
        i++; // Posicionem el cursor a la següent estació
        String prevArrivalTime = primerTrain.getArrivalTime(); // Guardem la hora d'arribada a la segona estació
        for (; i < rutaEstacions.size() - 1; i++) {
            // Obtenir el primer tren que surt de la ith estació cap a la ith+1 estació
            Train winnerTrain = null;
            String winnerArrivalTime = "";
            LocalDateTime minimumDepartureTime = LocalDateTime.MAX;
            LocalDateTime prevTrainArrTime = null;
            for (Train t : getAllTrainsGoingFromTo(rutaEstacions.get(i), rutaEstacions.get(i + 1))) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy, HH:mm:ss");
                LocalDateTime departureTime = LocalDateTime.parse(t.getDepartureTime(), formatter);
                prevTrainArrTime = LocalDateTime.parse(prevArrivalTime, formatter);

                if (departureTime.isAfter(prevTrainArrTime)) {
                    if (departureTime.isBefore(minimumDepartureTime)) {
                        minimumDepartureTime = departureTime;
                        winnerArrivalTime = t.getArrivalTime();
                        winnerTrain = t;
                    }
                }
            }

            rutaTrens.add(winnerTrain);
            if (prevArrivalTime.equals("")) break; // Si hem arribat al final, sortim

            prevArrivalTime = prevArrivalTime; //netejar per la següent iteració
        }
        return rutaTrens;
    }


    private static void calculateTempsEspera(Map<Train, Integer> tempsEsperaList, List<String> ruta, int routeIndex) {
            /*
            Dona'm tots els trens amb:+
                departureStation == {depStationId}
                arrivalStation == {arrivalStationId}
                departureTime > {arrivalTimeAnterior} (no igual, pq hi ha un temps real mínim de transbord, no és instantani)
            I de tots aquests dona'm el que té un departureTime més petit (osigui, el que surti abans)

            */
        int i = 0;
        List<Train> trains = getAllTrainsGoingFromTo(ruta.get(i), ruta.get(i + 1));
        if (ruta.size() <= 2) {
            // Si només hi ha 2 estacions a la ruta (o cap), vol dir que no hi haurà transbord, per tant el temps d'espera serà 0
            tempsEsperaList.put(trains.get(0), 0);
            // Sortim, perquè ja no hi ha més estacions a la ruta
            return;
        }

        // Si arribem aquí vol dir que hi ha més estacions, per tant cridem la recursiva perque vagi calculant els temps
        // d'espera.

        // Movem una posició l'iterador, per agafar el següent parell d'estacions
        i++;

        for (Train t : trains) {
            int tempsEspera = calculateTempsEsperaRecursive(ruta, t.getArrivalTime(), i);
            if (tempsEspera != -1) {//Mentre no haguem arribat al final
                //tempsEsperaList.add(t.getId(), tempsEspera);
                System.out.println("Temps espera per Ruta " + routeIndex + " amb tren inici " + t + ": " + tempsEspera);
                tempsEsperaList.put(t, tempsEspera);
            }
        }
    }

    private static int calculateTempsEsperaRecursive(List<String> ruta, String prevArrivalTime, int i) {
        // SI hem arribat al punt on (i == última estació) que retorni 0 com a temps d'espera d'aquest tram, perquè ja és
        // la última estació, i quan baixes ja no fas cap transbord més.
        if (i >= ruta.size() - 1) return 0;

        LocalDateTime minimumDepartureTime = LocalDateTime.MAX;
        String winnerArrivalTime = "";
        Train winnerTrain = null;
        LocalDateTime prevTrainArrTime = null;
//        System.out.println("Getting all trains going from "+ruta.get(i)+" to "+ruta.get(i+1));
        for (Train t : getAllTrainsGoingFromTo(ruta.get(i), ruta.get(i + 1))) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy, HH:mm:ss");
            LocalDateTime departureTime = LocalDateTime.parse(t.getDepartureTime(), formatter);
            prevTrainArrTime = LocalDateTime.parse(prevArrivalTime, formatter);

            if (departureTime.isAfter(prevTrainArrTime)) {
                if (departureTime.isBefore(minimumDepartureTime)) {
                    minimumDepartureTime = departureTime;
                    winnerArrivalTime = t.getArrivalTime();
                    winnerTrain = t;
                }
            }
        }
        // Movem el cursos una posició més
        i++;

        // Calculem temps d'espera ara que ja sabem quin és el tren guanyador
        int tempsEspera = (int) Math.abs(ChronoUnit.MINUTES.between(minimumDepartureTime, prevTrainArrTime));

        if (winnerArrivalTime.equals("")) return -1;
        return tempsEspera + calculateTempsEsperaRecursive(ruta, winnerArrivalTime, i);
    }






    /*
        public static void processParent(List<Train> list) {
            System.out.println("\033[32m==== Processant la llista pare (" + list.size() + " trens) ====\033[0m");
            // Això ho farà només per els indexos de la llista pare
            // Per cadascun d'aquestos es genera 1 mapa amb les estacions viables segons la hora
            list.forEach(train -> {
                System.out.println("\033[32mProcessant el tren: " + Integer.toHexString(list.hashCode()) + " - " + train + " de la llista pare\033[0m\n");
                // Preparar mapa
                Map<String, List<Adjacent>> map = new HashMap<>();
                map.put(train.getLine().getDepartureStation().getId(), new ArrayList<>());
                System.out.println("Node arrel (" + train.getLine().getDepartureStation().getId() + ") afegit al map");
                System.out.println("Buscant estacions adjacents vàlides");
                // Tots els trains que surten de la següent estació seran vàlids, menys els que ja estiguin passats de temps,
                // de moment. Lo de WAIT_TIME té sentit de fer-ho quan hi hagi trens que surtin tota la estona, però ara mateix
                // que només tenim tres trens per dia, millor no ho tinguem en compte perquè acabarem generant uns mapes amb
                // tres estacions comptades, i inclus mapes incomplets que no tinguin connexió amb totes les estacions.
                // Ara per ara, si el client s'ha d'esperar al dia següent per agafar el següent tren que li toca per arribar
                // al seu destí, doncs que s'espavili i es busqui un hotel per passar la nit.
                Station arrivalStation = train.getLine().getArrivalStation();
                List<Train> trainsValidsSeguentEstacio = getTrainsValid(arrivalStation, train.getArrivalTime());
                map.put(train.getLine().getDepartureStation().getId(), getUniqueAdjacentsListFromTrainsList(trainsValidsSeguentEstacio));
                System.out.println("GENOVA->");
                trainsValidsSeguentEstacio.forEach(train1 -> System.out.println(train1));
                processChild(map, trainsValidsSeguentEstacio);
                System.out.println("MAPA FINAL PER " + train + ":");
                map.entrySet().forEach(entry -> {
                    System.out.println(entry.getKey() + " -> " + entry.getValue().toString());
                });
            });
        }


     */
//    private static Set<Adjacent> getAdjacentStationsFromStation(String stationId) {
//        // Utilitzem un Set i no una List, perquè no volem tenir estacions repetides
//        // Si tenim una llista amb 3 trains que van cap a Lausane, llavors acabaríem tenint una llista amb 3 estacions Lausane
//        Set<Adjacent> nextStations = new HashSet<>();
//        getAllTrainsLeavingFromStation(stationId).forEach(train -> {
//            nextStations.add(new Adjacent(train.getArrivalStation().getId(), train.getDuration()));
//        });
//        return nextStations;
//    }

    /*
        public static void processChild(Map<String, List<Adjacent>> map, List<Train> list) {
            if (!list.isEmpty()) {
                list.forEach(train -> {
                    // Imprimim el tren en vermell per saber que és dolent a l'hora de debugar
                    if (!train.isValid()) {
                        System.out.println("\033[31m====> " + Integer.toHexString(list.hashCode()) + " - " + train + ")\033[0m");
                    } else {
                        System.out.println("\033[34m====> " + Integer.toHexString(list.hashCode()) + " - " + train + "\033[0m");
                    }
                    System.out.println("Processant estació " + train.getLine().getDepartureStation().getId());
                    System.out.println("Buscant estacions adjacents vàlides");
                    Station arrivalStation = train.getLine().getArrivalStation();
    //                if (!map.containsKey(train.getLine().getDepartureStation().getId())) {
    //                    map.put(train.getLine().getDepartureStation().getId(), new ArrayList<>());
    //                    System.out.println(train.getLine().getDepartureStation().getId() + " agegit al map");
    //                }
    //                System.out.println("Buscant trens valids que surtin de la següent estació (" + arrivalStation.getId() + ")");
    //                boolean finish = processChild(map, getTrainsValid(arrivalStation, train.getArrivalTime()));
                    List<Train> trainsValidsSeguentEstacio = getTrainsValid(arrivalStation, train.getArrivalTime());
                    System.out.println(train.getLine().getDepartureStation().getId() + "->");
                    trainsValidsSeguentEstacio.forEach(System.out::println);
                    if (!map.containsKey(train.getLine().getDepartureStation().getId())) {
    //                    map.put(train.getLine().getDepartureStation().getId(), getUniqueAdjacentsListFromTrainsList(trainsValidsSeguentEstacio));
                    } else {
                        addAdjacentStationToStationInMap(map, train.getLine().getDepartureStation().getId(), trainsValidsSeguentEstacio);
                    }
                    processChild(map, trainsValidsSeguentEstacio);
                });
            } else {
                System.out.println("\033[31mFinished processing a child list!\033[0m\n");
            }
        }


     */
    // Funció per afegir elements d'una llista a una altra llista, assegurant-nos que no hi ha cap element repetit
//    private static void addAdjacentStationToStationInMap(Map<String, List<Adjacent>> map, String key, List<Train> elementsToAdd) {
//        List<Adjacent> llista = map.get(key);
//        elementsToAdd.forEach(train -> {
//            Adjacent a = new Adjacent(train.getDepartureStation().getId(), train.getDuration());
//            if (!llista.contains(a)) {
//                llista.add(a);
//            }
//        });
//    }

//    public static void generateMapForTrain(Train t) {
//        // Get valid trains from current train
//        List<Train> list = getTrainsValid(t.getLine().getArrivalStation().getId(), t.getArrivalTime());
//        System.out.println(list);
//    }

    private static List<Train> getAllTrainsLeavingFromStation(String stationId) {
        List<Train> llista = new ArrayList<>();
        trains.forEach(t -> {
            if (t.getDepartureStation().getId().equals(stationId))
                llista.add(t);
        });
        return llista;
    }

    private static List<Train> getAllTrainsGoingFromTo(String depStationId, String arrStationId) {
        List<Train> llista = new ArrayList<>();
        trains.forEach(t -> {
            if (t.getDepartureStation().getId().equals(depStationId) && t.getArrivalStation().getId().equals(arrStationId))
                llista.add(t);
        });
        return llista;
    }


}