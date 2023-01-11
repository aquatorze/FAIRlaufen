package com.tudresden.fairlaufen;

import androidx.appcompat.app.AppCompatActivity;
import com.tudresden.fairlaufen.DatabaseHelper;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.Bundle;
import android.util.Base64;
import android.widget.ImageView;
import android.widget.TextView;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.sql.Blob;

public class DescriptionActivity extends AppCompatActivity {

    DatabaseHelper dbHelper;
    SQLiteDatabase database;
    Cursor dbCursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_description);

        //Intent von der MapActivity, es wird die ID des angeklickten Ortes übergeben
        Intent intent = getIntent();
        int id = intent.getIntExtra("id",1);


        dbHelper = new DatabaseHelper(this);
        database = dbHelper.getReadableDatabase();// initialize the database object
        dbCursor = database.rawQuery("SELECT * FROM fairPlaces WHERE place_ID = ?",
                new String[] {String.valueOf(id)});


        //define different Text Views from xml file
        TextView NamenTextView = findViewById(R.id.Namen);
        TextView KategorieTextView = findViewById(R.id.Kategorie);
        TextView BeschreibungTextView = findViewById(R.id.Beschreibung);
        TextView AdresseTextView = findViewById(R.id.Adresse);
        TextView WebdresseTextView = findViewById(R.id.Webadresse);
        //ImageView BildImageView = findViewById(R.id.Bild);

        //get string of the columns
        if (dbCursor.moveToFirst()) { // check if the cursor is empty
            String name = dbCursor.getString(dbCursor.getColumnIndex("name"));
            String category = dbCursor.getString(dbCursor.getColumnIndex("category"));
            String description = dbCursor.getString(dbCursor.getColumnIndex("description"));
            String adress = dbCursor.getString(dbCursor.getColumnIndex("adress"));
            String url = dbCursor.getString(dbCursor.getColumnIndex("url"));


            //retrieve image data as a byte array
            //byte[] photos= dbCursor.getBlob(dbCursor.getColumnIndex("image"));
            //convert the byte array to bitmap object
            //Bitmap imagebitmap = BitmapFactory.decodeByteArray(photos, 0, photos.length);


            //set text to textviews
            NamenTextView.setText(name);
            KategorieTextView.setText(category);
            //BildImageView.setImageBitmap(imagebitmap);
            BeschreibungTextView.setText(description);
            AdresseTextView.setText(adress);
            WebdresseTextView.setText(url);

        }

        dbCursor.close(); // close the cursor when you are finished with it

    }
}

