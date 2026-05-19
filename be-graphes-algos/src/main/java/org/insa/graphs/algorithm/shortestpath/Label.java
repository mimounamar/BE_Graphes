package org.insa.graphs.algorithm.shortestpath;

import java.util.List;

import org.insa.graphs.model.Arc;
import org.insa.graphs.model.Node;

public class Label implements Comparable<Label>{

    private Node currentNode;
    private Arc parent;
    private double effectiveCost;
    private boolean isMarked;
    private boolean isDestination;
    

    public Label(Node currentNode, Arc parent, double effectiveCost, Node destinationNode) {
        this.currentNode = currentNode;
        this.parent = parent;
        this.effectiveCost = effectiveCost;
        this.isMarked = false;
        this.isDestination = currentNode.equals(destinationNode);
    }


    public void update(Arc parent, double effectiveCost) {
        this.parent = parent;
        this.effectiveCost = effectiveCost;
    }


    public boolean isDestination() {
        return this.isDestination;
    }


    public List<Arc> getSuccessors() {
        return this.currentNode.getSuccessors();
    } 


    public void mark() {
        this.isMarked = true;
    }


    public boolean isMarked() {
        return this.isMarked;
    }


    public double getCost(){
        return this.effectiveCost;
    }


    public double getEffectiveCost() {
        return this.effectiveCost;
    }


    public Arc getParent() {
        return this.parent;
    }

    
    public int compareTo(Label lab) {
        return Double.compare(this.effectiveCost, lab.getEffectiveCost());
    }

}
