package com.slagkryssaren.funwithcubez;

/*
CLASS CREATED BY LOVE & ANNA

Class that creates BenchObjects

 */

import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import com.google.gson.Gson;

public class BenchObjects extends AppCompatActivity {
    ArrayList<Long> pointTimes;
    ArrayList<Integer> numberOfPoints;
    ArrayList<Long> planeTimes;
    ArrayList<String> coordinatesX;
    ArrayList<String> coordinatesZ;


    public BenchObjects(){

    }

    public BenchObjects(ArrayList<Long> times, ArrayList<Integer> number){
        this.pointTimes = times;
        this.numberOfPoints = number;
    }

    public BenchObjects(ArrayList<Long> times, ArrayList<String> s1, ArrayList<String> s2){
        this.planeTimes = times;
        this.coordinatesX = s1;
        this.coordinatesZ = s2;
    }

    public File createGson(ArrayList<PointObject> pointObjects, ArrayList<PlaneObject> planeObjects, int numberOfBenchmarks) throws IOException {
        Gson gson = new Gson();
        ArrayList<Object> ar = new ArrayList<>();
        ar.add(pointObjects);
        ar.add(planeObjects);
        String jsonString = gson.toJson(ar);

        File file;
        try {
            file = new File(Environment.getExternalStorageDirectory(), "benchmark" + Integer.toString(numberOfBenchmarks) + ".json");
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write(jsonString);
            fileWriter.close();
            return file;
        } catch (IOException e) {
            Log.e("ERRR", "Could not create file",e);
            e.printStackTrace();
        }

        return null;
    }

}
