package org.insa.graphs.algorithm.shortestpath;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import org.insa.graphs.algorithm.utils.BinaryHeap;
import org.insa.graphs.model.Arc;
import org.insa.graphs.model.Graph;
import org.insa.graphs.model.Node;
import org.insa.graphs.model.Path;

import org.insa.graphs.algorithm.AbstractSolution.Status;

public class DijkstraAlgorithm extends ShortestPathAlgorithm {

    public DijkstraAlgorithm(ShortestPathData data) {
        super(data);
    }

    @Override
    protected ShortestPathSolution doRun() {

        // retrieve data from the input problem (getInputData() is inherited from the
        // parent class ShortestPathAlgorithm)
        final ShortestPathData data = getInputData();

        // Retrieve the graph.
        Graph graph = data.getGraph();

        // variable that will contain the solution of the shortest path problem
        ShortestPathSolution solution = null;

        final int nbNodes = graph.size();

        // Initialize array of labels. TO DO : Hash table ? comparer gains réels
        Label[] labels = new Label[nbNodes];
        //Arrays.fill(labels, null);

        /* 
        for(int i = 0; i< nbNodes; i++){
            labels[i] = new Label(graph.get(i));
        }
        */

        BinaryHeap<Label> tas = new BinaryHeap<Label>();

        // Mettre dans la liste des labels l'origine et la destination.
        labels[data.getOrigin().getId()] = new Label(data.getOrigin());
        labels[data.getDestination().getId()] = new Label(data.getDestination());
        labels[data.getOrigin().getId()].setCoutRealise(0);
        tas.insert(labels[data.getOrigin().getId()]);

        // Notify observers about the first event (origin processed).
        notifyOriginProcessed(data.getOrigin());


        // Iterations Djikstra
        Label x = null;

        while(!tas.isEmpty()) { // condition problématique, remplacer par tas binaire vide
            x = tas.deleteMin();

            if (x.getSommetCourant().equals(data.getDestination())){
                break;
            }

            x.setMarque(true);

            for(Arc a : x.getSommetCourant().getSuccessors()){

                // Small test to check allowed roads...
                if (!data.isAllowed(a)) {
                    continue;
                }

                Node n = a.getDestination();
                Label y = labels[n.getId()];
                if (y == null) {
                    labels[n.getId()] = new Label(n);
                    y = labels[n.getId()];
                }
                if (!y.getMarque()){
                    if(y.getCoutRealise()> (x.getCoutRealise()+ data.getCost(a))){
                        try{
                            tas.remove(y);  

                        }catch(Exception e){
                            notifyNodeReached(n);
                        }
                        y.setCoutRealise((x.getCout()+ data.getCost(a)));
                        y.setPere(a);
                        tas.insert(y);
                        labels[n.getId()] = y;
                    }
                }  
            }
        }


        // Destination has no predecessor, the solution is infeasible...
        if (labels[data.getDestination().getId()].getPere() == null) {
            solution = new ShortestPathSolution(data, Status.INFEASIBLE);
        }
        else {
            // The destination has been found, notify the observers.
            notifyDestinationReached(data.getDestination());

            // Create the path from the array of predecessors...
            ArrayList<Arc> arcs = new ArrayList<>();
            Arc arc = labels[data.getDestination().getId()].getPere();
            while (arc != null) {
                arcs.add(arc);
                arc = labels[arc.getOrigin().getId()].getPere();
            }

            // Reverse the path...
            Collections.reverse(arcs);

            // Create the final solution.
            solution = new ShortestPathSolution(data, Status.OPTIMAL,
                    new Path(graph, arcs));
        }
        

        // when the algorithm terminates, return the solution that has been found
        return solution;
    }

}
