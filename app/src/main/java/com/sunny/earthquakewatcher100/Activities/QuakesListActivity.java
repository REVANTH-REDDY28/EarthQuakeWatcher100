package com.sunny.earthquakewatcher100.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DownloadManager;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.sunny.earthquakewatcher100.R;
import com.sunny.earthquakewatcher100.model.EarthQuake;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class QuakesListActivity extends AppCompatActivity {

    private ArrayList<String> arrayList;
    ArrayAdapter arrayAdapter;
    ListView listView;
    RequestQueue requestQueue;
    String url = "https://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/all_week.geojson";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quakes_list);
        listView = findViewById(R.id.listview);
        arrayList = new ArrayList<>();
        requestQueue = Volley.newRequestQueue(this);


        getAllQuakes(url);
    }

    public void getAllQuakes(String url){
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray jsonArray = response.getJSONArray("features");
                    for(int i=0;i<30;i++){
                        EarthQuake earthQuake = new EarthQuake();

                        //get properties
                        JSONObject properties = jsonArray.getJSONObject(i).getJSONObject("properties");

                        earthQuake.setPlace(properties.getString("place"));

                        arrayList.add(earthQuake.getPlace());


                    }
                    arrayAdapter = new ArrayAdapter(QuakesListActivity.this, android.R.layout.simple_list_item_1,
                            android.R.id.text1,arrayList);
                    listView.setAdapter(arrayAdapter);
                    arrayAdapter.notifyDataSetChanged();


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        requestQueue.add(jsonObjectRequest);
    }
}