package org.example;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import org.example.DFT.Graph;
import org.example.model.Adjacent;
import org.example.model.PossibleRuta;
import org.example.model.Ticket;
import org.example.model.TrainAux;
import org.example.model.xml.Data;
import org.example.model.xml.Line;
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
    private static List<Line> lines = new ArrayList<>();
    private static List<Station> stations = new ArrayList<>();
    private static Map<String, Set<Adjacent>> mapaBase = new HashMap<>();

    public static void main(String[] args) throws JAXBException {
        JAXBContext jaxbContext = JAXBContext.newInstance(Data.class);
        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
        Data data = (Data) jaxbUnmarshaller.unmarshal(new File("generated-xml/tickets.xml"));
        stations = data.getStations().getStations();
        trains = data.getTrains().getTrains();
        lines = data.getLines().getLines();

        // Generar mapa base amb totes les estacions i les seves adjacents
        generarMapaBase();

        List<Ticket> finalTicketsList = new ArrayList<>();

        // GRAPH TEST
        stations.forEach(source -> {
            stations.forEach(dest -> {
                // Calcular possibles rutes de cada estació a cada estació excepte que source i dest siguin la mateixa estació
                if (!source.getId().equals(dest.getId())) {
                    Graph g = new Graph(mapaBase);
                    System.out.println("\n\033[33m####################################################################");
                    System.out.println("Calculant tots els possibles camins de " + source.getName() + " a " + dest.getName() + "...");
                    System.out.println("####################################################################\033[0m");
                    // Generar totes les rutes possibles (segons les línies que existeixen), per anar d'estació source a destination
                    g.calculateAllPaths(source.getId(), dest.getId());
                    List<PossibleRuta> rutesPossibles = new ArrayList<>();
                    int routeIndex = 0; //Index to keep track of the route we are iterating
                    for(List<String> ruta: g.getLlistaFinal()) {
//                        System.out.println(routeIndex+" -> "+ruta);
                        // Duració trajecte
                        int duracioBase = 0;
                        for (int i = 0; i < ruta.size() - 1; i++) {
                            duracioBase += getDuracio(ruta.get(i), ruta.get(i + 1));
                        }
                        // Temps d'espera
                        Map<Train,Integer> tempsEsperaList = new HashMap<>();
                        System.out.println("Calculant els temps d'espera per la ruta "+routeIndex+"...");
                        // Passar-li llista pq la vagi omplint amb la ruta de trens
                        List<Train> rutaTrens = new ArrayList<>();
                        calculateTempsEspera(tempsEsperaList, ruta, routeIndex);

//                        System.out.print("\033[34m" + duracioBase + " min.\033[0m | ");
//                        System.out.print("\033[32m" + ruta + "\033[0m \n");
                        rutesPossibles.add(new PossibleRuta(ruta, duracioBase,tempsEsperaList));

                        routeIndex++;
                    }

//                    System.out.println("RUTES POSSIBLES ------------------------------------->");
//                    rutesPossibles.forEach(possibleRuta -> {
//                        System.out.println("Ruta->"+possibleRuta.getRuta());
//                        System.out.println("Hores->");
//                        possibleRuta.getTempsEsperaList().forEach((train, integer) -> {
//                            System.out.println(train+" --- "+integer);
//                        });
//                    });
//                    System.out.println("<----------------------------------RUTES POSSIBLES");

                    // Dona'm la quickest path per cada hora de cada tren
                    Map<String, TrainAux> totalWaitTime = new HashMap<>();
                    rutesPossibles.forEach(possibleRuta -> {
                        possibleRuta.getTempsEsperaList().forEach((train, duration) -> {
                            String depTimeAux = train.getDepartureTime().split(",")[1].trim();
                            int thisDuracioTotal = possibleRuta.getBaseDuration()+duration;
                            if(!totalWaitTime.containsKey(depTimeAux)) {
                                totalWaitTime.put(depTimeAux, new TrainAux(thisDuracioTotal,train,possibleRuta.getRuta()));
                            } else {
                                // Mira'm si aquest és més petit que el que ja tenim, si si, posa'm al map aquest i elimina el que hi havia
                                if(totalWaitTime.get(depTimeAux).getDuracioTotal() > thisDuracioTotal){
                                    totalWaitTime.put(depTimeAux, new TrainAux(thisDuracioTotal,train,possibleRuta.getRuta()));
                                }
                            }
                        });
                    });

                    System.out.println("### QUICKEST PATH FOR "+source+"-"+dest+" ###");
                    totalWaitTime.forEach((s, trainAux) -> {
                        System.out.println("At "+s+": "+trainAux.getTrain()+"\nWith duration of: "+trainAux.getDuracioTotal()+"\nRoute: "+trainAux.getRutaEstacions());
                        // Obtenir llista de trens a partir de la llista d'estacions, tenint en compte el primer train.
                        List<Train> llistaFinal = getTrainListFollowingRoute(trainAux.getRutaEstacions(), trainAux.getTrain());
                        System.out.println("Ruta estacions->"+trainAux.getRutaEstacions());
                        System.out.println("Ruta trens->"+llistaFinal);
                        finalTicketsList.add(
                                new Ticket(
                                        source.getId(), //departureStationId
                                        trainAux.getTrain().getDepartureTime(),
                                        dest.getId(), //arrivalStationId
//                                        llistaFinal.get(llistaFinal.size()-1).getArrivalTime(),
                                        llistaFinal
                                )
                        );
                    });



                }
            });
        });

        System.out.println("-------------------FINAAAAAAAAAAAAAAAL-------------------");
        finalTicketsList.forEach(ticket -> {
            System.out.println("\nTicket from "+ticket.getDepartureStationId()+" to "+ticket.getArrivalStationId()+" at "+ticket.getDepartureTime());
            System.out.println("With route "+ticket.getTrainsList());
        });

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
        for(;i<rutaEstacions.size()-1;i++){
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
            if(prevArrivalTime.equals("")) break; // Si hem arribat al final, sortim

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
            tempsEsperaList.put(trains.get(0),0);
            // Sortim, perquè ja no hi ha més estacions a la ruta
            return;
        }

        // Si arribem aquí vol dir que hi ha més estacions, per tant cridem la recursiva perque vagi calculant els temps
        // d'espera.

        // Movem una posició l'iterador, per agafar el següent parell d'estacions
        i++;

        for (Train t : trains) {
            int tempsEspera = calculateTempsEsperaRecursive(ruta, t.getArrivalTime(), i);
            if(tempsEspera != -1) {//Mentre no haguem arribat al final
                //tempsEsperaList.add(t.getId(), tempsEspera);
                System.out.println("Temps espera per Ruta " + routeIndex + " amb tren inici " + t + ": " + tempsEspera);
                tempsEsperaList.put(t,tempsEspera);
            }
        }
    }

    private static int calculateTempsEsperaRecursive(List<String> ruta, String prevArrivalTime, int i) {
        // SI hem arribat al punt on (i == última estació) que retorni 0 com a temps d'espera d'aquest tram, perquè ja és
        // la última estació, i quan baixes ja no fas cap transbord més.
        if (i >= ruta.size()-1) return 0;

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

        if(winnerArrivalTime.equals("")) return -1;
        return tempsEspera + calculateTempsEsperaRecursive(ruta, winnerArrivalTime, i);
    }

    private static int getDuracio(String depStationId, String arrStationId) {
        for (Line l : lines) {
            if (l.getDepartureStation().getId().equals(depStationId) && l.getArrivalStation().getId().equals(arrStationId)) {
                return l.getDuration();
            }
        }
        return -1;
    }

    private static List<Train> getValidTrains(String stationId, String time) {
        List<Train> llista = new ArrayList<>();
        getAllTrainsLeavingFromStation(stationId).forEach(t -> {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy, HH:mm:ss");
            LocalDateTime departureTime = LocalDateTime.parse(t.getDepartureTime(), formatter);
            LocalDateTime prevStationArrTime = LocalDateTime.parse(time, formatter);

            // Si el tren en qüestió ja ha sortit quan naltros arribem
            // o el passatger s'ha d'esperar més de WAIT_TIME,
            // llavors marca la línia com a invàlida
            if (departureTime.isBefore(prevStationArrTime) || (ChronoUnit.MINUTES.between(departureTime, prevStationArrTime) > WAIT_TIME)) {
                t.setValid(false);
            }
            llista.add(t);

        });
        return llista;
    }

    private static void generarMapaBase() {
        stations.forEach(station -> {
            // Obtenir estacions adjacents
            Set<Adjacent> nextStations = getAdjacentStationsFromStation(station.getId());
            // Afegir-les al mapa com a valor de la seva clau corresponent
            mapaBase.put(station.getId(), nextStations);
        });
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
    private static Set<Adjacent> getAdjacentStationsFromStation(String stationId) {
        // Utilitzem un Set i no una List, perquè no volem tenir estacions repetides
        // Si tenim una llista amb 3 trains que van cap a Lausane, llavors acabaríem tenint una llista amb 3 estacions Lausane
        Set<Adjacent> nextStations = new HashSet<>();
        getAllTrainsLeavingFromStation(stationId).forEach(train -> {
            nextStations.add(new Adjacent(train.getLine().getArrivalStation().getId(), train.getLine().getDuration()));
        });
        return nextStations;
    }

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
    private static void addAdjacentStationToStationInMap(Map<String, List<Adjacent>> map, String key, List<Train> elementsToAdd) {
        List<Adjacent> llista = map.get(key);
        elementsToAdd.forEach(train -> {
            Adjacent a = new Adjacent(train.getLine().getDepartureStation().getId(), train.getLine().getDuration());
            if (!llista.contains(a)) {
                llista.add(a);
            }
        });
    }

//    public static void generateMapForTrain(Train t) {
//        // Get valid trains from current train
//        List<Train> list = getTrainsValid(t.getLine().getArrivalStation().getId(), t.getArrivalTime());
//        System.out.println(list);
//    }

    private static List<Train> getAllTrainsLeavingFromStation(String stationId) {
        List<Train> llista = new ArrayList<>();
        trains.forEach(t -> {
            if (t.getLine().getDepartureStation().getId().equals(stationId))
                llista.add(t);
        });
        return llista;
    }

    private static List<Train> getAllTrainsGoingFromTo(String depStationId, String arrStationId) {
        List<Train> llista = new ArrayList<>();
        trains.forEach(t -> {
            if (t.getLine().getDepartureStation().getId().equals(depStationId) && t.getLine().getArrivalStation().getId().equals(arrStationId))
                llista.add(t);
        });
        return llista;
    }


}