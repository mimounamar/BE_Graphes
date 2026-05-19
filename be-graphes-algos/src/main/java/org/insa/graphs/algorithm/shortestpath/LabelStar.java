package org.insa.graphs.algorithm.shortestpath;
import org.insa.graphs.algorithm.ArcInspector;
import org.insa.graphs.algorithm.AbstractInputData.Mode;
import org.insa.graphs.model.Arc;
import org.insa.graphs.model.Node;

public class LabelStar extends Label {
    
    private double estimatedCost;


    public LabelStar(Node currentNode, Arc parent, double effectiveCost, Node destinationNode, Mode mode, int maximumSpeed) {
        super(currentNode, parent, effectiveCost, destinationNode);
        this.estimatedCost = currentNode.getPoint().distanceTo(destinationNode.getPoint());
        if (mode.equals(Mode.TIME)) {
            this.estimatedCost /= maximumSpeed;
        }
    }

 
    public double getEstimatedCost(){
        return this.estimatedCost;
    }


    @Override
    public double getCost(){
        return this.getEffectiveCost() + this.estimatedCost;
    }


    @Override
    public int compareTo(Label lab) {
        int res = Double.compare(this.getCost(), lab.getCost());
        if (res == 0){
            res = Double.compare(this.getEstimatedCost(), ((LabelStar) lab).getEstimatedCost());
        }
        return res;
    }

}
