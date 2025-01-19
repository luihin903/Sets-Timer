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

import java.io.Serializable;

public class Sets implements Serializable {

    transient MainActivity mainActivity;
    transient LinearLayout container;
    transient LayoutInflater inflater;
    private Set head;
    private Set tail;
    private Set playingSet;
    private String name;
    private boolean saved;
    private int numSet;

    public Sets(MainActivity mainActivity, LinearLayout container, LayoutInflater inflater) {
        this.mainActivity = mainActivity;
        this.container = container;
        this.inflater = inflater;
        this.saved = false;
        this.numSet = 0;
    }

    public void add(String name, Time time) {
        Set set = new Set(name, time);
        set.nameView.setText(name);
        set.timeView.setText(time.toString());
        container.addView(set.setView);

        if (head == null) {
            head = set;
        }
        else {
            tail.next = set;
        }
        tail = set;
        this.numSet ++;
    }

    public void start() {
        Log.d("DEBUG", name + " starts playing");
        if (head == null) return;
        Set set = head;
        set.start();
    }

    public void stop() {
        if (playingSet != null && playingSet.timer != null) {
            playingSet.timer.cancel();
        }
    }

    public void show() {
        container.removeAllViews();
        Set set = head;
        while (set != null) {
            set.setView = inflater.inflate(R.layout.item_set, container, false);
            set.nameView = set.setView.findViewById(R.id.name);
            set.timeView = set.setView.findViewById(R.id.time);
            set.nameView.setText(set.name);
            set.timeView.setText(set.time.toString());
            container.addView(set.setView);
            set = set.next;
        }
    }

    public void save(String name) {
        this.name = name;
        this.saved = true;
    }

    @Override
    public String toString() {
        return String.format("<%s (%d)>", name, numSet);
    }

    public String getName() {
        return this.name;
    }


    private class Set implements Serializable {

        transient View setView;
        transient TextView nameView;
        transient TextView timeView;
        transient CountDownTimer timer;
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
            playingSet = this;

            long millisecond = time.toMillisecond();
            timer = new CountDownTimer(millisecond, 1000) {
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

            if (next == null) {
                tone.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 400);
                head.start();
            }
            else {
                next.start();
            }
        }

        public String getName() {
            return this.name;
        }


    }

}
