package com.hackaproject.foodswap.foodswap;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.hackaproject.foodswap.foodswap.requests.CheckEventRequest;
import com.hackaproject.foodswap.foodswap.requests.GiphyRequest;

import org.json.JSONException;
import org.json.JSONObject;

public class MatchedEventActivity extends AppCompatActivity {

    private String eventId;
    private ImageView celeberateImage;
    private TextView userName;
//    private TextView userLast;
//    private TextView userGender;
    private TextView userNationality;
    private TextView userDietary;
//    private TextView userDOB;
    private TextView userFoodToCook;
    private RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_matched_event);

        queue = Volley.newRequestQueue(MatchedEventActivity.this);
        queue.start();

        celeberateImage = findViewById(R.id.celebrate_gif);
        userName = findViewById(R.id.user_name);
//        userLast = findViewById(R.id.user_last);
//        userGender = findViewById(R.id.user_gender);
        userNationality = findViewById(R.id.user_nationality);
        userDietary = findViewById(R.id.user_dietary);
//        userDOB = findViewById(R.id.user_dob);
        userFoodToCook = findViewById(R.id.user_food_to_cook);

        Intent intent = getIntent();
        eventId = intent.getStringExtra(RecyclerAdapter.EVENT_ID);

        getSetRandomGifImage();
        getPairedEvent(eventId);
    }

    private void getPairedEvent(String eventId) {
        CheckEventRequest checkEventRequest = new CheckEventRequest(eventId, new Response.Listener<String>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    int responseStatus = jsonResponse.getInt("status");

                    if (responseStatus == 1) {

                        JSONObject jsonObject = jsonResponse.getJSONObject("partner_info");

                        String fname = jsonObject.getString("fname");

                        userName.setText("You've been paired with " + fname + " " + jsonObject.getString("lname"));
                        userNationality.setText("Their ethnicity is: " + jsonObject.getString("nationality"));
                        userDietary.setText("Their dietary requirements are: " + jsonObject.getString("dietary"));
                        userFoodToCook.setText(fname + " will be cooking " + jsonObject.getString("food") + " for you!");

                        getSetRandomGifImage();

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
                Log.e("FoodSwap", "Matched Event. Error Message. Failed Response" + error);
                showErrorDialog("Matched Event Retrieval Failed.");
            }
        });

        queue.add(checkEventRequest);
    }

    private void getSetRandomGifImage() {
        GiphyRequest giphyRequest = new GiphyRequest(new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject gifImageJson = new JSONObject(response)
                            .getJSONObject("data")
                            .getJSONObject("images")
                            .getJSONObject("fixed_height");

//                    Log.i("FoodSwap", dataJson.toString());

                    String url = gifImageJson.getString("url").replace("\\","");

                    Log.i("FoodSwap", url);

//                    Picasso.get().load(url).into(celeberateImage);
                    Glide.with(getApplicationContext()).load(url).into(celeberateImage);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("FoodSwap", "Giphy request. Error Message. Failed Response" + error);
            }
        });

        queue.add(giphyRequest);

    }

    private void showErrorDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MatchedEventActivity.this);
        builder.setMessage(message)
                .setNegativeButton("Retry", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        getPairedEvent(eventId);
                    }
                })
                .create()
                .show();
    }

}
