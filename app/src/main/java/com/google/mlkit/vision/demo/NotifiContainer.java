package com.google.mlkit.vision.demo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.TaskStackBuilder;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.mlkit.vision.demo.ui.MainActivity;

public class NotifiContainer extends AppCompatActivity {

    private TextView textView5,textView8;
    private Button content;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifi_container);
        content=findViewById(R.id.content);
        content.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            sendNotification("bruh","lmao","123");
            }
            private void sendNotification(String messageTitle,String messageBody, String clickAction) {
                int NOTIFICATION_ID = 234;
                NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                String CHANNEL_ID = null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    CHANNEL_ID = "my_channel_01";
                    CharSequence name = "my_channel";
                    String Description = "This is my channel";
                    int importance = NotificationManager.IMPORTANCE_HIGH;
                    NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
                    mChannel.setDescription(Description);
                    mChannel.enableLights(true);
                    mChannel.setLightColor(Color.RED);
                    mChannel.enableVibration(true);
                    mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
                    mChannel.setShowBadge(false);
                    notificationManager.createNotificationChannel(mChannel);

                }

                NotificationCompat.Builder builder = new NotificationCompat.Builder(NotifiContainer.this, CHANNEL_ID)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle(messageTitle)
                        .setContentText(messageBody)
                        .setAutoCancel(true);

                Intent resultIntent = new Intent(NotifiContainer.this, NotiDetailActivity.class);
                resultIntent.putExtra("notice", messageTitle);
                resultIntent.putExtra("content", messageBody);
                TaskStackBuilder stackBuilder = TaskStackBuilder.create(NotifiContainer.this);
                stackBuilder.addParentStack(MainActivity.class);
                stackBuilder.addNextIntent(resultIntent);
                PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
                builder.setContentIntent(resultPendingIntent);
                notificationManager.notify(NOTIFICATION_ID, builder.build());
            }
        });



//        contentView = findViewById(R.id.content);
//        Intent intent = getIntent();
//        int data = intent.getIntExtra("notice", 0);
//        String content = intent.getStringExtra("content");
//        contentView.setText(content);
//        Toast.makeText(this, ""+data, Toast.LENGTH_SHORT).show();
    }
}