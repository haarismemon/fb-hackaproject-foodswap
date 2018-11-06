package com.hackaproject.foodswap.foodswap.requests;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.hackaproject.foodswap.foodswap.datamodels.NewEvent;

import java.util.HashMap;
import java.util.Map;

public class NewEventRequest extends StringRequest {

    private static final String REGISTER_REQUEST_URL = "https://foodswapapp.herokuapp.com/users/list";
    private Map<String, String> params;

    public NewEventRequest(NewEvent newEvent, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(Method.POST, REGISTER_REQUEST_URL, listener, errorListener);

        params = new HashMap<>();
        params.put("uid", newEvent.getUid());
        params.put("food", newEvent.getFood());
        params.put("date", newEvent.getDate());
    }

    @Override
    public Map<String, String> getParams() {
        return params;
    }
}
