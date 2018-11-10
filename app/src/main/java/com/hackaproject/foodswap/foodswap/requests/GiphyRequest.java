package com.hackaproject.foodswap.foodswap.requests;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.hackaproject.foodswap.foodswap.datamodels.NewEvent;

import java.util.HashMap;
import java.util.Map;

public class GiphyRequest extends StringRequest {

    private static final String GIPHY_RANDOM_URL = "http://api.giphy.com/v1/gifs/search?";
    private static final String API_KEY = "BTDuQbWeIejB4hyKIyHP7fwOVuqS7ULS";
    private static final String TAG = "yay";


    public GiphyRequest(Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(Method.GET, GIPHY_RANDOM_URL + "q=" + TAG + "&api_key=" + API_KEY, listener, errorListener);
    }

}
