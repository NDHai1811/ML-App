package com.google.mlkit.vision.demo.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.mlkit.vision.demo.R;
import com.google.mlkit.vision.demo.history.History;
import com.google.mlkit.vision.demo.history.History_Adapter;
import com.google.mlkit.vision.demo.realtime_data.DataInfo;
import com.google.mlkit.vision.demo.realtime_data.DataInfo_Adapter;
import com.google.mlkit.vision.demo.traffic_sign.CourseAdapter;
import com.google.mlkit.vision.demo.traffic_sign.CourseModal;
import com.google.mlkit.vision.demo.traffic_sign.TrafficSign;
import com.google.mlkit.vision.demo.trafficsign;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class TestUi extends AppCompatActivity {

    RecyclerView historyRV;
    private ArrayList<History> histories;
    private RecyclerView.Adapter adapter;
    ArrayList<History> list = new ArrayList<>();
    Button button, btn;
    String startDestination, endDestination, totalTime, sleepyCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle("Lịch trình");
        setContentView(R.layout.activity_test_ui);

        loadData();
        historyRV = findViewById(R.id.historyRV);
        histories = readCSVData();
        adapter = new History_Adapter(histories);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        this.historyRV.setLayoutManager(linearLayoutManager);


        this.historyRV.setAdapter(adapter);
    }


    private ArrayList<History> readCSVData() {
//            list.add(new History("start1", "end", "time", "counter"));
//            list.add(new History("start2", "end", "time", "counter"));
        return list;
    }

    private void loadData() {
        // method to load arraylist from shared prefs
        // initializing our shared prefs with name as
        // shared preferences.
        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);

        // creating a variable for gson.
        Gson gson = new Gson();

        // below line is to get to string present from our
        // shared prefs if not present setting it as null.
        String json = sharedPreferences.getString("courses", null);

        // below line is to get the type of our array list.
        Type type = new TypeToken<ArrayList<History>>() {}.getType();

        // in below line we are getting data from gson
        // and saving it to our array list
        list = gson.fromJson(json, type);

        // checking below if the array list is empty or not
        if (list == null) {
            // if the array list is empty
            // creating a new array list.
            list = new ArrayList<>();
        }
    }

    private void saveData() {
        // method for saving the data in array list.
        // creating a variable for storing data in
        // shared preferences.
        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);

        // creating a variable for editor to
        // store data in shared preferences.
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // creating a new variable for gson.
        Gson gson = new Gson();

        // getting data from gson and storing it in a string.
        String json = gson.toJson(list);

        // below line is to save data in shared
        // prefs in the form of string.
        editor.putString("courses", json);

        // below line is to apply changes
        // and save data in shared prefs.
        editor.apply();
    }
}