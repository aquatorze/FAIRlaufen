package com.tudresden.fairlaufen;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class DescriptionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_description);

        //Intent von der MapActivity, es wird die ID des angeklickten Ortes übergeben
        Intent intent = getIntent();
        TextView textView = findViewById(R.id.text_description);
        int id = intent.getIntExtra("id",1);
        textView.setText("" + id);

    }
}

//über select, cursor (Seite 3) in java activity
// im layout verschiedene text views festlegen als Platzhalter