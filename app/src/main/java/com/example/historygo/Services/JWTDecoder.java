package com.example.historygo.Services;

import android.util.Log;

import com.auth0.android.jwt.JWT;

public class JWTDecoder {
    public String decodeJWTCognitoUsername(String jwtToken) {
        try {
            JWT jwt = new JWT(jwtToken);
            return jwt.getClaim("cognito:username").asString();
        } catch (Exception e) {
            Log.e("JWT", "Error al decodificar el JWT: " + e.getMessage());
        }
        return null;
    }
}
