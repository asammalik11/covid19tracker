package com.hackathon.covid19tracker;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.Button;

public class Notify extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notify);

        Button reportButton = (Button) findViewById(R.id.notify);
        reportButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                switchNotify(1);
                finish();
            }
        });

        Button safeButton = (Button) findViewById(R.id.nonotify);
        safeButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                switchNotify(0);
                finish();
            }
        });
    }

    private void switchNotify(int status) {
        if (status == 0) {

        } else {

        }
    }
}