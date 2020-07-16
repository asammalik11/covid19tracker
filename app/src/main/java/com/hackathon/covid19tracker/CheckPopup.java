package com.hackathon.covid19tracker;

import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.widget.TextView;

import androidx.annotation.Nullable;

public class CheckPopup extends Activity {
    private Object VolleyCallback;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.checkself);

        DisplayMetrics dm  = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;
        getWindow().setLayout( (int) (width * .7 ),  (int) (height * .5) );

       // CloudantDB.getRemoteDB(getApplicationContext(), MainActivity.docMain, VolleyCallback);
        int notify = 1;

        TextView status = (TextView) findViewById (R.id.selfstatus);
        if (notify == 1) {
            status.setText("Warning: someone has reported symptoms of COVID-19 near you");

        } else {
            status.setText("No reports near you!");
        }
    }
}
