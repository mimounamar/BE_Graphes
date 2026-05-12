package org.insa.graphs.algorithm.shortestpath;
import org.insa.graphs.model.Node;

public class LabelStar extends Label {
    
    private double coutEstime;

    public LabelStar(Node sommetCourant){
        super(sommetCourant);
    }

    public double getCoutEstime(){
        return coutEstime;
    }

    @Override
    public double getCout(){
        return this.getCoutRealise()+ coutEstime;
    }

    public int compareTo(LabelStar lab) {
        int res = Double.compare(this.getCout(), lab.getCout());
        if (res ==0){
            res = Double.compare(this.getCoutEstime(), lab.getCoutEstime());
        }
        return res;
    }

}
