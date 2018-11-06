package com.hackaproject.foodswap.foodswap.requests;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class ListRequest extends StringRequest {

    private static final String REGISTER_REQUEST_URL = "https://foodswapapp.herokuapp.com/users/list";
    private Map<String, String> params;

    public ListRequest(String uid, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(Method.POST, REGISTER_REQUEST_URL, listener, errorListener);

        params = new HashMap<>();
        params.put("uid", uid);
    }

    @Override
    public Map<String, String> getParams() {
        return params;
    }
}
