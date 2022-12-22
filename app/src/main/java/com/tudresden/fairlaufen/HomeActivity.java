package com.tudresden.fairlaufen;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import androidx.appcompat.app.AppCompatActivity;

public class HomeActivity extends AppCompatActivity {
    RelativeLayout rl_citytour;
    View popupView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //onStart();
        showPopup(popupView);

        rl_citytour = (RelativeLayout) findViewById(R.id.citytour);
        rl_citytour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickStartCitytour();
            }
        });
    }

    /*@Override
    protected void onStart() {
        super.onStart();
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.popup_window, null);
        PopupWindow pw = new PopupWindow(popupView, 300, 300, true);
        pw.setFocusable(false);
        pw.setTouchable(true);
        pw.setOutsideTouchable(true);
        pw.showAtLocation(popupView, Gravity.CENTER, 0, 0);

        // dismiss the popup window when touched
        popupView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                pw.dismiss();
                return true;
            }
        });
    }*/

    public void onClickStartCitytour(){
        Intent intent = new Intent(this, CitytourActivity.class);
        startActivity(intent);
    }

    public void showPopup(View view){
        // create popup
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.popup_window, null);
        PopupWindow pw = new PopupWindow(popupView, 300, 300, true);
        pw.setFocusable(false);
        pw.setTouchable(true);
        pw.setOutsideTouchable(true);

        // show the popup window
        popupView.post(new Runnable() {
            public void run() {
                pw.showAtLocation(popupView,Gravity.CENTER, 0, 0);
            }
        });
        //pw.showAtLocation(popupView, Gravity.CENTER, 0, 0);

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