package com.google.mlkit.vision.demo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Window;
import android.widget.TextView;

public class NotiDetailActivity extends AppCompatActivity {
    private TextView tiltie,body;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        ColorDrawable colorDrawable
                = new ColorDrawable(Color.parseColor("#02457A"));
        getSupportActionBar().setBackgroundDrawable(colorDrawable);
        getSupportActionBar().setTitle("Thông báo");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.noti_detail);
        Intent intent = getIntent();
        String tieude = intent.getStringExtra("title");
        String noidung = intent.getStringExtra("detail");
        tiltie=findViewById(R.id.tiltle);
        body=findViewById(R.id.body);
        tiltie.setText(tieude);
        body.setText(noidung);
    }
}