package com.luihin903.setstimer;

import android.graphics.Color;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class Sets implements Serializable {

    transient MainActivity mainActivity;
    transient LinearLayout container;
    transient LayoutInflater inflater;
    private Set head;
    private Set tail;

    public Sets(MainActivity mainActivity, LinearLayout container, LayoutInflater inflater) {
        this.mainActivity = mainActivity;
        this.container = container;
        this.inflater = inflater;

        read();
    }

    public void add(String name, Time time) {
        Set set = new Set(name, time);
        set.nameView.setText(name);
        set.timeView.setText(time.toString());
        container.addView(set.getSetView());

        if (head == null) {
            head = set;
            tail = set;
        }
        else {
            tail.next = set;
            tail = set;
        }

        save();
    }

    public void start() {
        if (head == null) return;
        Set set = head;
        set.start();
    }

    private void save() {
        try {
            FileOutputStream fileOutputStream = mainActivity.openFileOutput("sets.ser", mainActivity.MODE_PRIVATE);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(this);
            Log.d("DEBUG", "Saved");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void read() {
        Log.d("DEBUG", "Read " + mainActivity.getFilesDir().toString());
        if (new File(mainActivity.getFilesDir(), "sets.ser").exists()) {
            try {
                FileInputStream fileInputStream = mainActivity.openFileInput("sets.ser");
                ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
                Sets sets = (Sets) objectInputStream.readObject();
                this.head = sets.head;
                this.tail = sets.tail;
            } catch (Exception e) {
                e.printStackTrace();
            }
            Set set = head;
            while (set != null) {
                set.setView = inflater.inflate(R.layout.item_set, container, false);
                set.nameView = set.setView.findViewById(R.id.name);
                set.timeView = set.setView.findViewById(R.id.time);
                set.nameView.setText(set.name);
                set.timeView.setText(set.time.toString());
                container.addView(set.getSetView());
                set = set.next;
            }
        }
    }

    private class Set implements Serializable {

        transient View setView;
        transient TextView nameView;
        transient TextView timeView;
        private String name;
        private Time time;
        private Set next;

        public Set(String name, Time time) {
            this.name = name;
            this.time = time;
            this.setView = inflater.inflate(R.layout.item_set, container, false);
            this.nameView = setView.findViewById(R.id.name);
            this.timeView = setView.findViewById(R.id.time);
        }

        public void start() {
            this.timeView.setTextColor(Color.BLACK);
            long millisecond = time.toMillisecond();

            CountDownTimer timer = new CountDownTimer(millisecond, 1000) {
                @Override
                public void onTick(long left) {
                    int hour = (int) (left / 3600 / 1000);
                    int minute = (int) (left % (3600 * 1000) / 60 / 1000);
                    int second = (int) (left % (60 * 1000) / 1000);
                    String time = new Time(hour, minute, second).toString();
                    timeView.setText(time);
                }

                @Override
                public void onFinish() {
                    end();
                }
            };

            timer.start();
        }

        public void end() {
            this.nameView.setTextColor(Color.GRAY);
            this.timeView.setTextColor(Color.GRAY);
            this.timeView.setText(this.time.toString());

            ToneGenerator tone = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);
            tone.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD,200);

            if (next != null) next.start();
        }

        public String getName() {
            return this.name;
        }

        public View getSetView() {
            return this.setView;
        }

    }

}
