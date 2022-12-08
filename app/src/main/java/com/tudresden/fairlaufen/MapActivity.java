package com.tudresden.fairlaufen;

import android.Manifest;
import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
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
import com.google.android.gms.maps.model.MapStyleOptions;
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

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    String mode = "foot-walking";
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

    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;

        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.getUiSettings().setZoomControlsEnabled(true);

        mMap.addMarker(new MarkerOptions()
                .position(new LatLng(0, 0))
                .alpha(0.5f)
                .icon(BitmapDescriptorFactory.defaultMarker(180)));

        String[] PERMISSIONS = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
        locationPermissionRequest.launch(PERMISSIONS);

        CameraPosition cam_pos = new CameraPosition.Builder()
                .target(new LatLng(51.035353, 13.728437))
                .zoom(15)
                .tilt(60)
                .bearing(25)
                .build();
        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cam_pos));
        mMap.setBuildingsEnabled(true);
        MarkerOptions myMarker = new MarkerOptions()
                .position(new LatLng(51.02855, 13.723903))
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
        });
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
