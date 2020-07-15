package com.hackathon.covid19tracker;

public interface VolleyCallback{
    void onSuccess(String result);
    void onError(Throwable throwable);
}
