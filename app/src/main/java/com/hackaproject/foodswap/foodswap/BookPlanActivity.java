package com.hackaproject.foodswap.foodswap;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.hackaproject.foodswap.foodswap.datamodels.NewEvent;
import com.hackaproject.foodswap.foodswap.requests.BookingRequest;

import org.json.JSONException;
import org.json.JSONObject;

public class BookPlanActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_plan);

        sharedPreferences = getApplicationContext().getSharedPreferences("DATA", Context.MODE_PRIVATE);
    }

    public void bookEvent(View view) {
        EditText foodText = findViewById(R.id.input_food);
        EditText dateText = findViewById(R.id.input_date);

        String loggedInUID = sharedPreferences.getString(HomeActivity.LOGGED_IN_UID, null);

        NewEvent newEvent = new NewEvent(loggedInUID, foodText.getText().toString(), dateText.getText().toString());

        if(loggedInUID != null) {
            BookingRequest bookingRequest = new BookingRequest(newEvent, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        int responseStatus = jsonResponse.getInt("status");

                        if (responseStatus == 1) {
                            Toast.makeText(BookPlanActivity.this, "Event created!", Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent(BookPlanActivity.this, HomeActivity.class);
                            startActivity(intent);
                        } else {
                            AlertDialog.Builder builder = new AlertDialog.Builder(BookPlanActivity.this);
                            builder.setMessage("Event Creation Failed")
                                    .setNegativeButton("Retry", null)
                                    .create()
                                    .show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d("FoodSwap", "Book Plan. Error Message. Failed Response" + error);
                }
            });

            RequestQueue queue = Volley.newRequestQueue(BookPlanActivity.this);
            queue.add(bookingRequest);
            queue.start();
        } else {
            Toast.makeText(this, "uid is null", Toast.LENGTH_SHORT).show();
        }
    }
}
