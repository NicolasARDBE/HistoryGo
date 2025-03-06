package com.example.historygo.awsServices;

import android.content.Context;

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

public class DynamoDBService {
    private static final String DYNAMODB_TABLE = "Review";
    public static AmazonDynamoDBClient dbClient;
    private static Cognito cognito;
    private Context context;
    private Table dbTable;

    public DynamoDBService(String accessToken, Context context) {
        this.context = context;
        dynamoConnection();
    }

    //CRUD

    public void dynamoConnection() {
        dbClient = new AmazonDynamoDBClient(cognito.MyDynamoDBHelper(context));
        Table dbTable = Table.loadTable(dbClient, DYNAMODB_TABLE);
    }

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