package com.luihin903.setstimer;

import android.content.Context;
import android.os.Bundle;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.util.Log;
import android.view.LayoutInflater;

import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.NumberPicker;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private Sets currentSets;
    private LinearLayout setsContainer;
    private LayoutInflater inflater;
    private ArrayList<Sets> list = new ArrayList<Sets>();
    private DrawerLayout drawer;

    private FloatingActionButton saveSetsButton, clearSetsButton, newSetsButton, deleteSetsButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MobileAds.initialize(this, status -> {});
        setContentView(R.layout.activity_main);

        // load banner ad
        AdView adView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);


        inflater = LayoutInflater.from(this);
        drawer = findViewById(R.id.drawer);
        setsContainer = findViewById(R.id.setsContainer);
        read();

        // find FAB's views
        FloatingActionButton add = findViewById(R.id.add);
        FloatingActionButton play = findViewById(R.id.play);
        FloatingActionButton menu = findViewById(R.id.menuButton);
        saveSetsButton = findViewById(R.id.saveSetsButton);
        clearSetsButton = findViewById(R.id.clearSetsButton);
        newSetsButton = findViewById(R.id.newSetsButton);
        newSetsButton.setVisibility(View.INVISIBLE);
        deleteSetsButton = findViewById(R.id.deleteSetsButton);
        deleteSetsButton.setVisibility(View.INVISIBLE);

        // set margins of FAB's
        int screenWidth = getResources().getDisplayMetrics().widthPixels;
        int screenHeight = getResources().getDisplayMetrics().heightPixels;
        int thinner = Math.min(screenWidth, screenHeight);
        int thinnerMargin = (int) (thinner * 0.10);
        int startMargin = (int) (screenHeight * 0.20);
        ViewGroup.MarginLayoutParams addParams = (ViewGroup.MarginLayoutParams) add.getLayoutParams();
        ViewGroup.MarginLayoutParams menuParams = (ViewGroup.MarginLayoutParams) menu.getLayoutParams();
        ViewGroup.MarginLayoutParams startParams = (ViewGroup.MarginLayoutParams) play.getLayoutParams();
        ViewGroup.MarginLayoutParams saveSetsButtonParams = (ViewGroup.MarginLayoutParams) saveSetsButton.getLayoutParams();
        ViewGroup.MarginLayoutParams clearSetsButtonParams = (ViewGroup.MarginLayoutParams) clearSetsButton.getLayoutParams();
        ViewGroup.MarginLayoutParams newSetsButtonParams = (ViewGroup.MarginLayoutParams) newSetsButton.getLayoutParams();
        ViewGroup.MarginLayoutParams deleteSetsButtonParams = (ViewGroup.MarginLayoutParams) deleteSetsButton.getLayoutParams();
        addParams.setMargins(addParams.leftMargin, addParams.topMargin, thinnerMargin, thinnerMargin);
        menuParams.setMargins(thinnerMargin, menuParams.topMargin, menuParams.rightMargin, thinnerMargin);
        startParams.setMargins(startParams.leftMargin, startParams.topMargin, startParams.rightMargin, startMargin);
        saveSetsButtonParams.setMargins(thinnerMargin, saveSetsButtonParams.topMargin, saveSetsButtonParams.rightMargin, thinnerMargin);
        clearSetsButtonParams.setMargins(clearSetsButtonParams.leftMargin, clearSetsButtonParams.topMargin, thinnerMargin, thinnerMargin);
        newSetsButtonParams.setMargins(thinnerMargin, newSetsButtonParams.topMargin, newSetsButtonParams.rightMargin, thinnerMargin);
        deleteSetsButtonParams.setMargins(deleteSetsButtonParams.leftMargin, deleteSetsButtonParams.topMargin, thinnerMargin, thinnerMargin);

        // set actions on FAB's
        add.setOnClickListener(v -> addSet());
        play.setOnClickListener(v -> currentSets.start());
        menu.setOnClickListener(v -> drawer.openDrawer(GravityCompat.START));
        saveSetsButton.setOnClickListener(v -> saveSets());
        clearSetsButton.setOnClickListener(v -> clearSets());
        newSetsButton.setOnClickListener(v -> newSets());
        deleteSetsButton.setOnClickListener(v -> deleteSets());

        showSetsInDrawer();

    }

    private void addSet() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Set");
        LayoutInflater inflater = LayoutInflater.from(this);

        View dialogView = inflater.inflate(R.layout.dialog, null);
        EditText input = dialogView.findViewById(R.id.setNameInput);
        NumberPicker hourPicker = dialogView.findViewById(R.id.hourPicker);
        NumberPicker minutePicker = dialogView.findViewById(R.id.minutePicker);
        NumberPicker secondPicker = dialogView.findViewById(R.id.secondPicker);

        hourPicker.setMinValue(0);
        hourPicker.setMaxValue(23);
        minutePicker.setMinValue(0);
        minutePicker.setMaxValue(59);
        secondPicker.setMinValue(0);
        secondPicker.setMaxValue(59);

        builder.setView(dialogView);

        builder.setPositiveButton("Add", (dialog, which) -> {
            String name = input.getText().toString().trim();
            int hours = hourPicker.getValue();
            int minutes = minutePicker.getValue();
            int seconds = secondPicker.getValue();
            Time time = new Time(hours, minutes, seconds);

            currentSets.add(name, time);
            Log.d("DEBUG", "Event added: " + name);
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void saveSets() {
        View form = inflater.inflate(R.layout.form_sets, null);
        EditText editText = form.findViewById(R.id.setsNameInput);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Create Sets");
        builder.setView(form);
        builder.setPositiveButton("Create", (dialog, which) -> {
            currentSets.save(editText.getText().toString().trim());
            list.add(currentSets);
            save();
            showSetsInDrawer();
            saveSetsButton.setVisibility(View.INVISIBLE);
            clearSetsButton.setVisibility(View.INVISIBLE);
            newSetsButton.setVisibility(View.VISIBLE);
            deleteSetsButton.setVisibility(View.VISIBLE);
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
        drawer.closeDrawer(GravityCompat.START);
    }

    private void clearSets() {
        currentSets = new Sets(this, setsContainer, inflater);
        setsContainer.removeAllViews();
        drawer.closeDrawer(GravityCompat.START);
    }

    private void newSets() {
        currentSets = new Sets(this, setsContainer, inflater);
        setsContainer.removeAllViews();
        saveSetsButton.setVisibility(View.VISIBLE);
        clearSetsButton.setVisibility(View.VISIBLE);
        newSetsButton.setVisibility(View.INVISIBLE);
        deleteSetsButton.setVisibility(View.INVISIBLE);
        drawer.closeDrawer(GravityCompat.START);
    }

    private void deleteSets() {
        list.remove(currentSets);
        save();
        if (list.isEmpty()) {
            currentSets = new Sets(this, setsContainer, inflater);
            setsContainer.removeAllViews();
            saveSetsButton.setVisibility(View.VISIBLE);
            clearSetsButton.setVisibility(View.VISIBLE);
            newSetsButton.setVisibility(View.INVISIBLE);
            deleteSetsButton.setVisibility(View.INVISIBLE);
        }
        else {
            currentSets = list.get(0);
            currentSets.show();
        }
        showSetsInDrawer();
        drawer.closeDrawer(GravityCompat.START);
    }

    private void save() {
        try {
            FileOutputStream fileOutputStream = this.openFileOutput("sets.ser", Context.MODE_PRIVATE);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(list);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void read() {
        if (new File(this.getFilesDir(), "sets.ser").exists()) {
            try {
                FileInputStream fileInputStream = this.openFileInput("sets.ser");
                ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
                list = (ArrayList<Sets>) objectInputStream.readObject();
                for (Sets sets : list) {
                    sets.mainActivity = this;
                    sets.container = setsContainer;
                    sets.inflater = inflater;
                }
                Log.d("DEBUG", list.toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        currentSets = new Sets(this, setsContainer, inflater);
    }

    private void showSetsInDrawer() {
        LinearLayout buttonsContainer = findViewById(R.id.buttonsContainer);
        buttonsContainer.removeAllViews();
        Log.d("DEBUG", list.toString());
        for (Sets sets : list) {
            Button button = new Button(this);
            button.setText(sets.getName());
            button.setLayoutParams(new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            ));
            button.setOnClickListener(v -> {
                currentSets.stop();
                currentSets = sets;
                sets.show();
                saveSetsButton.setVisibility(View.INVISIBLE);
                clearSetsButton.setVisibility(View.INVISIBLE);
                newSetsButton.setVisibility(View.VISIBLE);
                deleteSetsButton.setVisibility(View.VISIBLE);
                drawer.closeDrawer(GravityCompat.START);
            });
            buttonsContainer.addView(button);
        }
    }

}