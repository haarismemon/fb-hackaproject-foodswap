package com.hackaproject.foodswap.foodswap.requests;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class CheckEventRequest extends StringRequest {

    private static final String REQUEST_URL = "https://foodswapapp.herokuapp.com/users/checkevent";
    private Map<String, String> params;

    public CheckEventRequest(String eventID, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(Method.POST, REQUEST_URL, listener, errorListener);

        params = new HashMap<>();
        params.put("id", eventID);
    }

    @Override
    public Map<String, String> getParams() {
        return params;
    }
}
