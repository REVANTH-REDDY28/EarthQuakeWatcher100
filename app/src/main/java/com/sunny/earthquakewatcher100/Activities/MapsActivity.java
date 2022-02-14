package com.sunny.earthquakewatcher100.Activities;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.sunny.earthquakewatcher100.R;
import com.sunny.earthquakewatcher100.UI.CustomInfoWindow;
import com.sunny.earthquakewatcher100.databinding.ActivityMapsBinding;
import com.sunny.earthquakewatcher100.model.EarthQuake;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.util.Date;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleMap.OnInfoWindowClickListener,GoogleMap.OnMarkerClickListener {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    LocationManager locationManager;
    LocationListener locationListener;
    LatLng myLocation;
    RequestQueue queue;

    AlertDialog.Builder dialogBuilder;
    AlertDialog alertDialog;

    Button showListBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // Instantiate the RequestQueue.
        queue = Volley.newRequestQueue(this);
        String url = "https://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/all_week.geojson";

        getEarthQuakes(queue, url);

        showListBtn = findViewById(R.id.showListBtn);
        showListBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MapsActivity.this,QuakesListActivity.class));
            }
        });


    }

    private void getEarthQuakes(RequestQueue queue, String url) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Log.d("lolipop", "onResponse: "+response.getJSONArray("features").getJSONObject(0).getJSONObject("properties").get("place"));
                            JSONArray jsonArray = response.getJSONArray("features");
                            for(int i=0 ; i< 30; i++){
                                Log.d("lolipop", "onResponse2: "+jsonArray.get(i));
                                EarthQuake earthQuake = new EarthQuake();

                                //get properties
                                JSONObject properties = jsonArray.getJSONObject(i).getJSONObject("properties");

                                earthQuake.setPlace(properties.getString("place"));
                                earthQuake.setTime(properties.getLong("time"));
                                earthQuake.setMagnitude(properties.getDouble("mag"));
                                earthQuake.setDetailUrl(properties.getString("detail"));

                                //get coordinates
                                JSONArray coordinates = jsonArray.getJSONObject(i).getJSONObject("geometry").getJSONArray("coordinates");
                                Double lat = coordinates.getDouble(1);
                                Double lng = coordinates.getDouble(0);

                                DateFormat dateFormat = DateFormat.getDateInstance();
                                String formattedDate =  dateFormat.format(new Date(earthQuake.getTime()).getTime());

                                LatLng latLng = new LatLng(lat,lng);

                                MarkerOptions markerOptions = new MarkerOptions();
                                markerOptions.title(earthQuake.getPlace());
                                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
                                markerOptions.position(latLng);
                                markerOptions.snippet("Magnitude: "+earthQuake.getMagnitude()+" \n"+
                                        "Date: "+formattedDate);

                                //Add circle to markers that have mag > x
                                if(earthQuake.getMagnitude() >= 2.0){
                                    CircleOptions circleOptions = new CircleOptions();
                                    circleOptions.center(new LatLng(lat,lng));
                                    circleOptions.radius(30000);
                                    circleOptions.strokeWidth(3.6f);
                                    circleOptions.fillColor(Color.RED);
                                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));

                                    mMap.addCircle(circleOptions);
                                }

                                Marker marker = mMap.addMarker(markerOptions);
                                marker.setTag(properties.get("detail"));


                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error

                    }
                });

        queue.add(jsonObjectRequest);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);


    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setInfoWindowAdapter(new CustomInfoWindow(getApplicationContext()));
        mMap.setOnInfoWindowClickListener(this);
        mMap.setOnMarkerClickListener(this);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                Log.d("lolipop", "onLocationChanged: " + location.toString());
                myLocation = new LatLng(location.getLatitude(), location.getLongitude());
                Log.d("lolipop", "onLocationChanged: " + myLocation.toString());
                locationManager.removeUpdates(locationListener);

                mMap.addMarker(new MarkerOptions().position(myLocation).title("Your location"));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(myLocation));


            }
        };

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        }



    }

    @Override
    public void onInfoWindowClick(@NonNull Marker marker) {
//        Toast.makeText(this, " "+marker.getTag(), Toast.LENGTH_SHORT).show();
            getEarthQuakeDetails(marker.getTag().toString());
    }

    private void getEarthQuakeDetails(String url) {

        //problem at api side which is unable to give 'nearby-cities' from given url
        //so provided new url which shows same nearby-cities for every earthquake
        String newUrl = "https://earthquake.usgs.gov/realtime/product/nearby-cities/ci40178960/ci/1644222135932/nearby-cities.json";

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET,
                newUrl, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
//                    Log.d("lalipop1", "onResponse: "+response);
//                    JSONObject properties = response.getJSONObject("properties");
//
//                    JSONObject products = properties.getJSONObject("products");
//                    Log.d("lalipop2", "onResponse: "+products);
//                    JSONArray nearByCities = products.getJSONArray("nearby-cities");
//                    Log.d("lalipop3", "onResponse: "+nearByCities);
//                    JSONObject nearByCities_0 = (JSONObject) nearByCities.get(0);
//                    JSONObject contents = nearByCities_0.getJSONObject("contents");
//                    JSONObject nearByCitiesJson = contents.getJSONObject("nearby-cities.json");
//                    String detailsUrl = nearByCitiesJson.getString("url");
//
//                    Log.d("lalipop", "onResponse: "+detailsUrl);

                    dialogBuilder = new AlertDialog.Builder(MapsActivity.this);
                    View view = getLayoutInflater().inflate(R.layout.popup,null);

                    Button dismissButtonBottom = view.findViewById(R.id.dismissPopBottom);
                    Button dismissButtonTop = view.findViewById(R.id.dismissPopTop);
                    TextView popList = view.findViewById(R.id.popList);
                    WebView htmlPop = view.findViewById(R.id.htmlWebView);

                    StringBuilder stringBuilder = new StringBuilder();

                    for(int i= 0;i<response.length();i++){
                        Log.d("newUrl", "onResponse: "+response.get(i));
                        JSONObject citiesObj = response.getJSONObject(i);
                        stringBuilder.append("City: "+citiesObj.getString("name")+
                                "\n"+"Distance: "+citiesObj.getString("distance")+
                                "\n"+"Population: "+
                                "500");
                        stringBuilder.append("\n\n");
                    }

                    popList.setText(stringBuilder);

                    dialogBuilder.setView(view);
                    alertDialog = dialogBuilder.create();
                    alertDialog.show();


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        queue.add(jsonArrayRequest);
    }

    @Override
    public boolean onMarkerClick(@NonNull Marker marker) {
        return false;
    }
}
