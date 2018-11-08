package com.hackaproject.foodswap.foodswap;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.hackaproject.foodswap.foodswap.datamodels.NewEvent;
import com.hackaproject.foodswap.foodswap.requests.BookingRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

public class BookPlanActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;
    private EditText foodText;
    private TextView dateText;
    private DatePickerDialog.OnDateSetListener mDataSetListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_plan);

        sharedPreferences = getApplicationContext().getSharedPreferences("DATA", Context.MODE_PRIVATE);

        foodText = findViewById(R.id.input_food);
        dateText = findViewById(R.id.input_date);

        //used by date picker dialog to store the date picked in right format
        mDataSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                //add 1 to the month, as month start with 0
                month = month + 1;

                //if month is one digit, add 0 to the front
                String monthOfYear = Integer.toString(month);
                monthOfYear = (monthOfYear.length() == 1) ? "0" + monthOfYear : monthOfYear;

                //if day is one digit, add 0 to the front
                String dayOfMonth = Integer.toString(day);
                dayOfMonth = (dayOfMonth.length() == 1) ? "0" + dayOfMonth : dayOfMonth;

                String date = year + "-" + dayOfMonth + "-" + dayOfMonth;
                dateText.setText(date);
            }
        };
    }

    public void bookEvent(View view) {
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

    public void selectDateToMeet(View view) {
        //if date button was found
        int day;
        int month;
        int year;

        /*  if the date button has tag of DATE_PICKED then set the date on dialog to date picked earlier,
            otherwise display todays date on the dialog  */
        if (dateText.getText().toString().equals("") || !dateText.getText().toString().toLowerCase().contains("date")) {
            String dates[] = dateText.getText().toString().split("-");

            day = Integer.parseInt(dates[2]);
            //minus 1 to get the month index
            month = Integer.parseInt(dates[1]) - 1;
            year = Integer.parseInt(dates[0]);

        } else {
            //get todays date
            Calendar cal = Calendar.getInstance();

            day = cal.get(Calendar.DAY_OF_MONTH);
            month = cal.get(Calendar.MONTH);
            year = cal.get(Calendar.YEAR);
        }

        //set the dialog with the date and show it
        DatePickerDialog dialog = new DatePickerDialog(BookPlanActivity.this,
                android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                mDataSetListener, year, month, day);

        dialog.setButton(DialogInterface.BUTTON_NEUTRAL, "Clear Date", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                dateText.setText("Select a date");
            }
        });

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }
}
