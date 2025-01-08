package com.luihin903.setstimer;

import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.app.AppCompatActivity;

import android.view.LayoutInflater;

import android.view.ViewGroup;
import android.widget.LinearLayout;

public class MainActivity extends AppCompatActivity {

    private Sets sets;
    private Dialog dialog;
    private FloatingActionButton add;
    private FloatingActionButton start;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LinearLayout setsContainer = findViewById(R.id.setsContainer);
        sets = new Sets(this, setsContainer, LayoutInflater.from(this));
        dialog = new Dialog(this, sets);

        add = findViewById(R.id.add);
        start = findViewById(R.id.start);

        int screenWidth = getResources().getDisplayMetrics().widthPixels;
        int screenHeight = getResources().getDisplayMetrics().heightPixels;
        int thinner = Math.min(screenWidth, screenHeight);
        int thinnerMargin = (int) (thinner * 0.10);
        int startMargin = (int) (screenHeight * 0.20);
        ViewGroup.MarginLayoutParams addParams = (ViewGroup.MarginLayoutParams) add.getLayoutParams();
        ViewGroup.MarginLayoutParams startParams = (ViewGroup.MarginLayoutParams) start.getLayoutParams();
        addParams.setMargins(addParams.leftMargin, addParams.topMargin, thinnerMargin, thinnerMargin);
        startParams.setMargins(startParams.leftMargin, startParams.topMargin, startParams.rightMargin, startMargin);


        add.setOnClickListener(v -> dialog.show());
        start.setOnClickListener(v -> sets.start());

    }

}