package com.hackaproject.foodswap.foodswap.requests;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.hackaproject.foodswap.foodswap.datamodels.NewEvent;

import java.util.HashMap;
import java.util.Map;

public class BookingRequest extends StringRequest {

    private static final String REGISTER_REQUEST_URL = "https://foodswapapp.herokuapp.com/users/newevent";
    private Map<String, String> params;

    public BookingRequest(NewEvent event, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(Method.POST, REGISTER_REQUEST_URL, listener, errorListener);

        params = new HashMap<>();
        params.put("uid", event.getUid());
        params.put("food", event.getFood());
        params.put("date", event.getDate());
    }

    @Override
    public Map<String, String> getParams() {
        return params;
    }
}
