package com.example.historygo.awsServices;

import android.content.Context;
import android.util.Log;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.tokens.CognitoAccessToken;
import com.amazonaws.mobileconnectors.dynamodbv2.document.Table;
import com.amazonaws.mobileconnectors.dynamodbv2.document.UpdateItemOperationConfig;
import com.amazonaws.mobileconnectors.dynamodbv2.document.datatype.Document;
import com.amazonaws.mobileconnectors.dynamodbv2.document.datatype.Primitive;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.model.ReturnValue;
import com.example.historygo.Mapper.RatingMapper;
import com.example.historygo.dto.RatingDto;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class DynamoDBService {
    private static final String DYNAMODB_TABLE = "Rating";
    private final Regions awsRegion = Regions.US_EAST_2;
    public static AmazonDynamoDBClient dbClient;
    private Table dbTable;
    private static Cognito cognito;
    private DynamoDBInitializationCallback callback;

    public DynamoDBService(Context context) {
        cognito = new Cognito(context);
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(this::dynamoConnectionAndAuth);
    }

    public void dynamoConnectionAndAuth() {
        String identityId = cognito.getCognitoCachingCredentialsProvider().getIdentityId();
        Log.i("AWS2", "Identity ID obtenido: " + identityId);
        dbClient = new AmazonDynamoDBClient(cognito.getCognitoCachingCredentialsProvider());
        dbClient.setRegion(Region.getRegion(awsRegion));
        dbTable = Table.loadTable(dbClient, DYNAMODB_TABLE);

        // Llamar al callback cuando la conexión esté lista
        if (callback != null) {
            callback.onDynamoDBInitialized();
        }
    }

    //CRUD
    public void createRating(RatingDto ratingDto) {
        dbTable.putItem(RatingMapper.ratingToDynamoDocument(ratingDto));
    }

    public void update(RatingDto ratingDto) {
        Document ratingDoc = RatingMapper.ratingToDynamoDocument(ratingDto);
        Document doc = dbTable.updateItem(ratingDoc, new UpdateItemOperationConfig().withReturnValues(ReturnValue.ALL_NEW));
    }


    public void delete(Document rating) {
        dbTable.deleteItem(
                Objects.requireNonNull(rating.get("touristSpotId")).asPrimitive(),   // The Partition Key
                Objects.requireNonNull(rating.get("ratingId")).asPrimitive());  // The Hash Key
    }

    public Document getRatingById(Number touristSpotId, Number ratingId) {
        if (dbTable == null) {
            Log.e("DynamoDBService", "dbTable is null, cannot query DynamoDB");
            return null;
        }
        return dbTable.getItem(
                new Primitive(touristSpotId),
                new Primitive(ratingId));
    }

    public List<Document> getAllRatings() {
        return dbTable.query(new Primitive(cognito.getCognitoCachingCredentialsProvider().getCachedIdentityId())).getAllResults();
    }

    public void setCallback(DynamoDBInitializationCallback callback) {
        this.callback = callback;
    }
}