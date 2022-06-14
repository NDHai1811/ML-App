package com.google.mlkit.vision.demo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.google.mlkit.vision.demo.notification.Notify;
import com.google.mlkit.vision.demo.notification.Notify_Adapter;
import com.google.mlkit.vision.demo.traffic_sign.CourseAdapter;
import com.google.mlkit.vision.demo.traffic_sign.CourseModal;
import com.google.mlkit.vision.demo.traffic_sign.TrafficSign;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class NotifiContainer extends AppCompatActivity {

    private TextView contentView;
    private RecyclerView courseRV;

    // variable for our adapter
    // class and array list
    private Notify_Adapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        ColorDrawable colorDrawable
                = new ColorDrawable(Color.parseColor("#02457A"));
        getSupportActionBar().setBackgroundDrawable(colorDrawable);
        getSupportActionBar().setTitle("Thông báo");
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_notifi_container);

        // initializing our variables.
        courseRV = findViewById(R.id.notifyrv);

        // calling method to
        // build recycler view.
        buildRecyclerView();
    }
    private ArrayList<Notify> notifylist = new ArrayList<>();
    private void buildRecyclerView() {

        notifylist.add(new Notify("Thông báo hệ thống","Đã có bản cập nhật mới","06/08/2022", "- now hands-free\n" +
                "- redesigned UI\n" +
                "- bug fix\n" +
                "- more alarm sounds\n" +
                "- improved performance\n" +
                "- no-preview mode"));
        notifylist.add(new Notify("Thông báo hệ thống","Hãy điền đầy đủ thông tin của bạn","06/08/2022", ""));

        // initializing our adapter class.
        adapter = new Notify_Adapter(NotifiContainer.this,notifylist);

        // adding layout manager to our recycler view.
        LinearLayoutManager manager = new LinearLayoutManager(this);
        courseRV.setHasFixedSize(true);

        // setting layout manager
        // to our recycler view.
        courseRV.setLayoutManager(manager);

        // setting adapter to
        // our recycler view.
        courseRV.setAdapter(adapter);
    }
}