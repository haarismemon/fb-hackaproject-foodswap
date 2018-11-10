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
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.hackaproject.foodswap.foodswap.datamodels.User;
import com.hackaproject.foodswap.foodswap.requests.RegisterRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

public class RegisterActivity extends AppCompatActivity {

    private EditText input_first_name;
    private EditText input_last_name;
    private EditText input_email;
    private EditText input_password_1;
    private EditText input_password_2;
    private Spinner input_gender;
    private EditText input_nationality;
    private EditText input_dietary;
    private TextView input_dob;

    private SharedPreferences sharedPreferences;
    private DatePickerDialog.OnDateSetListener mDataSetListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        sharedPreferences = getApplicationContext().getSharedPreferences("DATA", Context.MODE_PRIVATE);

        input_first_name = (EditText) findViewById(R.id.input_first_name);
        input_last_name = (EditText) findViewById(R.id.input_last_name);
        input_email = (EditText) findViewById(R.id.input_email);
        input_password_1 = (EditText) findViewById(R.id.input_password_1);
        input_password_2 = (EditText) findViewById(R.id.input_password_2);
        input_gender = (Spinner) findViewById(R.id.input_gender);
        input_nationality = (EditText) findViewById(R.id.input_nationality);
        input_dietary = (EditText) findViewById(R.id.input_dietary);
        input_dob = (TextView) findViewById(R.id.input_dob);

        String[] genders = new String[]{"Male","Female", "Other", "Prefer not to say"};

        ArrayAdapter<String> gameKindArray= new ArrayAdapter<String>(RegisterActivity.this,android.R.layout.simple_spinner_item, genders);
        gameKindArray.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        input_gender.setAdapter(gameKindArray);

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
                input_dob.setText(date);
            }
        };
    }

    public void submit(View view) {
        Toast.makeText(this, "Submit button clicked", Toast.LENGTH_SHORT).show();

        String userPassword = "";

        String password1 = input_password_1.getText().toString();
        String password2 = input_password_2.getText().toString();

        User user = new User(input_first_name.getText().toString(),
                input_last_name.getText().toString(),
                input_gender.getSelectedItem().toString(),
                input_email.getText().toString(),
                input_password_1.getText().toString(),
                input_nationality.getText().toString(),
                input_dietary.getText().toString(),
                input_dob.getText().toString());

        RegisterRequest registerRequest = new RegisterRequest(user, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    Toast.makeText(RegisterActivity.this, "Response Received", Toast.LENGTH_SHORT).show();
                    JSONObject jsonResponse = new JSONObject(response);
                    int responseStatus = jsonResponse.getInt("status");

                    if (responseStatus == 1) {
                        JSONObject profile = jsonResponse.getJSONObject("profile");
                        String email = profile.getString("email");
                        String userId = jsonResponse.getString("uid");

                        sharedPreferences.edit().putString(HomeActivity.LOGGED_IN_UID, userId).apply();

                        Intent intent = new Intent(RegisterActivity.this, HomeActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    } else {
                        Log.i("FoodSwap", jsonResponse.toString());
                        AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                        builder.setMessage("Register Failed")
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
                Log.e("FoodSwap", "Register. Error Message. Failed Response" + error);
            }
        });

        RequestQueue queue = Volley.newRequestQueue(RegisterActivity.this);
        queue.start();

        if(password1.equals(password2)) {
            queue.add(registerRequest);
        } else {
            Toast.makeText(this, "The passwords do not match. Please re-enter.", Toast.LENGTH_SHORT).show();
        }
    }

    public void selectDate(View view) {
        //if date button was found
        int day;
        int month;
        int year;

        /*  if the date button has tag of DATE_PICKED then set the date on dialog to date picked earlier,
            otherwise display todays date on the dialog  */
        if (input_dob.getText().toString().equals("") || !input_dob.getText().toString().toLowerCase().contains("date")) {
            String dates[] = input_dob.getText().toString().split("-");

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
        DatePickerDialog dialog = new DatePickerDialog(RegisterActivity.this,
                android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                mDataSetListener, year, month, day);

        dialog.setButton(DialogInterface.BUTTON_NEUTRAL, "Clear Date", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                input_dob.setText("Select a date");
            }
        });

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }
}
