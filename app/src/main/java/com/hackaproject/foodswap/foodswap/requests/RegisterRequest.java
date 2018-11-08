package com.hackaproject.foodswap.foodswap.requests;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.hackaproject.foodswap.foodswap.datamodels.User;

import java.util.HashMap;
import java.util.Map;

public class RegisterRequest extends StringRequest {

    private static final String REGISTER_REQUEST_URL = "https://foodswapapp.herokuapp.com/users/signup";
    private Map<String, String> params;

    public RegisterRequest(User user, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(Method.POST, REGISTER_REQUEST_URL, listener, errorListener);

        params = new HashMap<>();
        params.put("fname", user.getFirst_name());
        params.put("lname", user.getLast_name());
        params.put("email", user.getEmail());
        params.put("pass", user.getPassword());
        params.put("gender", user.getGender());
        params.put("nationality", user.getNationality());
        params.put("dietary", user.getDietary());
        params.put("dob", user.getDob());
    }

    @Override
    public Map<String, String> getParams() {
        return params;
    }
}
