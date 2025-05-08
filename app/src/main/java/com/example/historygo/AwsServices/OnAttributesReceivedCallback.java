package com.example.historygo.AwsServices;

import java.util.Map;

public interface OnAttributesReceivedCallback {
    void onReceived(Map<String, String> attributes);
    void onError(Exception exception);
}