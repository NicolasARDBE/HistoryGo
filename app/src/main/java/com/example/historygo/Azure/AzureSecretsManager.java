package com.example.historygo.Azure;

import android.util.Log;

import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class AzureSecretsManager {

    private static final String TAG = "AzureSecretsManager";
    private static final String API_GATEWAY_URL = "https://wqxjv7it1h.execute-api.us-east-2.amazonaws.com/dev/secrets";

    private final OkHttpClient client;

    public AzureSecretsManager() {
        this.client = new OkHttpClient();
    }

    public CompletableFuture<String> getSecretValue(String secretKey) {
        CompletableFuture<String> future = new CompletableFuture<>();

        String urlWithParam = API_GATEWAY_URL + "?secretKey=" + secretKey;

        Request request = new Request.Builder()
                .url(urlWithParam)
                .get()
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "Failed to fetch secret: " + e.getMessage());
                future.completeExceptionally(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    future.completeExceptionally(new IOException("Unexpected code " + response));
                    return;
                }

                String responseBody = response.body().string();
                try {
                    JSONObject json = new JSONObject(responseBody);
                    String secretValue = json.getString("value");
                    future.complete(secretValue);
                } catch (Exception e) {
                    future.completeExceptionally(e);
                }
            }
        });

        return future;
    }
}
