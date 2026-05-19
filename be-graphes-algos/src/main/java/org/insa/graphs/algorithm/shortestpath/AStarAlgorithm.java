package org.insa.graphs.algorithm.shortestpath;
import org.insa.graphs.model.Arc;
import org.insa.graphs.model.Node;

public class AStarAlgorithm extends DijkstraAlgorithm {

    public AStarAlgorithm(ShortestPathData data) {
        super(data);
    }

    @Override
    protected Label createLabel(Node currentNode, Arc parent, double effectiveCost, Node destinationNode) {
        return new LabelStar(currentNode, parent, effectiveCost, destinationNode, this.getInputData().getMode(), this.getInputData().getMaximumSpeed());
    }

} 
