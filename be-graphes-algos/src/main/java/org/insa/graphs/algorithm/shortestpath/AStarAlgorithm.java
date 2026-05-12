package org.insa.graphs.algorithm.shortestpath;
import org.insa.graphs.model.Node;

public class AStarAlgorithm extends DijkstraAlgorithm {

    public AStarAlgorithm(ShortestPathData data) {
        super(data);
        labels = new LabelStar[data.getGraph().size()];
        labels[data.getOrigin().getId()] = new LabelStar(data.getOrigin());
        labels[data.getDestination().getId()] = new LabelStar(data.getDestination());
    }

    @Override
    protected void createLabel(Node n){
        labels[n.getId()] = new LabelStar(n);
    }

}
