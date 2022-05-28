package com.google.mlkit.vision.demo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

public class NotifiContainer extends AppCompatActivity {

    private TextView contentView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifi_container);

        contentView = findViewById(R.id.content);
        Intent intent = getIntent();
        int data = intent.getIntExtra("notice", 0);
        String content = intent.getStringExtra("content");
        contentView.setText(content);
        Toast.makeText(this, ""+data, Toast.LENGTH_SHORT).show();
    }
}