package com.slagkryssaren.funwithcubez;

/*
CLASS CREATED BY LOVE & ANNA
 */

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class Settings extends AppCompatActivity implements View.OnClickListener {

    Button inc, dec, half, each, thedouble, ten, twenty, thirty, bench;
    TextView result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        inc =(Button)findViewById(R.id.button2);
        dec = (Button)findViewById(R.id.button3);
        half = (Button)findViewById(R.id.button4);
        each = (Button)findViewById(R.id.button5);
        thedouble = (Button)findViewById(R.id.button6);
        ten = (Button)findViewById(R.id.button7);
        twenty = (Button)findViewById(R.id.button8);
        thirty = (Button)findViewById(R.id.button9);
        bench = (Button)findViewById(R.id.button10);

        result = (TextView)findViewById(R.id.textView5);

        inc.setOnClickListener(this);
        dec.setOnClickListener(this);
        half.setOnClickListener(this);
        each.setOnClickListener(this);
        thedouble.setOnClickListener(this);
        ten.setOnClickListener(this);
        twenty.setOnClickListener(this);
        thirty.setOnClickListener(this);
        bench.setOnClickListener(this);


        SharedPreferences settings=getSharedPreferences("prefs",0);
        int number = settings.getInt("maxAnchors", 20);
        result.setText("Current number of anchors : " + number);


    }

    @Override
    public void onClick(View v)
    {
        SharedPreferences settings=getSharedPreferences("prefs",0);
        int number = settings.getInt("maxAnchors", 20);
        boolean benchMode = settings.getBoolean("benchMode", true);

        boolean showText = false;
        SharedPreferences.Editor editor=settings.edit();
        switch(v.getId())
        {
            case R.id.button2:  number--;editor.putInt("maxAnchors", number);editor.commit(); showText = true; break;
            case R.id.button3:  number++;editor.putInt("maxAnchors", number);editor.commit(); showText = true; break;
            case R.id.button4:  editor.putInt("rateOfCollecting", 4);editor.commit();Log.d("BUTTON", Integer.toString(1/2)); showText = true; break;
            case R.id.button5:  editor.putInt("rateOfCollecting", 2);editor.commit(); Log.d("BUTTON", Integer.toString(1));showText = true; break;
            case R.id.button6:  editor.putInt("rateOfCollecting", 1);editor.commit(); Log.d("BUTTON", Integer.toString(2)); showText = true; break;
            case R.id.button7:  editor.putInt("timeToBench", 10);editor.commit(); Log.d("BUTTON", Integer.toString(10));showText = true; break;
            case R.id.button8:  editor.putInt("timeToBench", 20);editor.commit(); Log.d("BUTTON", Integer.toString(20));showText = true; break;
            case R.id.button9:  editor.putInt("timeToBench", 30);editor.commit();
                Log.d("BUTTON", Integer.toString(30)); showText = true; break;
            case R.id.button10:
                if(benchMode) {
                    editor.putBoolean("benchMode", false);
                }
                if (!benchMode) {
                    editor.putBoolean("benchMode", true);
                }
                editor.commit();
                Log.d("BUTTON", Integer.toString(30)); showText = true; break;
        }
        if(showText)
            result.setText("Current number of anchors  : "+ number);
    }


    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}
