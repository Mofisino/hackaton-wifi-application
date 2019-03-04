package com.example.interactivitydemo;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.example.interactivitydemo.db.DatabaseHandler;
import com.example.interactivitydemo.db.FeedReaderContract;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    ImageView imgMain;
    DatabaseHandler databaseHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        databaseHandler = new DatabaseHandler(this);

        setContentView(R.layout.activity_main);
        imgMain = findViewById(R.id.img_main);

        SQLiteDatabase db = databaseHandler.getWritableDatabase();

// Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(FeedReaderContract.FeedEntry.COLUMN_SSID, "alabala");
        values.put(FeedReaderContract.FeedEntry.COLUMN_PASSWORD, "12345567");
        values.put(FeedReaderContract.FeedEntry.COLUMN_LATITUDE, "123.12");
        values.put(FeedReaderContract.FeedEntry.COLUMN_LONGITUDE, "11.2");
// Insert the new row, returning the primary key value of the new row
        long newRowId = db.insert(FeedReaderContract.FeedEntry.TABLE_NAME, null, values);

        //readDatabase();
        writeToFirebase();
        readFromFirebase();
    }

    public void clickFunction(View view) {
        Intent myIntent = new Intent(MainActivity.this, MapsActivity.class);
        MainActivity.this.startActivity(myIntent);
    }

    public void writeToFirebase() {
        FirebaseDatabase database = FirebaseDatabase.getInstance(FirebaseApp.initializeApp(this));
        DatabaseReference ref = database.getReference("server/saving-data/networks");
        ref.child("1").setValue(new Network("aubg", "12345678", "122.4", "23.2"));
    }

    public void readFromFirebase() {
        FirebaseDatabase database = FirebaseDatabase.getInstance(FirebaseApp.initializeApp(this));
        DatabaseReference ref = database.getReference("server/saving-data/networks");
        ref.orderByChild("ssid").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Network network = dataSnapshot.getValue(Network.class);
                Log.e("query", network.getSsid());
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void readDatabase() {
        SQLiteDatabase db = databaseHandler.getReadableDatabase();

// Define a projection that specifies which columns from the database
// you will actually use after this query.
        String[] projection = {
                FeedReaderContract.FeedEntry.COLUMN_SSID,
                FeedReaderContract.FeedEntry.COLUMN_PASSWORD,
                FeedReaderContract.FeedEntry.COLUMN_LATITUDE,
                FeedReaderContract.FeedEntry.COLUMN_LONGITUDE,
        };

// Filter results WHERE "title" = 'My Title'
        String selection = FeedReaderContract.FeedEntry.COLUMN_SSID + " = ?";
        String[] selectionArgs = {"alabala"};

// How you want the results sorted in the resulting Cursor
        String sortOrder =
                FeedReaderContract.FeedEntry.COLUMN_PASSWORD + " DESC";

        Cursor cursor = db.query(
                FeedReaderContract.FeedEntry.TABLE_NAME,   // The table to query
                projection,             // The array of columns to return (pass null to get all)
                selection,              // The columns for the WHERE clause
                selectionArgs,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                null               // The sort order
        );

        List itemIds = new ArrayList<>();
        while(cursor.moveToNext()) {
            String itemId = cursor.getString(
                    cursor.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.COLUMN_SSID));
            itemIds.add(itemId);
        }
        cursor.close();
        for (int i = 0; i < itemIds.size(); i++) {
            Log.e("DATA", itemIds.get(i).toString());
        }
    }
}
