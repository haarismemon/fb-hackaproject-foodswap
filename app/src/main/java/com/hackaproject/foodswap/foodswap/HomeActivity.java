package com.hackaproject.foodswap.foodswap;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.hackaproject.foodswap.foodswap.datamodels.Event;
import com.hackaproject.foodswap.foodswap.requests.ListRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    public static final String LOGGED_IN_EMAIL = "LOGGED_IN_EMAIL";
    public static final String LOGGED_IN_UID = "LOGGED_IN_UID";
    private String loggedInEmail;
    private String loggedInUID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("DATA", Context.MODE_PRIVATE);
//        sharedPreferences.edit().putString(LOGGED_IN_EMAIL, null).apply();
//        sharedPreferences.edit().putString(LOGGED_IN_UID, null).apply();
        loggedInEmail = sharedPreferences.getString(LOGGED_IN_EMAIL, null);
        loggedInUID = sharedPreferences.getString(LOGGED_IN_UID, null);

        if(loggedInEmail == null || loggedInUID == null) {
            Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        } else {

            FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(HomeActivity.this, BookPlanActivity.class);
                    startActivity(intent);
                }
            });

            //make a list request on startup
            updateEventList();
        }
    }

    private void updateEventList() {
        ListRequest listRequest = new ListRequest(loggedInUID, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    Toast.makeText(HomeActivity.this, "Response Received", Toast.LENGTH_SHORT).show();
                    JSONObject jsonResponse = new JSONObject(response);
                    int responseStatus = jsonResponse.getInt("status");

                    if (responseStatus == 1) {
                        List<Event> events = new ArrayList<>();

                        JSONArray jsonArray = jsonResponse.getJSONArray("list");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);

                            Event event = new Event();
                            event.setUid(jsonObject.getString("uid"));
                            event.setFood(jsonObject.getString("food"));
                            event.setDate(jsonObject.getString("date"));
                            event.setStatus(jsonObject.getString("status"));
                            event.setPartnerId(jsonObject.getString("partnerid"));

                            events.add(event);
                        }

                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(HomeActivity.this,
                                android.R.layout.simple_list_item_1, android.R.id.text1, getEventStrings(events));

                        ListView listView = findViewById(R.id.eventsListView);
                        listView.setAdapter(adapter);
                    } else {
                        showErrorDialog("List Retrieval Failed. Response code: " + responseStatus);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("FoodSwap", "HomeActivity. Error Message. Failed Response" + error);
                showErrorDialog("List Retrieval Failed.");
            }
        });

        RequestQueue queue = Volley.newRequestQueue(HomeActivity.this);
        queue.add(listRequest);
        queue.start();
    }

    private void showErrorDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
        builder.setMessage(message)
                .setNegativeButton("Retry", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        updateEventList();
                    }
                })
                .create()
                .show();
    }

    public void bookPlan(View view) {
        Intent intent = new Intent(HomeActivity.this, BookPlanActivity.class);
        this.startActivity(intent);
    }

    public List<String> getEventStrings(List<Event> events) {
        List<String> strings = new ArrayList<>();

        for(Event event : events) {
            String title = event.getFood() + " - ";

            if(event.getStatusInteger() == 0) {
                title += "pending";
            } else if(event.getStatusInteger() == 1) {
                title += "confirmed";
            } else if(event.getStatusInteger() == 2) {
                title += "done";
            }

            strings.add(title);
        }

        return strings;
    }
}
