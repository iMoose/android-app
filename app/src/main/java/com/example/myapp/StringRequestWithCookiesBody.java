package com.example.myapp;

import android.util.Xml;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class StringRequestWithCookiesBody extends StringRequest {
    static byte[] temp = "Random text".getBytes();
    private String[] header = new String[2];
    private String body;

    public StringRequestWithCookiesBody(String url, String[] header, String body, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(Request.Method.POST, url, listener, errorListener);
        this.header = header;
        this.body = body;
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        HashMap<String, String> headers = new HashMap<>();
        headers.put(header[0], header[1]);

        return headers;
    }

    @Override
    public byte[] getBody() throws AuthFailureError {
        return body.getBytes();
    }
}