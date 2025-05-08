package com.example.historygo.AwsServices;

import android.content.Context;
import android.util.Log;

import com.amazonaws.mobileconnectors.dynamodbv2.document.Table;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class DynamoDBService {
    private static final String RATING_DYNAMODB_TABLE = "Rating";
    private static final String TOURIST_SPOT_DYNAMODB_TABLE = "TouristSpot";
    private final Regions awsRegion = Regions.US_EAST_2;
    public static AmazonDynamoDBClient dbClient;
    private Table dbTable;
    private static Cognito cognito;
    private DynamoDBInitializationCallback callback;


    public DynamoDBService(Context context) {

        CognitoManager.Companion.getInstance(context, new Function1<Cognito, Unit>() {
            public Unit invoke(Cognito cognitoInstance) {
                if (cognitoInstance != null) {
                    // Cognito inicializado, úsalo
                    cognito = cognitoInstance;
                } else {
                    Log.e("MyActivity", "Error: Cognito es null");
                }
                return Unit.INSTANCE;
            }
        });

        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(this::dynamoConnectionAndAuth);
    }

    public void dynamoConnectionAndAuth() {
        String identityId = cognito.getCognitoCachingCredentialsProvider().getIdentityId();
        dbClient = new AmazonDynamoDBClient(cognito.getCognitoCachingCredentialsProvider());
        dbClient.setRegion(Region.getRegion(awsRegion));
        callback.onDynamoDBInitialized();
    }

    public Table setTableRating(){
        dbTable = Table.loadTable(dbClient, RATING_DYNAMODB_TABLE);
        return dbTable;
    }

    public Table setTableTouristSpot() {
        dbTable = Table.loadTable(dbClient, TOURIST_SPOT_DYNAMODB_TABLE);
        return dbTable;
    }

    public void setCallback(DynamoDBInitializationCallback callback) {
        this.callback = callback;
    }
}