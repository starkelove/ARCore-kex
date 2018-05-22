package com.slagkryssaren.funwithcubez;

/*
CLASS CREATED BY LOVE & ANNA
 */

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

public class Check extends Activity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);

        SharedPreferences settings=getSharedPreferences("prefs",0);
        boolean firstRun=settings.getBoolean("firstRun",false);
        if(firstRun==false){
            //if running for first time
             //Splash will load for first time

            SharedPreferences.Editor editor=settings.edit();
            editor.putBoolean("firstRun",true);
            editor.putBoolean("benchMode", true);
            editor.putInt("maxAnchors", 20);
            editor.putInt("noOfBenchmarks", 1);
            editor.putInt("rateOfCollecting",2);
            editor.putInt("timeToBench", 20);
            editor.commit();
            Intent i=new Intent(Check.this,MainActivity.class);
            startActivity(i);
            finish();
        }
        else
        {

            Intent a=new Intent(Check.this,ArActivity.class);
            startActivity(a);
            finish();
        }
    }

}
