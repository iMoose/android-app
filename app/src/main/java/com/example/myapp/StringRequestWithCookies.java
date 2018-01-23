package com.example.myapp;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class StringRequestWithCookies extends StringRequest {
    private String[] header = new String[2];

    public StringRequestWithCookies(String url, String[] header, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(Request.Method.GET, url, listener, errorListener);
        this.header = header;
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        HashMap<String, String> headers = new HashMap<>();
        headers.put(header[0], header[1]);

        return headers;
    }

    @Override
    public byte[] getBody() throws AuthFailureError {
        return super.getBody();
    }
}