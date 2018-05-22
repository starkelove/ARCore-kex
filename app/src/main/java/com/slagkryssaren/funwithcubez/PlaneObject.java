package com.slagkryssaren.funwithcubez;

/*
CLASS CREATED BY LOVE & ANNA
 */

public class PlaneObject {
    private int aPlaneID;
    private String coordinateX;
    private String coordinateZ;
    private long timeStamp;

    public PlaneObject(int aPlaneID, String coordinateX, String coordinateZ, long timeStamp) {
        this.aPlaneID = aPlaneID;
        this.coordinateX = coordinateX;
        this.coordinateZ = coordinateZ;
        this.timeStamp = timeStamp;
    }
}
