package com.acmerobotics.dashboard.canvas;

import java.util.ArrayList;

public class GPolygon {
    ArrayList<GVector> vertexes = new ArrayList<>();

    public GPolygon(GVector... vertexes){
        for(GVector vertex: vertexes){
            this.vertexes.add(vertex);
        }
    }

    public GPolygon(ArrayList<GVector> vertexes){
        this.vertexes = vertexes;
    }

    ArrayList<GVector> getVertexes(){
        return vertexes;
    }

    void rotate(double theta){
        for(int i = 0;i < vertexes.size(); i++){
            vertexes.set(i,GVector.rotateBy(vertexes.get(i), theta));
        }
    }

}
