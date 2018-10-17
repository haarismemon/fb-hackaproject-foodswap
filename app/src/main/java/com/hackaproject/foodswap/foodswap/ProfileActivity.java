package com.hackaproject.foodswap.foodswap;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;

public class ProfileActivity extends AppCompatActivity {

    private EditText profile_email;
    private EditText profile_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        profile_email = (EditText) findViewById(R.id.profile_email);
        profile_name = (EditText) findViewById(R.id.profile_name);

        Intent intent = getIntent();
        String first_name = intent.getStringExtra("first_name");
        String last_name = intent.getStringExtra("last_name");
        String email = intent.getStringExtra("email");

        profile_email.setText(email);
        profile_name.setText(first_name + " " + last_name);
    }
}
