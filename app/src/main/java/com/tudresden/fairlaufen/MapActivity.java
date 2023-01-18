package com.tudresden.fairlaufen;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
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
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private int tour;
    List<Marker> markerList = new ArrayList<>();


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
            ioe.printStackTrace();
        }
        database = dbHelper.getDataBase();

        switch (intent.getIntExtra("type", 1)) {
            case R.id.tour1: {
                System.out.println("1. Button gedrueckt");
                tour = 1;
                dbCursor = database.rawQuery("SELECT * FROM fairPlaces WHERE city_tour_type LIKE 'Alternativ für AnfängerInnen'", null);
                break;
            }
            case R.id.tour2: {
                System.out.println("2. Button gedrueckt");
                tour = 2;
                dbCursor = database.rawQuery("SELECT * FROM fairPlaces WHERE city_tour_type LIKE 'FAIRkleidet'", null);
            }
        }

        if(intent.getBooleanArrayExtra("category") != null){
            String searchTerm = "";
            boolean[] array = intent.getBooleanArrayExtra("category");
            for(int i = 0; i < array.length; i++){
                if(array[i]){
                    if(!searchTerm.equals("")){
                        searchTerm += " OR ";
                    }
                    switch(i){
                        case 0:
                            searchTerm += "category LIKE 'Essen & Trinken'";
                            break;
                        case 1:
                            searchTerm += "category LIKE 'Lebensmittel'";
                            break;
                        case 2:
                            searchTerm += "category LIKE 'Kosmetik'";
                            break;
                        case 3:
                            searchTerm += "category LIKE 'Second Hand'";
                            break;
                        case 4:
                            searchTerm += "category LIKE 'Bekleidung'";
                            break;
                        case 5:
                            searchTerm += "category LIKE 'Sonstiges'";
                            break;
                    }
                }
            }
            if(searchTerm.equals("")){
                dbCursor = database.rawQuery("SELECT * FROM fairPLaces;", null);
            }
            else {
                dbCursor = database.rawQuery("SELECT * FROM fairPlaces WHERE " + searchTerm, null);
            }

        }


        int length = dbCursor.getCount();
        dbCursor.moveToFirst();

        String[] place_names = new String[length];

        int index_name = dbCursor.getColumnIndex("name");

        for (int i = 0; i < length; i++) {
            place_names[i] = dbCursor.getString(index_name);
            dbCursor.moveToNext();
            System.out.println(place_names[i]);
        }
        ListView listView = findViewById(R.id.list_map);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.list_item_map, place_names);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Marker marker = markerList.get(position);
                marker.showInfoWindow();
                sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                CameraPosition cam_pos = new CameraPosition.Builder()
                        .target(marker.getPosition())
                        .zoom(15)
                        .build();
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cam_pos));
            }
        });

    }

    /*public void onClickCollapse(View view) {
        Button button = findViewById(R.id.bottom_sheet_arrow);
        if (sheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
            sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            button.setText("V");
        } else {
            sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            button.setText("^");
        }
    }*/

    @SuppressLint("PotentialBehaviorOverride")
    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;
        mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style));
        //mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        //mMap.getUiSettings().setZoomControlsEnabled(true);

        String[] PERMISSIONS = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
        locationPermissionRequest.launch(PERMISSIONS);

        CameraPosition cam_pos = new CameraPosition.Builder()
                .target(new LatLng(51.06, 13.75))
                .zoom(14)
                .build();
        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cam_pos));
        mMap.setBuildingsEnabled(true);

        Context context = this;
        addRoute(tour, context, mMap);
        addMarkersFromDB(dbCursor);
        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(@NonNull Marker marker) {
                onMarkerTextClick(marker);

            }
        });
    }

    private void addMarkersFromDB(Cursor cursor) {
        int length = cursor.getCount();
        cursor.moveToFirst();

        int index_name = cursor.getColumnIndex("name");
        int index_lat = cursor.getColumnIndex("latitude");
        int index_lon = cursor.getColumnIndex("longitude");
        int index_cat = cursor.getColumnIndex("category");

        for (int i = 0; i < length; i++) {
            Double latitude = Double.parseDouble(cursor.getString(index_lat));
            Double longitude = Double.parseDouble(cursor.getString(index_lon));
            cursor.getString(index_name);
            int markerColor;
            switch(cursor.getString(index_cat)){
                case "Second Hand":
                    markerColor = 60;
                    break;
                case "Kosmetik":
                    markerColor = 270;
                    break;
                case "Bekleidung":
                    markerColor = 210;
                    break;
                case "Lebensmittel":
                    markerColor = 30;
                    break;
                case "Essen & Trinken":
                    markerColor = 120;
                    break;
                default:
                    markerColor = 180;
            }
            Marker marker = mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(latitude, longitude))
                    .title(cursor.getString(index_name))
                    .icon(BitmapDescriptorFactory.defaultMarker(markerColor)));
            markerList.add(marker);
            cursor.moveToNext();
        }
    }

    public void onMarkerTextClick(Marker marker) {
        System.out.println("Markertext gecklicked mit Titel " + marker.getTitle());
        Cursor mCursor = database.rawQuery("SELECT * FROM fairPlaces WHERE name LIKE '" + marker.getTitle() + "';", null);
        mCursor.moveToFirst();
        int index_id = mCursor.getColumnIndex("place_ID");
        int id = Integer.parseInt(mCursor.getString(index_id));

        Intent intent = new Intent(this, DescriptionActivity.class);
        intent.putExtra("id", id);
        startActivity(intent);
    }

    public void addRoute(int tour, Context context, GoogleMap gMap) {
        String jsonString = "";
        String routename = "";
        switch (tour) {
            case 1:
                routename = "Alternativ_route.json";
                break;
            case 2:
                routename = "FAIRkleidet_route.json";
                break;
        }
        try {
            InputStream is = context.getAssets().open(routename);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            jsonString = new String(buffer, "UTF-8");
            JSONObject geojsonData = new JSONObject(jsonString);

            JSONArray jsonArray = geojsonData.getJSONArray("features").getJSONObject(0)
                    .getJSONObject("geometry").getJSONArray("coordinates");
            List<LatLng> latLngList = new ArrayList<>();
            for (int i = 0; i < jsonArray.length(); i++) {
                double longitude = Double.parseDouble(jsonArray.getJSONArray(i).get(0).toString());
                double latitude = Double.parseDouble(jsonArray.getJSONArray(i).get(1).toString());
                latLngList.add(new LatLng(latitude, longitude));
            }
            Polyline polylineRoute = gMap.addPolyline(new PolylineOptions()
                    .addAll(latLngList)
                    .clickable(false)
                    .color(R.color.background_green));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
