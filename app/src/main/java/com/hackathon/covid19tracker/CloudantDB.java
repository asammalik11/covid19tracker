package com.hackathon.covid19tracker;

import android.content.Context;
import android.content.res.Resources;
import android.util.Base64;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class CloudantDB {

    final static String cloudantURL = "https://8cf59af7-656d-4673-84b6-fa4a5ec0052e-bluemix.cloudant.com";
    final static String dbName = "riot-main";
    final static String user = "8cf59af7-656d-4673-84b6-fa4a5ec0052e-bluemix";
    final static String pass = "043cf093460d2ca584de5c500efb8617bdf99bfa7ea1ea461985167bbda898ac";

    public static void getDB(Context c, String docId, final VolleyCallback callback) {
        final RequestQueue requestQueue = Volley.newRequestQueue(c);
        String url = cloudantURL +  "/" + dbName + "/" + docId;
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, (JSONObject) null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                callback.onSuccess(response.toString());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                callback.onError(error);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                String credentials = user + ":" + pass;
                String auth = "Basic " + Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put("Authorization", auth);
                return headers;
            }

        };
        requestQueue.getCache().clear();
        requestQueue.add(request);
    }

    public static void postToDB(Context c, String docId, String revNumber, HashMap<String, Object> postMap, final VolleyCallback callback) {
        final RequestQueue requestQueue = Volley.newRequestQueue(c);
        HashMap<String, Object> map = new HashMap<String, Object>();
        String url = cloudantURL +  "/" + dbName;
        //map.put("_id", doc);
        map.put("_id", docId);
        map.put("_rev", revNumber);
        map.putAll(postMap);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(postMap), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                callback.onSuccess(response.toString());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                callback.onError(error);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                String credentials = user + ":" + pass;
                String auth = "Basic " + Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put("Authorization", auth);
                return headers;
            }

        };
        requestQueue.getCache().clear();
        requestQueue.add(request);
    }

    public static void getLatestRevisionNumber(Context c, String docId, final RevisionNumberListener revisionNumberListener) {
        getDB(c, docId, new VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    String rev = jsonObject.getString("_rev");
                    revisionNumberListener.onRevisionNumberReceived(rev);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Throwable throwable) {
                Log.d("REV", "" + throwable);
            }
        });
    }

}