package com.tudresden.fairlaufen;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import androidx.appcompat.app.AppCompatActivity;

public class HomeActivity extends AppCompatActivity {
    RelativeLayout rl_citytour;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        showPopup();

        rl_citytour = (RelativeLayout) findViewById(R.id.citytour);
        rl_citytour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickStartCitytour();
            }
        });

    }

    public void onClickStartCitytour(){
        Intent intent = new Intent(this, CitytourActivity.class);
        startActivity(intent);
    }

    public void showPopup(){
        // create popup
        View popupView = getLayoutInflater().inflate(R.layout.popup_window, null);
        View mainView = findViewById(R.id.text_citytour).getRootView();

        //DisplayMetrics entnehmen die Eigenschaften des Displays des aktuellen Geräts --> width
        DisplayMetrics display = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(display);
        PopupWindow pw = new PopupWindow(popupView, display.widthPixels * 9/10 , 1120, true);
        pw.setFocusable(false);
        pw.setTouchable(true);
        pw.setOutsideTouchable(true);

        // show the popup window
        mainView.post(new Runnable() {
            public void run() {
                pw.showAtLocation(mainView,Gravity.CENTER, 0, 0);
            }
        });

        // dismiss the popup window when touched
        popupView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                pw.dismiss();
                return true;
            }
        });
    }
}