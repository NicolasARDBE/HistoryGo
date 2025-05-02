package com.example.historygo.AwsServices;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoDevice;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUser;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserAttributes;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserCodeDeliveryDetails;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserDetails;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserPool;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserSession;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.AuthenticationContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.AuthenticationDetails;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.ChallengeContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.ForgotPasswordContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.MultiFactorAuthenticationContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.AuthenticationHandler;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.ForgotPasswordHandler;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.GenericHandler;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.GetDetailsHandler;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.SignUpHandler;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.UpdateAttributesHandler;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.cognitoidentityprovider.model.InvalidParameterException;
import com.amazonaws.services.cognitoidentityprovider.model.InvalidPasswordException;
import com.amazonaws.services.cognitoidentityprovider.model.NotAuthorizedException;
import com.amazonaws.services.cognitoidentityprovider.model.SignUpResult;
import com.amazonaws.services.cognitoidentityprovider.model.UserNotConfirmedException;
import com.amazonaws.services.cognitoidentityprovider.model.UserNotFoundException;
import com.example.historygo.Activities.ExpererienceMenuActivity;
import com.example.historygo.Azure.AzureSecretsManager;
import com.example.historygo.R;

import static android.content.ContentValues.TAG;
import static android.content.Context.MODE_PRIVATE;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Cognito {
    private final Regions awsRegion = Regions.US_EAST_2;
    private String identityPoolID;
    private String userPoolID;
    private String clientID;
    private CognitoUserPool userPool;
    private CognitoUserAttributes userAttributes; // Used for adding attributes to the user
    private final Context appContext;
    private String userPassword;
    private ForgotPasswordContinuation forgotPasswordContinuation;
    @SuppressLint("StaticFieldLeak")
    private static CognitoUser cognitoUser;


    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public interface OnInitializedCallback {
        void onInitialized(Cognito cognito);
        void onError(Exception e);
    }

    public Cognito(Context context, OnInitializedCallback callback) {
        this.appContext = context;

        AzureSecretsManager secretsManager = new AzureSecretsManager();

        // Obtener los secretos de Azure de manera asíncrona
        CompletableFuture<Void> secretsLoaded = new CompletableFuture<>();

        // Obtener secretos de Azure
        secretsManager.getSecretValue("identityPoolID").thenCompose(identityPool ->
                secretsManager.getSecretValue("userPoolID").thenCombine(
                        secretsManager.getSecretValue("clientID"),
                        (userPoolId, clientId) -> {
                            this.identityPoolID = identityPool;
                            this.userPoolID = userPoolId;
                            this.clientID = clientId;

                            // Inicializar userPool y atributos
                            this.userPool = new CognitoUserPool(appContext, userPoolID, clientID, null, awsRegion);
                            this.userAttributes = new CognitoUserAttributes();
                            secretsLoaded.complete(null);
                            return null;
                        }
                )
        ).exceptionally(e -> {
            callback.onError(new Exception("Error al obtener secretos", e));
            return null;
        });

        secretsLoaded.thenRun(() -> callback.onInitialized(this));
    }

    public void signUpInBackground(String userId, String password) {
        userPool.signUpInBackground(userId, password, this.userAttributes, null, signUpCallback);
    }

    SignUpHandler signUpCallback = new SignUpHandler() {
        @Override
        public void onSuccess(CognitoUser user, SignUpResult signUpResult) {
            Log.d(TAG, "Sign-up success");
            Toast.makeText(appContext, "Sign-up success", Toast.LENGTH_LONG).show();
            if (!signUpResult.getUserConfirmed()) {
                // User must be confirmed
            } else {
                Toast.makeText(appContext, "Error: User Confirmed before", Toast.LENGTH_LONG).show();
            }
        }

        @Override
        public void onFailure(Exception exception) {
            String errorMessage = "Error en el registro:" + exception.getMessage();

            if (exception instanceof UserNotConfirmedException) {
                errorMessage = appContext.getString(R.string.signup_error_user_not_confirmed);
            } else if (exception instanceof InvalidPasswordException) {
                errorMessage =  appContext.getString(R.string.signup_error_invalid_password);
            }

            Toast.makeText(appContext, errorMessage, Toast.LENGTH_LONG).show();
            Log.d(TAG, "Sign-up failed: " + exception);
        }
    };

    public void confirmUser(String userId, String code) {
        cognitoUser = userPool.getUser(userId);
        cognitoUser.confirmSignUpInBackground(code, false, confirmationCallback);
    }

    GenericHandler confirmationCallback = new GenericHandler() {
        @Override
        public void onSuccess() {
            Toast.makeText(appContext, R.string.user_confirmed, Toast.LENGTH_LONG).show();
        }

        @Override
        public void onFailure(Exception exception) {
            // User confirmation failed.
        }
    };

    public void addAttribute(String key, String value) {
        userAttributes.addAttribute(key, value);
    }

    public void userLogin(String userId, String password) {
        try {
            if (userPool == null) {
                throw new NullPointerException("userPool is null");
            }
            cognitoUser = userPool.getUser(userId);
            this.userPassword = password;
            if (cognitoUser == null) {
                throw new NullPointerException("Failed to get CognitoUser from userPool");
            }
            cognitoUser.getSessionInBackground(authenticationHandler);
        } catch (Exception e) {
            Log.e("CognitoLogin", "Error during login: " + e.getMessage(), e);
            Toast.makeText(appContext, R.string.connection_error, Toast.LENGTH_LONG).show();
        }
    }

    AuthenticationHandler authenticationHandler = new AuthenticationHandler() {
        @Override
        public void authenticationChallenge(ChallengeContinuation continuation) {}

        @Override
        public void onSuccess(CognitoUserSession userSession, CognitoDevice newDevice) {
            Toast.makeText(appContext, R.string.sign_in_success, Toast.LENGTH_LONG).show();

            CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(
                    appContext, identityPoolID, awsRegion
            );

            String jwtToken = userSession.getIdToken().getJWTToken();

            Map<String, String> logins = new HashMap<>();
            logins.put("cognito-idp.us-east-2.amazonaws.com/" + userPoolID, jwtToken);
            credentialsProvider.setLogins(logins);

            executor.execute(() -> {
                try {
                    String identityId = credentialsProvider.getIdentityId();
                    Log.i("AWS", "Identity ID obtenido: " + identityId);

                } catch (Exception e) {
                    Log.e("AWS", "Error al obtener Identity ID", e);
                }
            });

            SharedPreferences sharedPreferences = appContext.getSharedPreferences("auth", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("jwt_token", jwtToken);
            editor.apply();

            Intent intent = new Intent(appContext, ExpererienceMenuActivity.class);
            intent.putExtra("JWTTOKEN", jwtToken);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            appContext.startActivity(intent);
        }

        @Override
        public void getAuthenticationDetails(AuthenticationContinuation authenticationContinuation, String userId) {
            AuthenticationDetails authenticationDetails = new AuthenticationDetails(userId, userPassword, null);
            authenticationContinuation.setAuthenticationDetails(authenticationDetails);
            authenticationContinuation.continueTask();
        }

        @Override
        public void getMFACode(MultiFactorAuthenticationContinuation multiFactorAuthenticationContinuation) {}

        @Override
        public void onFailure(Exception exception) {
            String message = "Error en inicio de sesión: " + exception.getMessage();
            Log.d("excepcion inicio sesion", exception.getMessage());
            if (exception instanceof UserNotConfirmedException) {
                message = appContext.getString(R.string.login_error_unconfirmed);
            } else if (exception instanceof NotAuthorizedException) {
                message = appContext.getString(R.string.login_error_unauthorized);
            } else if (exception instanceof UserNotFoundException) {
                message = appContext.getString(R.string.login_error_not_found);
            }
            Toast.makeText(appContext, message, Toast.LENGTH_LONG).show();
        }
    };

    public void UserSignOut() {
        cognitoUser.signOut();
    }

    public void forgotPassword(String userId) {
        cognitoUser = userPool.getUser(userId);
        cognitoUser.forgotPasswordInBackground(forgotPasswordHandler);
    }

    ForgotPasswordHandler forgotPasswordHandler = new ForgotPasswordHandler() {
        @Override
        public void onSuccess() {
            Toast.makeText(appContext, R.string.password_reset_success, Toast.LENGTH_LONG).show();
        }

        @Override
        public void getResetCode(ForgotPasswordContinuation continuation) {
            forgotPasswordContinuation = continuation;
            Toast.makeText(appContext, R.string.verification_code_sent, Toast.LENGTH_LONG).show();
        }

        @Override
        public void onFailure(Exception exception) {
            String errorMessage = "Error al restablecer la contraseña: " + exception.getMessage();

            if (exception instanceof UserNotFoundException) {
                errorMessage = appContext.getString(R.string.password_reset_error_not_found);
            } else if (exception instanceof InvalidParameterException) {
                errorMessage = appContext.getString(R.string.password_reset_error_invalid_code);
            }

            Toast.makeText(appContext, errorMessage, Toast.LENGTH_LONG).show();
            Log.e(TAG, "Error en recuperación de contraseña", exception);
        }
    };

    public void confirmForgotPassword(String verificationCode, String newPassword) {
        if (forgotPasswordContinuation != null) {
            forgotPasswordContinuation.setPassword(newPassword);
            forgotPasswordContinuation.setVerificationCode(verificationCode);
            forgotPasswordContinuation.continueTask();
        } else {
            Toast.makeText(appContext, R.string.request_password_reset_first, Toast.LENGTH_LONG).show();
        }
    }

    public CognitoCachingCredentialsProvider getCognitoCachingCredentialsProvider() {
        return new CognitoCachingCredentialsProvider(appContext, identityPoolID, awsRegion);
    }

    public void updateUserAttributes(String familyName) {
        userAttributes.addAttribute("family_name", familyName);
        cognitoUser.updateAttributesInBackground(userAttributes, new UpdateAttributesHandler() {
            @Override
            public void onSuccess(List<CognitoUserCodeDeliveryDetails> attributesVerificationList) {
                Toast.makeText(appContext, R.string.attributes_updated, Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onFailure(Exception exception) {
                Toast.makeText(appContext, R.string.attributes_update_failed + exception.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                Log.e("Cognito", "Attribute update error", exception);
            }
        });
    }

    public void getUserAttributes(OnAttributesReceivedCallback callback) {
        cognitoUser.getDetailsInBackground(new GetDetailsHandler() {
            @Override
            public void onSuccess(CognitoUserDetails cognitoUserDetails) {
                Map<String, String> attributes = cognitoUserDetails.getAttributes().getAttributes();
                callback.onReceived(attributes); // Devuelve los atributos por el callback
            }

            @Override
            public void onFailure(Exception exception) {
                callback.onError(exception); // Devuelve el error por el callback
            }
        });
    }

    public void changePassword(String oldPassword, String newPassword) {
        // Change the user's password
        cognitoUser.changePasswordInBackground(oldPassword, newPassword, new GenericHandler() {
            @Override
            public void onSuccess() {
                Toast.makeText(appContext, R.string.password_change_success, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Exception exception) {
                Toast.makeText(appContext, R.string.password_change_error + exception.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}