package com.example.historygo.awsServices;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoDevice;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUser;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserAttributes;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserPool;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserSession;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.AuthenticationContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.AuthenticationDetails;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.ChallengeContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.MultiFactorAuthenticationContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.AuthenticationHandler;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.GenericHandler;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.SignUpHandler;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.cognitoidentityprovider.model.SignUpResult;
import com.auth0.android.jwt.JWT;
import com.example.historygo.Activities.RatingManagement;

import static  android.content.ContentValues.TAG;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Cognito {
    private final Regions awsRegion = Regions.US_EAST_2;
    private final String identityPoolID = "us-east-2:68e86ddc-a7fa-4a70-9e79-5985e2280a7d";
    private final String userPoolID = "us-east-2_OuaxYxAbc";
    private final String clientID = "6qcath8m13v7iv8tf33aafd8n5";
    private final CognitoUserPool userPool;
    private final CognitoUserAttributes userAttributes; // Used for adding attributes to the user
    private final Context appContext;
    private String userPassword;
    @SuppressLint("StaticFieldLeak")
    private static CognitoUser cognitoUser;

    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public Cognito(Context context) {
        this.appContext = context;
        //Information about Cognito Pool

        userPool = new CognitoUserPool(context, userPoolID, clientID, null, this.awsRegion);
        userAttributes = new CognitoUserAttributes();
    }

    public void signUpInBackground(String userId, String password){
        userPool.signUpInBackground(userId, password, this.userAttributes, null, signUpCallback);
        //userPool.signUp(userId, password, this.userAttributes, null, signUpCallback);
    }

    SignUpHandler signUpCallback = new SignUpHandler() {
        @Override
        public void onSuccess(CognitoUser user, SignUpResult signUpResult) {
            // Sign-up was successful
            Log.d(TAG, "Sign-up success");
            Toast.makeText(appContext,"Sign-up success", Toast.LENGTH_LONG).show();
            // Check if this user (cognitoUser) needs to be confirmed
            if(!signUpResult.getUserConfirmed()) {
                // This user must be confirmed and a confirmation code was sent to the user
                // cognitoUserCodeDeliveryDetails will indicate where the confirmation code was sent
                // Get the confirmation code from user
            }
            else {
                Toast.makeText(appContext,"Error: User Confirmed before", Toast.LENGTH_LONG).show();
                // The user has already been confirmed
            }
        }

        @Override
        public void onFailure(Exception exception) {
            Toast.makeText(appContext,"Sign-up failed", Toast.LENGTH_LONG).show();
            Log.d(TAG, "Sign-up failed: " + exception);
        }
    };

    public void confirmUser(String userId, String code){
        cognitoUser =  userPool.getUser(userId);
        cognitoUser.confirmSignUpInBackground(code,false, confirmationCallback);
        //cognitoUser.confirmSignUp(code,false, confirmationCallback);
    }
    // Callback handler for confirmSignUp API

    GenericHandler confirmationCallback = new GenericHandler() {
        @Override
        public void onSuccess() {
            // User was successfully confirmed
            Toast.makeText(appContext,"User Confirmed", Toast.LENGTH_LONG).show();
        }

        @Override
        public void onFailure(Exception exception) {
            // User confirmation failed. Check exception for the cause.
        }
    };

    public void addAttribute(String key, String value){
        userAttributes.addAttribute(key, value);
    }

    public void userLogin(String userId, String password){
        cognitoUser = userPool.getUser(userId);
        this.userPassword = password;
        cognitoUser.getSessionInBackground(authenticationHandler);
    }

    // Callback handler for the sign-in process
    AuthenticationHandler authenticationHandler = new AuthenticationHandler() {
        @Override
        public void authenticationChallenge(ChallengeContinuation continuation) {

        }
        @Override
        public void onSuccess(CognitoUserSession userSession, CognitoDevice newDevice) {
            Toast.makeText(appContext,"Sign in success", Toast.LENGTH_LONG).show();

            CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(appContext, identityPoolID, awsRegion);

            String jwtToken = userSession.getIdToken().getJWTToken();

            Map<String, String> logins = new HashMap<String, String>();
            logins.put("cognito-idp.us-east-2.amazonaws.com/"+userPoolID, userSession.getIdToken().getJWTToken());
            credentialsProvider.setLogins(logins);

            String identityId = credentialsProvider.getIdentityId();
            Log.i("AWS", "Identity ID obtenido: " + identityId);


            Intent intent = new Intent(appContext, RatingManagement.class);
            intent.putExtra("JWTTOKEN", jwtToken);

            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            appContext.startActivity(intent);

        }
        @Override
        public void getAuthenticationDetails(AuthenticationContinuation authenticationContinuation, String userId) {
            // The API needs user sign-in credentials to continue
            AuthenticationDetails authenticationDetails = new AuthenticationDetails(userId, userPassword, null);
            // Pass the user sign-in credentials to the continuation
            authenticationContinuation.setAuthenticationDetails(authenticationDetails);
            // Allow the sign-in to continue
            authenticationContinuation.continueTask();
        }
        @Override
        public void getMFACode(MultiFactorAuthenticationContinuation multiFactorAuthenticationContinuation) {
            // Multi-factor authentication is required; get the verification code from user
            //multiFactorAuthenticationContinuation.setMfaCode(mfaVerificationCode);
            // Allow the sign-in process to continue
            //multiFactorAuthenticationContinuation.continueTask();
        }
        @Override
        public void onFailure(Exception exception) {
            // Sign-in failed, check exception for the cause
            Toast.makeText(appContext,"Sign in Failure.\n" + exception.toString(), Toast.LENGTH_LONG).show();
        }
    };

    public void UserSignOut(){
        cognitoUser.signOut();
    }
    public CognitoCachingCredentialsProvider getCognitoCachingCredentialsProvider(){
        return new CognitoCachingCredentialsProvider(appContext, identityPoolID, awsRegion);
    }
}