package com.hackaproject.foodswap.foodswap;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.hackaproject.foodswap.foodswap.DataModel.User;

import java.util.HashMap;
import java.util.Map;

public class ListRequest extends StringRequest {

    private static final String REGISTER_REQUEST_URL = "https://localhost:4000/users/lists";
    private Map<String, String> params;

    public ListRequest(Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(Method.GET, REGISTER_REQUEST_URL, listener, errorListener);
    }

    @Override
    public Map<String, String> getParams() {
        return params;
    }
}
