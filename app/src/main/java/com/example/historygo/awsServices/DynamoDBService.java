package com.example.historygo.awsServices;

import android.content.Context;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.dynamodbv2.document.Table;
import com.amazonaws.mobileconnectors.dynamodbv2.document.UpdateItemOperationConfig;
import com.amazonaws.mobileconnectors.dynamodbv2.document.datatype.Document;
import com.amazonaws.mobileconnectors.dynamodbv2.document.datatype.Primitive;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.model.ReturnValue;
import com.example.historygo.Mapper.RatingMapper;
import com.example.historygo.dto.RatingDto;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class DynamoDBService {
    private static final String DYNAMODB_TABLE = "Review";
    public static AmazonDynamoDBClient dbClient;
    private static Cognito cognito;
    private final Context context;
    private Table dbTable;

    public DynamoDBService(String accessToken, Context context) {
        this.context = context;
        cognito = new Cognito(this.context);
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> dynamoConnection(accessToken));
    }


    public void dynamoConnection(String idToken) {
        try {
            Future<CognitoCachingCredentialsProvider> future = cognito.getDynamoDBCredentials(idToken);
            CognitoCachingCredentialsProvider credentialsProvider = future.get();  // Espera la credencial

            dbClient = new AmazonDynamoDBClient(credentialsProvider);
            dbTable = Table.loadTable(dbClient, DYNAMODB_TABLE);
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
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

    public Document getRatingById(String ratingId) {
        return dbTable.getItem(new Primitive(cognito.MyDynamoDBHelper(context).getCachedIdentityId()), new Primitive("ratingId"));
    }

    public List<Document> getAllRatings() {
        return dbTable.query(new Primitive(cognito.MyDynamoDBHelper(context).getCachedIdentityId())).getAllResults();
    }
}