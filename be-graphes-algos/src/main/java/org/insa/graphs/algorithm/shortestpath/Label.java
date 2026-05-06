package org.insa.graphs.algorithm.shortestpath;

import org.insa.graphs.model.Arc;
import org.insa.graphs.model.Node;

public class Label implements Comparable<Label>{
    
    private Node sommetCourant;
    private boolean marque;
    private double coutRealise;
    private Arc pere;

    public Label(Node sommetCourant){
        this.sommetCourant = sommetCourant;
        this.marque = false;
        this.coutRealise = Double.POSITIVE_INFINITY;
        this.pere = null;
    }

    public Node getSommetCourant (){
        return sommetCourant;
    }

    public boolean getMarque(){
        return marque;
    }

    public void setMarque(boolean b){
        this.marque = b;
    }

    public double getCoutRealise(){
        return coutRealise;
    }

    public void setCoutRealise(double cout){
        this.coutRealise = cout;
    }

    public double getCout(){
        // changera surement plus tard ...
        return coutRealise;
    }

    public void setPere(Arc p){
        this.pere = p;
    }
    
    public Arc getPere(){
        return pere;
    }

    public int compareTo(Label lab) {
        return Double.compare(this.coutRealise, lab.getCoutRealise());
    }

}
