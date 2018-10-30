package com.hackaproject.foodswap.foodswap;

import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.hackaproject.foodswap.foodswap.DataModel.User;

import org.json.JSONException;
import org.json.JSONObject;

public class RegisterActivity extends AppCompatActivity {

    private EditText input_first_name;
    private EditText input_last_name;
    private EditText input_email;
    private EditText input_password_1;
    private EditText input_password_2;
    private RadioGroup input_gender_group;
    private EditText input_nationality;
    private EditText input_dietary;
    private EditText input_dob;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        input_first_name = (EditText) findViewById(R.id.input_first_name);
        input_last_name = (EditText) findViewById(R.id.input_last_name);
        input_email = (EditText) findViewById(R.id.input_email);
        input_password_1 = (EditText) findViewById(R.id.input_password_1);
        input_password_2 = (EditText) findViewById(R.id.input_password_2);
        input_gender_group = (RadioGroup) findViewById(R.id.input_gender);
        input_nationality = (EditText) findViewById(R.id.input_nationality);
        input_dietary = (EditText) findViewById(R.id.input_dietary);
        input_dob = (EditText) findViewById(R.id.input_dob);
    }

    public void submit(View view) {
        Toast.makeText(this, "Submit button clicked", Toast.LENGTH_SHORT).show();
        User user = new User();

        user.setFirst_name(input_first_name.getText().toString());
        user.setLast_name(input_last_name.getText().toString());
        user.setEmail(input_email.getText().toString());

        //todo hash the password
        String password1 = input_password_1.getText().toString();
        String password2 = input_password_2.getText().toString();
        if(password1.equals(password2)) {
            user.setPassword(input_password_1.getText().toString());
        } else {
            Toast.makeText(this, "The passwords do not match. Please re-enter.", Toast.LENGTH_SHORT).show();
        }

        RadioButton selectedGenderRadio = (RadioButton) findViewById(input_gender_group.getCheckedRadioButtonId());
        user.setGender(selectedGenderRadio.getText().toString());

        user.setNationality(input_nationality.getText().toString());
        user.setDietary(input_dietary.getText().toString());
        user.setDob(input_dob.getText().toString());

        RegisterRequest registerRequest = new RegisterRequest(user, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    Toast.makeText(RegisterActivity.this, "Response Received", Toast.LENGTH_SHORT).show();
                    JSONObject jsonResponse = new JSONObject(response);
                    boolean success = jsonResponse.getBoolean("success");

                    if (success) {
                        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                        RegisterActivity.this.startActivity(intent);
                    } else {
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
                Log.d("FoodSwap", "Error Message. Failed Response" + error);
            }
        });

        RequestQueue queue = Volley.newRequestQueue(RegisterActivity.this);
        queue.add(registerRequest);
        queue.start();
    }

}
