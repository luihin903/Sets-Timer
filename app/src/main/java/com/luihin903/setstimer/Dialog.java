package com.luihin903.setstimer;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.NumberPicker;

import androidx.appcompat.app.AlertDialog;

public class Dialog {

    private MainActivity mainActivity;
    private AlertDialog.Builder builder;
    private Sets sets;

    public Dialog(MainActivity mainActivity, Sets sets) {
        this.mainActivity = mainActivity;
        this.builder = new AlertDialog.Builder(mainActivity);
        this.sets = sets;
    }

    public void show() {
        Log.d("DEBUG", "fab clicked");
        builder.setTitle("Add Set");

        LayoutInflater inflater = LayoutInflater.from(mainActivity);
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

            sets.add(name, time);
            Log.d("DEBUG", "Event added: " + name);
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }

}
