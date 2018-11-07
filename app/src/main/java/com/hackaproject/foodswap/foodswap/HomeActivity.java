package com.hackaproject.foodswap.foodswap;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.hackaproject.foodswap.foodswap.datamodels.Event;
import com.hackaproject.foodswap.foodswap.requests.ListRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class HomeActivity extends AppCompatActivity {

    public static final String LOGGED_IN_UID = "LOGGED_IN_UID";
    private String loggedInUID;

    private RecyclerAdapter recyclerAdapter;
    private RecyclerView recyclerView;
    private List<Event> eventsList;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("DATA", Context.MODE_PRIVATE);
//        sharedPreferences.edit().putString(LOGGED_IN_UID, null).apply();
        loggedInUID = sharedPreferences.getString(LOGGED_IN_UID, null);

        recyclerView = findViewById(R.id.eventsListView);
        eventsList = new ArrayList<>();

        if(loggedInUID == null) {
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

            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setHasFixedSize(true);
            recyclerView.setFocusable(false);
            recyclerAdapter = new RecyclerAdapter(this, eventsList);
            recyclerView.setAdapter(recyclerAdapter);

            //make a list request on startup
            updateEventList();

            swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
            swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    updateEventList();
                    Toast.makeText(HomeActivity.this, "Events refreshed", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void updateEventList() {
        ListRequest listRequest = new ListRequest(loggedInUID, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    int responseStatus = jsonResponse.getInt("status");

                    if (responseStatus == 1) {

                        List<Event> events = new ArrayList<>();

                        JSONArray jsonArray = jsonResponse.getJSONArray("list");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);

                            Event event = new Event(jsonObject.getString("uid"),
                                    jsonObject.getString("food"),
                                    parseDate(jsonObject.getString("date")),
                                    jsonObject.getString("status"),
                                    jsonObject.getString("partnerid"));

                            events.add(event);
                        }

                        recyclerAdapter.eventsList = events;
                        recyclerAdapter.notifyDataSetChanged();

                        if (swipeRefreshLayout.isRefreshing()) {
                            swipeRefreshLayout.setRefreshing(false);
                        }
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

    private String parseDate(String dateUTC) {
        DateFormat utcFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        utcFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

        Date date = null;
        try {
            date = utcFormat.parse(dateUTC);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        DateFormat gmtFormat = new SimpleDateFormat("dd/MM/yyyy");
        gmtFormat.setTimeZone(TimeZone.getTimeZone("GMT"));

        return gmtFormat.format(date);
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
