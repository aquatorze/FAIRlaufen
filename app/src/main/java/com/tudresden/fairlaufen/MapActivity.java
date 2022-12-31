package com.tudresden.fairlaufen;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.maps.android.data.geojson.GeoJsonLayer;
import com.google.maps.android.data.geojson.GeoJsonLineStringStyle;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.concurrent.TimeUnit;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    String mode = "foot-walking";
    private int tour;

    static DatabaseHelper dbHelper;
    SQLiteDatabase database;
    Cursor dbCursor;

    ActivityResultLauncher<String[]> locationPermissionRequest;

    private RelativeLayout mBottomSheetLayout;
    private BottomSheetBehavior sheetBehavior;

    @SuppressLint("MissingPermission")
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);

        locationPermissionRequest = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), result -> {
            Boolean fineLocationGranted = result.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false);
            Boolean coarseLocationGranted = result.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false);

            if ((fineLocationGranted != null && fineLocationGranted) || (coarseLocationGranted != null && coarseLocationGranted)) {
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                Toast.makeText(this, "Location cannot be obtained due to missing permission.", Toast.LENGTH_LONG).show();
            }
        });

        mBottomSheetLayout = findViewById(R.id.bottom_sheet_layout);
        sheetBehavior = BottomSheetBehavior.from(mBottomSheetLayout);

        Intent intent = getIntent();

        dbHelper = new DatabaseHelper(this);
        try {
            dbHelper.createDataBase();
        } catch (IOException ioe) {
        }
        database = dbHelper.getDataBase();

        if(intent.getIntExtra("type", 1) == 2131231228){
            System.out.println("1. Button gedrueckt");
            tour = 1;
            dbCursor = database.rawQuery("SELECT * FROM fairPlaces WHERE city_tour_type LIKE 'Alternativ für AnfängerInnen'", null);
        }
        else{
            System.out.println("2. Button gedrueckt");
            tour = 2;
            dbCursor = database.rawQuery("SELECT * FROM fairPlaces WHERE city_tour_type LIKE 'FAIRkleidet'", null);
        }
        int length = dbCursor.getCount();
        dbCursor.moveToFirst();

        String[] place_names = new String[length];

        int index_name = dbCursor.getColumnIndex("name");

        for(int i = 0; i < length; i++){
            place_names[i] = dbCursor.getString(index_name);
            dbCursor.moveToNext();
            System.out.println(place_names[i]);
        }
        ListView listView = findViewById(R.id.list_map);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.list_item_map, place_names);
        listView.setAdapter(adapter);


    }

    public void onClickCollapse(View view){
        Button button = findViewById(R.id.bottom_sheet_arrow);
        if(sheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED){
            sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            button.setText("V");
        }
        else {
            sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            button.setText("^");
        }
    }

    @SuppressLint("PotentialBehaviorOverride")
    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        //mMap.getUiSettings().setZoomControlsEnabled(true);

        String[] PERMISSIONS = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
        locationPermissionRequest.launch(PERMISSIONS);

        CameraPosition cam_pos = new CameraPosition.Builder()
                .target(new LatLng(51.05, 13.74))
                .zoom(13)
                .build();
        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cam_pos));
        mMap.setBuildingsEnabled(true);

        addMarkersFromDB(dbCursor);
        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(@NonNull Marker marker) {
                onMarkerTextClick(marker);
            }
        });
        //mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 100));
        /*MarkerOptions myMarker = new MarkerOptions()
                .position(new LatLng(51.05, 13.74))
                .anchor(0.5f, 1) //An welchem Punkt des Markers sollen die Koordinaten liegen
                .title("HÜL/S590")
                .snippet("Computer Lab");
        mMap.addMarker(myMarker);
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng newPos) {
                MarkerOptions clickMarker = new MarkerOptions().position(newPos);
                mMap.addMarker(clickMarker);
                String url = "https://api.openrouteservice.org/v2/directions/" +
                        mode +
                        "?api_key=5b3ce3597851110001cf62487ffe36ced36242aeb94b33ecb7c2fff3" +
                        "&start=" + myMarker.getPosition().longitude + "," + myMarker.getPosition().latitude +
                        "&end=" + clickMarker.getPosition().longitude + "," + clickMarker.getPosition().latitude;
                new DownloadGeoJsonFile().execute(url);
            }
        });*/


    }

    private void addMarkersFromDB(Cursor cursor){
        int length = cursor.getCount();
        cursor.moveToFirst();

        int index_name = cursor.getColumnIndex("name");
        int index_lat = cursor.getColumnIndex("latitude");
        int index_lon = cursor.getColumnIndex("longitude");

        for(int i = 0; i < length; i++){
            Double latitude = Double.parseDouble(cursor.getString(index_lat));
            Double longitude = Double.parseDouble(cursor.getString(index_lon));
             cursor.getString(index_name);
            mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(latitude, longitude))
                    .title(cursor.getString(index_name))
                    .icon(BitmapDescriptorFactory.defaultMarker(120)));
            cursor.moveToNext();
        }
    }
    public void onMarkerTextClick(Marker marker){
        System.out.println("Markertext gecklicked mit Titel " + marker.getTitle());
        Cursor mCursor = database.rawQuery("SELECT * FROM fairPlaces WHERE name LIKE '" + marker.getTitle() + "';", null);
        mCursor.moveToFirst();
        int index_id = mCursor.getColumnIndex("place_ID");
        int id = Integer.parseInt(mCursor.getString(index_id));

        Intent intent = new Intent(this, DescriptionActivity.class);
        intent.putExtra("id",id);
        startActivity(intent);
    }

    private class DownloadGeoJsonFile extends AsyncTask<String, Void, GeoJsonLayer> {

        @Override
        protected GeoJsonLayer doInBackground(String... params) {
            try {
                // Open a stream from the URL
                InputStream stream = new URL(params[0]).openStream();

                String line;
                StringBuilder result = new StringBuilder();
                BufferedReader reader = new BufferedReader(new InputStreamReader(stream));

                while ((line = reader.readLine()) != null) {
                    // Read and save each line of the stream
                    result.append(line);
                }

                // Close the stream
                reader.close();
                stream.close();

                return new GeoJsonLayer(mMap, new JSONObject(result.toString()));
            } catch (IOException e) {
                Log.e("mLogTag", "GeoJSON file could not be read");
            } catch (JSONException e) {
                Log.e("mLogTag", "GeoJSON file could not be converted to a JSONObject");
            }
            return null;
        }

        @Override
        protected void onPostExecute(GeoJsonLayer layer) {
            if (layer != null) {
                GeoJsonLineStringStyle lineStringStyle = layer.getDefaultLineStringStyle();
                lineStringStyle.setColor(Color.RED);
                lineStringStyle.setWidth(10f);

                layer.addLayerToMap();
            }
        }
    }

}
