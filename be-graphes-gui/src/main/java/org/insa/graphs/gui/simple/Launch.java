package org.insa.graphs.gui.simple;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import org.insa.graphs.algorithm.AbstractAlgorithm;
import org.insa.graphs.algorithm.AlgorithmFactory;
import org.insa.graphs.algorithm.ArcInspector;
import org.insa.graphs.algorithm.ArcInspectorFactory;
import org.insa.graphs.algorithm.shortestpath.AStarAlgorithm;
import org.insa.graphs.algorithm.shortestpath.BellmanFordAlgorithm;
import org.insa.graphs.algorithm.shortestpath.DijkstraAlgorithm;
import org.insa.graphs.algorithm.shortestpath.ShortestPathAlgorithm;
import org.insa.graphs.algorithm.shortestpath.ShortestPathData;
import org.insa.graphs.algorithm.shortestpath.ShortestPathSolution;
import org.insa.graphs.gui.drawing.Drawing;
import org.insa.graphs.gui.drawing.components.BasicDrawing;
import org.insa.graphs.model.Graph;
import org.insa.graphs.model.Path;
import org.insa.graphs.model.io.BinaryGraphReader;
import org.insa.graphs.model.io.BinaryPathReader;
import org.insa.graphs.model.io.GraphReader;
import org.insa.graphs.model.io.PathReader;

public class Launch {

