package com.google.mlkit.vision.demo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class NotiDetailActivity extends AppCompatActivity {
    private TextView tiltie,body;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.noti_detail);
        Intent intent = getIntent();
        String tieude = intent.getStringExtra("notice");
        String noidung = intent.getStringExtra("content");
        tiltie=findViewById(R.id.tiltle);
        body=findViewById(R.id.body);
        tiltie.setText(tieude);
        body.setText(noidung);
    }
}