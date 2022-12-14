package com.tudresden.fairlaufen;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onClickStartCitytour(View view){
        Intent intent = new Intent(this, CitytourActivity.class);
        startActivity(intent);
    }

    public void onClickStartDiscover(View view){
        Intent intent = new Intent(this, DiscoverActivity.class);
        startActivity(intent);
    }

    public void onClickTour1(View view){
        Intent intent = new Intent(this, Tour1Activity.class);
        startActivity(intent);
    }

    public void onClickTour2(View view){
        Intent intent = new Intent(this, Tour2Activity.class);
        startActivity(intent);
    }

    public void onClickStartMap(View view){
        Intent intent = new Intent(this, MapActivity.class);

        startActivity(intent);
    }
}