    /**
     * Create a new Drawing inside a JFrame an return it.
     *
     * @return The created drawing.
     * @throws Exception if something wrong happens when creating the graph.
     */
    public static Drawing createDrawing() throws Exception {
        BasicDrawing basicDrawing = new BasicDrawing();
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override
            public void run() {
                JFrame frame = new JFrame("BE Graphes - Launch");
                frame.setLayout(new BorderLayout());
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setVisible(true);
                frame.setSize(new Dimension(800, 600));
                frame.setContentPane(basicDrawing);
                frame.validate();
            }
        });
        return basicDrawing;
    }

    // Pour la comparaison de float (coût des PCCs).
    private static float testThreshold = 0.001f;

    /**
     * Fonction de test pour le cas suivant : aucun chemin n'existe.
     * 
     * Deux examples :
     * 1. Traverser deux îles de la Nouvelle-Zélande (sans filtre) qui ne sont pas reliées entre elles.
     * 2. Longer le canal (en voiture) alors que le canal du Midi n'est pas accessible en voiture.
     * 
     * @param testedAlgorithm
     * @throws Exception
     */
    private static void testPathDoesNotExist(Class<? extends AbstractAlgorithm<?>> testedAlgorithm) throws Exception {
        // Chargement de la carte de test numéro 1 (NZ).
        Graph graph;
        try (final GraphReader reader = new BinaryGraphReader(new DataInputStream(
                new BufferedInputStream(Launch.class.getResourceAsStream("/maps/new-zealand.mapgr"))))) {

            graph = reader.read();
        }

        // Construction du ShortestPathData
        List<ArcInspector> inspectors = ArcInspectorFactory.getAllFilters();
        ShortestPathData data = new ShortestPathData(graph, graph.get(171884), graph.get(225173), inspectors.get(0));
        ShortestPathAlgorithm algorithm = (ShortestPathAlgorithm) AlgorithmFactory.createAlgorithm(testedAlgorithm, data);
        ShortestPathSolution output = algorithm.run();

        // Vérifier qu'aucune solution n'est trouvée.
        if (output.getStatus() == ShortestPathSolution.Status.INFEASIBLE) {
            System.out.println("[TEST nº1] testPathDoesNotExist avec Nouvelle-Zélande : succès.");
        } else {
            System.err.println("[TEST nº1] testPathDoesNotExist avec Nouvelle-Zélande : échec (#looser).");
        }


        // Chargement de la carte de test numéro 2 (canal en voiture).
        try (final GraphReader reader = new BinaryGraphReader(new DataInputStream(
                new BufferedInputStream(Launch.class.getResourceAsStream("/maps/insa.mapgr"))))) {

            graph = reader.read();
        }

        // Construction du ShortestPathData
        data = new ShortestPathData(graph, graph.get(317), graph.get(829), inspectors.get(1));
        algorithm = (ShortestPathAlgorithm) AlgorithmFactory.createAlgorithm(testedAlgorithm, data);
        output = algorithm.run();

        // Vérifier qu'aucune solution n'est trouvée.
        if (output.getStatus() == ShortestPathSolution.Status.INFEASIBLE) {
            System.out.println("[TEST nº2] testPathDoesNotExist avec bord du canal du Midi : succès.");
        } else {
            System.err.println("[TEST nº2] testPathDoesNotExist avec avec bord du canal du Midi : échec (#looser).");
        }
    }


    /**
     * Fonction de test lorsque la longueur est nulle (ie. origine = destination).
     * 
     * @param testedAlgorithm
     * @throws Exception
     */
    private static void testPathIsNull(Class<? extends AbstractAlgorithm<?>> testedAlgorithm) throws Exception {
        // Chargement de la carte de test (INSA).
        Graph graph;
        try (final GraphReader reader = new BinaryGraphReader(new DataInputStream(
                new BufferedInputStream(Launch.class.getResourceAsStream("/maps/insa.mapgr"))))) {

            graph = reader.read();
        }

        // Construction du ShortestPathData
        List<ArcInspector> inspectors = ArcInspectorFactory.getAllFilters();
        ShortestPathData data = new ShortestPathData(graph, graph.get(377), graph.get(377), inspectors.get(0));
        ShortestPathAlgorithm algorithm = (ShortestPathAlgorithm) AlgorithmFactory.createAlgorithm(testedAlgorithm, data);
        ShortestPathSolution output = algorithm.run();

        // Vérifier qu'aucune solution n'est trouvée.
        if (output.getStatus() == ShortestPathSolution.Status.INFEASIBLE) {
            System.out.println("[TEST nº3] testPathIsNull : succès.");
        } else {
            System.err.println("[TEST nº3] testPathIsNull : échec (#looser).");
        }

    }


    /**
     * Tester des cartes de taille raisonnable (comparaison avec le résultat de Bellman-Ford).
     * 
     * Deux examples :
     * 1. Carte INSA en cherchant le chemin le plus court (en longueur).
     * 2. Carte Toulouse en cherchant le chemin le plus rapide.
     * 
     * @param testedAlgorithm
     * @throws Exception
     */
    private static void testReasonableMap(Class<? extends AbstractAlgorithm<?>> testedAlgorithm) throws Exception{
        // Chargement de la carte de test 1 + comparaison de longueur (INSA).
        Graph graph;
        try (final GraphReader reader = new BinaryGraphReader(new DataInputStream(
                new BufferedInputStream(Launch.class.getResourceAsStream("/maps/insa.mapgr"))))) {

            graph = reader.read();
        }

        // Construction du ShortestPathData
        List<ArcInspector> inspectors = ArcInspectorFactory.getAllFilters();
        ShortestPathData data = new ShortestPathData(graph, graph.get(422), graph.get(143), inspectors.get(1));
        ShortestPathAlgorithm algorithm = (ShortestPathAlgorithm) AlgorithmFactory.createAlgorithm(testedAlgorithm, data);
        ShortestPathSolution output = algorithm.run();

        // Calcul du chemin référence avec Bellman-Ford
        ShortestPathAlgorithm bellmanFord = new BellmanFordAlgorithm(data);
        ShortestPathSolution bellmanFordOutput = bellmanFord.run();

        // Vérifier qu'aucune solution n'est trouvée.
        if (output.getStatus() == ShortestPathSolution.Status.OPTIMAL 
                && Math.abs(output.getPath().getLength() - bellmanFordOutput.getPath().getLength()) < testThreshold) {
            System.out.println("[TEST nº4] testReasonableMap avec INSA : succès.");
        } else {
            System.err.println("[TEST nº4] testReasonableMap avec INSA : échec (#looser).");
        }


        // Chargement de la carte de test 2 + comparaison de vitesse (Toulouse).
        try (final GraphReader reader = new BinaryGraphReader(new DataInputStream(
                new BufferedInputStream(Launch.class.getResourceAsStream("/maps/toulouse.mapgr"))))) {

            graph = reader.read();
        }

        // Construction du ShortestPathData
        data = new ShortestPathData(graph, graph.get(1239), graph.get(12489), inspectors.get(2));
        algorithm = (ShortestPathAlgorithm) AlgorithmFactory.createAlgorithm(testedAlgorithm, data);
        output = algorithm.run();

        // Calcul du chemin référence avec Bellman-Ford
        bellmanFord = new BellmanFordAlgorithm(data);
        bellmanFordOutput = bellmanFord.run();

        // Vérifier qu'aucune solution n'est trouvée.
        if (output.getStatus() == ShortestPathSolution.Status.OPTIMAL 
                && Math.abs(output.getPath().getMinimumTravelTime() - bellmanFordOutput.getPath().getMinimumTravelTime()) < testThreshold) {
            System.out.println("[TEST nº5] testReasonableMap avec Toulouse : succès.");
        } else {
            System.err.println("[TEST nº5] testReasonableMap avec Toulouse : échec (#looser).");
        }
    }


    /**
     * Fonction qui fait l'appel à tous les tests.
     * 
     * @param testedAlgorithm
     * @throws Exception
     */
    private static void executeTestCases(Class<? extends AbstractAlgorithm<?>> testedAlgorithm) throws Exception {
        testPathDoesNotExist(testedAlgorithm);
        testPathIsNull(testedAlgorithm);
        testReasonableMap(testedAlgorithm);
        // TO DO : quand Dijsktra optimisé, grande map.
    }

    public static void main(String[] args) throws Exception {

        // visit these directory to see the list of available files on commetud.
        final String mapName =
                "/mnt/commetud/3eme Annee MIC/Graphes-et-Algorithmes/Maps/insa.mapgr";
        final String pathName =
                "/mnt/commetud/3eme Annee MIC/Graphes-et-Algorithmes/Paths/path_fr31insa_rangueil_r2.path";

        final Graph graph;
        final Path path;

        // create a graph reader
        try (final GraphReader reader = new BinaryGraphReader(new DataInputStream(
                new BufferedInputStream(new FileInputStream(mapName))))) {

            graph = reader.read();
        }

        // create the drawing
        final Drawing drawing = createDrawing();

        drawing.drawGraph(graph);

        try (final PathReader pathReader = new BinaryPathReader(new DataInputStream(
                new BufferedInputStream(new FileInputStream(pathName))))) {

            path = pathReader.readPath(graph);
        }

        drawing.drawPath(path);

        executeTestCases(DijkstraAlgorithm.class);
        executeTestCases(AStarAlgorithm.class);
    }

}
