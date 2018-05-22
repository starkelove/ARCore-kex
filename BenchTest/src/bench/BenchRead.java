package bench;

/*
PROGRAM CREATED BY LOVE & ANNA

Program to read .json files used in the FunWithCubez project
The program calculates the mean value of the specified number of tests
The path to the files, the starting file number (minus 1) and how many files
that will be read needs to be specified.

 */

import com.google.gson.stream.JsonReader;

import java.io.*;


public class BenchRead{

    public int[] meanPointsPerSecond = new int[60];
    public int meanPointsOverall;

    public float[] meanPlanePerSecondX = new float[60];
    public float[] meanPlanePerSecondZ = new float[60];
    public float meanPlaneOverallX;
    public float meanPlaneOverallZ;
    public long timeToDetectPlane;

    public long startTime, endTime;


    public void fileRead(int currentFile){
        int currentPointValue, i;
        StringBuilder sb = new StringBuilder("/home/love/FunWithCubezProjekt/funwithcubez/BenchTest/src/bench/benchmark");
        sb.append(currentFile);
        sb.append(".json");
        String fileName = sb.toString();
        try{
            JsonReader reader = new JsonReader(new FileReader(fileName));
            i = 0;
            reader.beginArray();
            reader.beginArray();
            while (reader.hasNext()){
                reader.beginObject();
                reader.nextName();
                currentPointValue = reader.nextInt();
                meanPointsPerSecond[i] += currentPointValue;
                meanPointsOverall += currentPointValue;
                i++;
                reader.nextName();
                reader.nextLong();
                reader.endObject();
            }
            reader.endArray();
            i = 0;
            reader.beginArray();
            boolean firstRun = true;
            boolean firstPlane = false;
            while (reader.hasNext()){
                reader.beginObject();
                reader.nextName();
                int id = reader.nextInt();
                reader.nextName();
                float valueX = Float.valueOf(reader.nextString());
                meanPlanePerSecondX[i] += valueX;
                meanPlaneOverallX += valueX;
                reader.nextName();
                float valueZ = Float.valueOf(reader.nextString());
                meanPlanePerSecondZ[i] += valueZ;
                meanPlaneOverallZ += valueZ;
                i++;
                reader.nextName();
                if(firstRun){
                    startTime = reader.nextLong();
                    firstRun = false;
                }else if(id != 0 && !firstPlane){
                    firstPlane = true;
                    endTime = reader.nextLong();
                    timeToDetectPlane += endTime - startTime;

                }else{
                    reader.nextLong();
                }
                reader.endObject();
            }
            reader.close();
        }catch (IOException e){
            e.printStackTrace();
        }

    }

    public void calculateMean(int numberOfFiles, int numberOfResults){
        for(int i = 0; i <numberOfResults; i++){
            meanPointsPerSecond[i] /= numberOfFiles;
            meanPlanePerSecondX[i] /= numberOfFiles;
            meanPlanePerSecondZ[i] /= numberOfFiles;
        }

        meanPointsOverall /= numberOfResults*numberOfFiles;
        meanPlaneOverallX /= numberOfResults*numberOfFiles;
        meanPlaneOverallZ /= numberOfResults*numberOfFiles;

        timeToDetectPlane /= numberOfFiles;
    }

    public void printResults(int numberOfResults){
        try {
            FileWriter fw = new FileWriter("benchresults.txt");
            PrintWriter writer = new PrintWriter(fw);
            writer.println("Mean values of points ");

            for(int i = 0; i < numberOfResults; i++){
                writer.println(meanPointsPerSecond[i]);
            }

            writer.println();
            writer.println("Mean points overall " + meanPointsOverall);

            writer.println();
            writer.println("Mean points per second X ");
            for(int i = 0; i < numberOfResults; i++){
                writer.println(meanPlanePerSecondX[i]);
            }

            writer.println();
            writer.println("Mean points per second Z ");
            for(int i = 0; i < numberOfResults; i++){
                writer.println(meanPlanePerSecondZ[i]);
            }

            writer.println();
            writer.println("Mean plane X overall " + meanPlaneOverallX);
            writer.println("Mean plane Z overall " + meanPlaneOverallZ);
            writer.println();
            writer.println("Average time to detect plane " + timeToDetectPlane);
            writer.close();
            fw.close();
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args){

        BenchRead bench = new BenchRead();
        int currentFile = 308;
        int numberOfResults = 20;
        int numberOfFiles = 5;
        for(int i = 0; i < numberOfFiles; i ++){
            currentFile++;
            bench.fileRead(currentFile);

        }

        bench.calculateMean(numberOfFiles, numberOfResults);
        bench.printResults(numberOfResults);
        System.out.println("Done!");


    }

}