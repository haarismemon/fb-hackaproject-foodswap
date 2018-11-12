package com.hackaproject.foodswap.foodswap;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.hackaproject.foodswap.foodswap.requests.CheckEventRequest;
import com.hackaproject.foodswap.foodswap.requests.GiphyRequest;
import com.hackaproject.foodswap.foodswap.requests.RematchRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Random;

public class MatchedEventActivity extends AppCompatActivity {

    private String eventId;
    private ImageView celeberateImage;
    private TextView userName;
    private TextView userNationality;
    private TextView userDietary;
    private TextView userFoodToCook;
    private RequestQueue queue;

    public static final String REMATCH = "REMATCH";

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

                    Log.i("FoodSwap", jsonResponse.toString());
                    if (responseStatus == 1) {
                        JSONObject jsonObject = jsonResponse.getJSONObject("partner_info");

                        String fname = jsonObject.getString("fname");
                        String dietary = jsonObject.getString("dietary");

                        if(dietary.equals("nil")) {
                            dietary = "None";
                        }

                        userName.setText("You've been paired with " + fname + " " + jsonObject.getString("lname"));
                        userNationality.setText("Their ethnicity is: " + jsonObject.getString("nationality"));
                        userDietary.setText("Their dietary requirements are: " + dietary);

                        String foodString = fname + " will be cooking <b>" + jsonObject.getString("food") + "</b> for you!";
                        userFoodToCook.setText(Html.fromHtml(foodString));

                        getSetRandomGifImage();

                    } else {
                        showErrorDialog("Checked Event Retrieval Failed. Response code: " + responseStatus);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("FoodSwap", "Checked Event. Error Message. Failed Response" + error);
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
                    Random random = new Random();
                    JSONObject gifImageJson = new JSONObject(response)
                            .getJSONArray("data").getJSONObject(random.nextInt(25))
                            .getJSONObject("images")
                            .getJSONObject("fixed_height");

//                    Log.i("FoodSwap", dataJson.toString());

                    String url = gifImageJson.getString("url").replace("\\","");

                    Log.i("FoodSwap", url);

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

    public void rematch(View view) {
        RematchRequest rematchRequest = new RematchRequest(eventId, new Response.Listener<String>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    int responseStatus = jsonResponse.getInt("status");

                    Log.i("FoodSwap", jsonResponse.toString());
                    if (responseStatus == 1) {
                        Intent intent = new Intent(MatchedEventActivity.this, HomeActivity.class);
                        intent.putExtra(REMATCH, eventId);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    } else {
                        showErrorDialog("Rematch Event Retrieval Failed. Response code: " + responseStatus);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("FoodSwap", "Rematch Event. Error Message. Failed Response" + error);
                showErrorDialog("Rematch Event Retrieval Failed.");
            }
        });

        queue.add(rematchRequest);
    }
}
