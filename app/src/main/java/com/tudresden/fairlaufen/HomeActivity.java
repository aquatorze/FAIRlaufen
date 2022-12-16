package com.tudresden.fairlaufen;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import androidx.appcompat.app.AppCompatActivity;

public class HomeActivity extends AppCompatActivity {
    RelativeLayout rl_citytour;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
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
}