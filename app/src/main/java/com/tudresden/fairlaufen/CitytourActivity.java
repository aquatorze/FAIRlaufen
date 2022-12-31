package com.tudresden.fairlaufen;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

public class CitytourActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_citytour);
    }

    public void onClickMap(View view){
        System.out.println(view.getId());
        Intent intent = new Intent(this, MapActivity.class);
        intent.putExtra("type",view.getId());
        startActivity(intent);
    }
}
