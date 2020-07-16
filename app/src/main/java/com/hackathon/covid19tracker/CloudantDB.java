package com.hackathon.covid19tracker;

import android.content.Context;
import android.content.res.Resources;
import android.nfc.Tag;
import android.os.Build;
import android.util.Base64;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.sromku.simple.storage.SimpleStorage;
import com.sromku.simple.storage.Storage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CloudantDB {

    final static String cloudantURL = "https://8cf59af7-656d-4673-84b6-fa4a5ec0052e-bluemix.cloudant.com";
    final static String dbName = "riot-main";
    final static String user = "8cf59af7-656d-4673-84b6-fa4a5ec0052e-bluemix";
    final static String pass = "043cf093460d2ca584de5c500efb8617bdf99bfa7ea1ea461985167bbda898ac";

    static Storage storage;

    public static void initDB(Context c, final String docId, final String bluetoothAddress) {
        storage = SimpleStorage.getInternalStorage(c);
        getRemoteDB(c, docId, new VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject jsonDB = new JSONObject(result);
                    // adding id and rev to db
                    jsonDB.put("_id", docId);
                    jsonDB.put("_rev", "");
                    // adding notif parameter
                    JSONObject notif = new JSONObject();
                    notif.put("notify", 0);
                    // adding device json array
                    JSONArray deviceJson = new JSONArray();
                    deviceJson.put(notif);
                    jsonDB.put(bluetoothAddress, deviceJson);
                    storage.deleteFile("files", "main.json");
                    storage.createFile("files", "main.json", jsonDB.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Throwable throwable) {
                Log.d("_rev", "" + throwable);
            }
        });
        postToRemoteDB(c, docId, new VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                Log.d("Success", result);
            }

            @Override
            public void onError(Throwable throwable) {
                Log.d("Success", "" + throwable);
            }
        });
    }

    public static void updateBluetoothList(Context c, final String docId, final String bluetoothAddress, final String bluetoothString) {
        storage = SimpleStorage.getInternalStorage(c);
        getRemoteDB(c, docId, new VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject jsonDB = new JSONObject(result);
                    // update device json array
                    JSONArray deviceJson = jsonDB.getJSONArray(bluetoothAddress);
                    deviceJson.put(bluetoothString);
                    jsonDB.put(bluetoothAddress, deviceJson);
                    storage.deleteFile("files", "main.json");
                    storage.createFile("files", "main.json", jsonDB.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Throwable throwable) {
                Log.d("_rev", "" + throwable);
            }
        });
        postToRemoteDB(c, docId, new VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                Log.d("Success", result);
            }

            @Override
            public void onError(Throwable throwable) {
                Log.d("Success", "" + throwable);
            }
        });
    }

    public static void notifyUsers(Context c, final String docId, final String bluetoothAddress, final String user) {
        storage = SimpleStorage.getInternalStorage(c);
        getRemoteDB(c, docId, new VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject jsonDB = new JSONObject(result);
                    if (jsonDB.has(user)) {
                        JSONArray deviceJson = jsonDB.getJSONArray(user);
                        JSONObject notif = new JSONObject();
                        notif.put("notify", 1);
                        deviceJson.put(notif);
                        jsonDB.put(user, deviceJson);
                        storage.createFile("files", "main.json", jsonDB.toString());
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Throwable throwable) {
                Log.d("_rev", "" + throwable);
            }
        });
        postToRemoteDB(c, docId, new VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                Log.d("Success", result);
            }

            @Override
            public void onError(Throwable throwable) {
                Log.d("Success", "" + throwable);
            }
        });
    }

    public static boolean bluetoothStringExists(JSONArray deviceJson, String bluetoothString) {
        boolean isDuplicate = false;
        for (int i = 0 ; i < deviceJson.length(); i++) {
            try {
                String entry = deviceJson.getJSONObject(i).toString();
                if (bluetoothString.equals(entry)) {
                    isDuplicate = true;
                    break;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return isDuplicate;
    }

    // Get the latest remote db
    public static void getRemoteDB(Context c, String docId, final VolleyCallback callback) {
        final RequestQueue requestQueue = Volley.newRequestQueue(c);
        String url = cloudantURL + "/" + dbName + "/" + docId;
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

    // Post latest local db to remote
    public static void postToRemoteDB(Context c, final String docId, final VolleyCallback callback) {
        final RequestQueue requestQueue = Volley.newRequestQueue(c);
        final String url = cloudantURL + "/" + dbName;
        getLatestRevisionNumber(c, docId, new RevisionNumberListener() {
            @Override
            public void onRevisionNumberReceived(String revNum) {
                try {
                    String stringDB = storage.readTextFile("files", "main.json");
                    JSONObject jsonDB = new JSONObject(stringDB);
                    jsonDB.put("_rev", revNum);
                    JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, jsonDB, new Response.Listener<JSONObject>() {
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
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public static void getLatestRevisionNumber(Context c, String docId, final RevisionNumberListener revisionNumberListener) {
        getRemoteDB(c, docId, new VolleyCallback() {
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
                Log.d("_rev", "" + throwable);
            }
        });
    }
}