package org.insa.graphs.algorithm.shortestpath;

import java.util.ArrayList;
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

    protected Label[] initLabels(int length) {
        return new Label[length];
    }

    protected Label createLabel(Node currentNode, Arc parent, double effectiveCost, Node destinationNode) {
        return new Label(currentNode, parent, effectiveCost, destinationNode);
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

        // Insert the lables (origin and destination) into the array and heap
        Label[] labels = initLabels(data.getGraph().size());
        BinaryHeap<Label> heap = new BinaryHeap<Label>();
        labels[data.getOrigin().getId()] = createLabel(data.getOrigin(), null, 0, data.getDestination());
        heap.insert(labels[data.getOrigin().getId()]);
        labels[data.getDestination().getId()] = createLabel(data.getDestination(), null, Double.POSITIVE_INFINITY, data.getDestination());

        // Notify observers about the first event (origin processed).
        notifyOriginProcessed(data.getOrigin());

        // Iterations Djikstra
        while(!heap.isEmpty()) {
            // Get next node.
            Label x = heap.deleteMin();
            if (x.isDestination()){
                break;
            }
            x.mark();

            // Go through the successors and update if needed.
            for(Arc a : x.getSuccessors()){
                // Small test to check allowed roads...
                if (!data.isAllowed(a)) {
                    continue;
                }

                // Handle the successors...
                Node n = a.getDestination();
                Label y = labels[n.getId()];
                if (y == null) {
                    labels[n.getId()] = createLabel(n, null, Double.POSITIVE_INFINITY, data.getDestination());
                    y = labels[n.getId()];
                }
                if (!y.isMarked()) {
                    if(y.getEffectiveCost() > (x.getEffectiveCost() + data.getCost(a))){
                        try {
                            heap.remove(y);  
                        }catch(Exception e){
                            notifyNodeReached(n);
                        }
                        y.update(a, x.getEffectiveCost() + data.getCost(a));
                        heap.insert(y);
                    }
                }  
            }
        }

        // Destination has no predecessor, the solution is infeasible...
        if (labels[data.getDestination().getId()].getParent() == null) {
            solution = new ShortestPathSolution(data, Status.INFEASIBLE);
        }
        else {
            // The destination has been found, notify the observers.
            notifyDestinationReached(data.getDestination());

            // Create the path from the array of predecessors...
            ArrayList<Arc> arcs = new ArrayList<>();
            Arc arc = labels[data.getDestination().getId()].getParent();
            while (arc != null) {
                arcs.add(arc);
                arc = labels[arc.getOrigin().getId()].getParent();
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
