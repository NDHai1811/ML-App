package com.google.mlkit.vision.demo.java;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.MediaRouteButton;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.collection.ArraySet;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.MultiProcessor;
import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.mlkit.vision.demo.CameraSourcePreview;
import com.google.mlkit.vision.demo.GraphicOverlay;
import com.google.mlkit.vision.demo.R;
import com.google.mlkit.vision.demo.map.LatLong;
import com.google.mlkit.vision.demo.map.MyLocationService;
import com.google.mlkit.vision.demo.ui.MainActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;


public final class LivePreviewActivity extends AppCompatActivity {
  private static final String TAG = "FaceTracker";

  private CameraSource mCameraSource = null;
  private ImageView end_button;
  private ToggleButton n_mode;
  private TextView tv,tv_1,clock;
  static int count = 0,count1=0;
  private ConstraintLayout layout;
  private MediaPlayer mp;
  private CameraSourcePreview mPreview;
  private GraphicOverlay mGraphicOverlay;
  private String start_2;
  private String key ="facetrackeractivity";
  private String key_2 = "callbackcat's project";
  private String key_3 = "hello";
  private String key_4 = "senstivity";
  private int s_status,s_time;
  private static final int RC_HANDLE_GMS = 9001;

  private static final int RC_HANDLE_CAMERA_PERM = 2;
  public int flag = 0;

  private CardView cameraCV;
  Vibrator vibrator;
  private static final int PERMISSION_REQUESTS = 1;
  ArrayList<LatLong> latLongs = new ArrayList<>();
  FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
  DatabaseReference databaseReference = firebaseDatabase.getReference("user");
  Geocoder geocoder;
  private List<Address> start;
  private List<Address> end;
  private int seconds;
  private String time;
  ArrayList<LatLong> detectedLocations = new ArrayList<>();
  ArrayList<String> detectedTime = new ArrayList<>();
  FirebaseStorage storage;
  StorageReference storageReference;
  String myUrl;
  private ArrayList<String> myUrlList;
  private ProgressBar mProgressBar;
  private TextView loadTV;
  int counter;


  @Override
  public void onCreate(Bundle icicle) {
    requestWindowFeature(Window.FEATURE_NO_TITLE);
    Objects.requireNonNull(getSupportActionBar()).hide();
//    getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//            WindowManager.LayoutParams.FLAG_FULLSCREEN);
    View decorView = getWindow().getDecorView();
    decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    getWindow().setStatusBarColor(Color.TRANSPARENT);
    super.onCreate(icicle);
    setContentView(R.layout.activity_vision_live_preview);
    mPreview = (CameraSourcePreview) findViewById(R.id.preview);
    mGraphicOverlay = (GraphicOverlay) findViewById(R.id.faceOverlay);
    end_button = (ImageView)findViewById(R.id.button);
    layout = (ConstraintLayout)findViewById(R.id.llCameraView);
    cameraCV = findViewById(R.id.camera);
    mProgressBar = findViewById(R.id.progressBar);
    loadTV = findViewById(R.id.loadTV);
    n_mode=(ToggleButton)findViewById(R.id.toggleButton);
    n_mode.setTextOn("Máy ảnh: bật");
    n_mode.setText("Máy ảnh");
    n_mode.setTextOff("Máy ảnh: tắt");
    n_mode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
      @Override
      public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if(isChecked) {
          cameraCV.setVisibility(View.VISIBLE);
//          mPreview.setVisibility(View.INVISIBLE);
//          Toast.makeText(getApplicationContext(),"Increase Brightness to maximum for higher accuracy",Toast.LENGTH_LONG).show();
        }
        else
        {
          cameraCV.setVisibility(View.INVISIBLE);
        }
      }
    });
    tv = (TextView)findViewById(R.id.textView3);
    tv_1 = (TextView)findViewById(R.id.textView4);
    clock = findViewById(R.id.clock);
    startClock();
    final AudioManager audioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
    int c = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
    if(c==0)
    {
      Toast.makeText(getApplicationContext(),"Thiết bị đang ở chế độ im lặng",Toast.LENGTH_LONG).show();
    }
    Intent intent_2 = getIntent();
    final String start = intent_2.getStringExtra(key_2);
    start_2=start;
    String time_info = intent_2.getStringExtra(key_4);
    s_status = 4;
    if(s_status == 0)
    {
      s_time = 500;
    }
    else if(s_status == 1)
    {
      s_time = 750;
    }
    else if(s_status == 2)
    {
      s_time = 1000;
    }
    else if(s_status == 3)
    {
      s_time = 1250;
    }
    else if(s_status == 4)
    {
      s_time = 1500;
    }
    else if(s_status == 5)
    {
      s_time = 1750;
    }
    else if(s_status == 6)
    {
      s_time = 2000;
    }
    else if(s_status == 7)
    {
      s_time = 2250;
    }
    else if(s_status == 8)
    {
      s_time = 2500;
    }

    end_button.setOnTouchListener(new View.OnTouchListener() {
      @Override
      public boolean onTouch(View v, MotionEvent event) {
//        Intent next = new Intent(LivePreviewActivity.this,MainActivity.class);
//        startActivity(next);
//        stopLocationService();
//        LivePreviewActivity.this.finish();
        exit();
        return false;
      }
    });



    int rc = ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
    if (rc == PackageManager.PERMISSION_GRANTED) {
      createCameraSource();
    } else {
      requestCameraPermission();
    }

    if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
      ActivityCompat.requestPermissions(
              LivePreviewActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUESTS
      );
    }else{
      startLocationService();
    }

    LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter("intentKey"));
  }

  private void requestCameraPermission() {
    Log.w(TAG, "Camera permission is not granted. Requesting permission");

    final String[] permissions = new String[]{Manifest.permission.CAMERA};

    if (!ActivityCompat.shouldShowRequestPermissionRationale(this,
            Manifest.permission.CAMERA)) {
      ActivityCompat.requestPermissions(this, permissions, RC_HANDLE_CAMERA_PERM);
      return;
    }

    final Activity thisActivity = this;

    View.OnClickListener listener = new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        ActivityCompat.requestPermissions(thisActivity, permissions,
                RC_HANDLE_CAMERA_PERM);
      }
    };

    Snackbar.make(mGraphicOverlay, R.string.permission_camera_rationale,
                    Snackbar.LENGTH_INDEFINITE)
            .setAction(R.string.ok, listener)
            .show();

  }

  private void createCameraSource() {

    Context context = getApplicationContext();
    FaceDetector detector = new FaceDetector.Builder(context)
            .setClassificationType(FaceDetector.ALL_CLASSIFICATIONS)
            .build();

    detector.setProcessor(
            new MultiProcessor.Builder<>(new GraphicFaceTrackerFactory())
                    .build());

    if (!detector.isOperational()) {

//      Toast.makeText(getApplicationContext(),"Dependencies are not yet available. ",Toast.LENGTH_LONG).show();
      Log.w(TAG, "Face detector dependencies are not yet available.");
    }

    mCameraSource = new CameraSource.Builder(context, detector)
            .setRequestedPreviewSize(640, 480)
            .setFacing(CameraSource.CAMERA_FACING_FRONT)
            .setRequestedFps(45.0f)
            .build();

  }


  @Override
  protected void onResume() {
    super.onResume();
    startCameraSource();

  }

  @Override
  protected void onPause() {
    super.onPause();
    mPreview.stop();
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    if (mCameraSource != null) {
      mCameraSource.release();
    }
    stopLocationService();
    stop_playing();
  }

  @Override
  public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
    if (requestCode != RC_HANDLE_CAMERA_PERM) {
      Log.d(TAG, "Got unexpected permission result: " + requestCode);
      super.onRequestPermissionsResult(requestCode, permissions, grantResults);
      return;
    }

    if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
      Log.d(TAG, "Camera permission granted - initialize the camera source");
      // we have permission, so create the camerasource
      createCameraSource();
      return;
    }

    Log.e(TAG, "Permission not granted: results len = " + grantResults.length +
            " Result code = " + (grantResults.length > 0 ? grantResults[0] : "(empty)"));

    DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
      public void onClick(DialogInterface dialog, int id) {
        finish();
      }
    };

    AlertDialog.Builder builder = new AlertDialog.Builder(this);
    builder.setTitle("ALERT")
            .setMessage(R.string.no_camera_permission)
            .setPositiveButton(R.string.ok, listener)
            .show();
  }

  private void startCameraSource() {


    int code = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(
            getApplicationContext());
    if (code != ConnectionResult.SUCCESS) {
      Dialog dlg =
              GoogleApiAvailability.getInstance().getErrorDialog(this, code, RC_HANDLE_GMS);
      dlg.show();
    }

    if (mCameraSource != null) {
      try {
        mPreview.start(mCameraSource, mGraphicOverlay);
      } catch (IOException e) {
        Log.e(TAG, "Unable to start camera source.", e);
        mCameraSource.release();
        mCameraSource = null;
      }
    }
  }

  public static int incrementer()
  {
    count++;
    return(count);
  }
  public static int incrementer_1()
  {
    count1++;
    return(count1);
  }
  public static int get_incrementer()
  {
    return(count);
  }

  public void play_media()
  {
    stop_playing();
    mp = MediaPlayer.create(this, R.raw.rickroll);
    mp.start();
  }
  public void stop_playing()
  {
    if (mp != null) {
      mp.stop();
      mp.release();
      mp = null;
    }
  }

  public void alert_box()
  {
    runOnUiThread(new Runnable() {
      @Override
      public void run() {
        play_media();
        AlertDialog dig;
        dig = new AlertDialog.Builder(LivePreviewActivity.this)
                .setTitle("Phát hiện buồn ngủ !!!")
                .setMessage("Hãy dừng xe, vận động cơ thể để có thể tỉnh táo tiếp tục hành trình")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                  public void onClick(DialogInterface dialog, int which) {
                    stop_playing();
                    flag = 0;
                  }
                }).setIcon(R.drawable.danger)
                .show();
        startVibrate();
        dig.setOnDismissListener(new DialogInterface.OnDismissListener() {
          @Override
          public void onDismiss(DialogInterface dialog) {
            stop_playing();
            stopVibrate();
            flag = 0;
          }
        });
      }
    });


  }



  private class GraphicFaceTrackerFactory implements MultiProcessor.Factory<Face> {
    @Override
    public Tracker<Face> create(Face face) {
      return new GraphicFaceTracker(mGraphicOverlay);
    }
  }

  private class GraphicFaceTracker extends Tracker<Face> {
    private GraphicOverlay mOverlay;
    private FaceGraphic mFaceGraphic;

    GraphicFaceTracker(GraphicOverlay overlay) {
      mOverlay = overlay;
      mFaceGraphic = new FaceGraphic(overlay);
    }


    @Override
    public void onNewItem(int faceId, Face item) {
      mFaceGraphic.setId(faceId);
    }


    int state_i,state_f=-1;
    long start,end=System.currentTimeMillis();
    long begin,stop;
    int c;

    @Override
    public void onUpdate(FaceDetector.Detections<Face> detectionResults, Face face) {
      mOverlay.add(mFaceGraphic);
      mFaceGraphic.updateFace(face);
      if (flag == 0)
      {
        eye_tracking(face);
      }
    }

    @Override
    public void onMissing(FaceDetector.Detections<Face> detectionResults) {
      mOverlay.remove(mFaceGraphic);
      setText(tv_1,"Không phát hiện khuôn mặt");

    }

    @Override
    public void onDone() {
      mOverlay.remove(mFaceGraphic);
    }

    private void setText(final TextView text,final String value){
      runOnUiThread(new Runnable() {
        @Override
        public void run() {
          text.setText(value);
          if (value == "Face Missing"){
            text.setTextColor(Color.YELLOW);
          }
          if (value == "Sleepy"){
            text.setTextColor(Color.parseColor("#fcba03"));
          }
          if (value == "Drowsy"){
            text.setTextColor(Color.RED);
          }
        }
      });
    }

    private void eye_tracking(Face face)
    {
      float l = face.getIsLeftEyeOpenProbability();
      float r = face.getIsRightEyeOpenProbability();
      if(l<0.50 && r<0.50)
      {
        state_i = 0;
      }
      else
      {
        state_i = 1;
      }
      if(state_i != state_f)
      {
        start = System.currentTimeMillis();
        if(state_f==0)
        {
          c = incrementer_1();

        }
        end = start;
        stop = System.currentTimeMillis();
      }
      else if (state_i == 0 && state_f ==0 ) {
        begin = System.currentTimeMillis();
        if(begin - stop > s_time )
        {
          c = incrementer();
          alert_box();
          if(latLongs.size()>0){
            takeImage();
            detectedLocations.add(latLongs.get(latLongs.size()-1));
          }
          String timeDetect = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
          detectedTime.add(timeDetect);
          flag = 1;
        }
        begin = stop;
      }
      state_f = state_i;
      status();
    }
    public void status()
    {
      runOnUiThread(new Runnable() {
        @Override
        public void run() {
          int s = get_incrementer();
          if(s<5)
          {
            setText(tv_1,"Bình thường");
            tv_1.setTextColor(Color.GREEN);
            tv_1.setTypeface(Typeface.DEFAULT_BOLD);
          }
          if(s>4 )
          {
            setText(tv_1,"Mệt mỏi");
            tv_1.setTextColor(Color.YELLOW);
            tv_1.setTypeface(Typeface.DEFAULT_BOLD);
          }
          if(s>8)
          {
            setText(tv_1,"Buồn ngủ");
            tv_1.setTextColor(Color.RED);
            tv_1.setTypeface(Typeface.DEFAULT_BOLD);
          }


        }
      });

    }

  }
  @Override
  public void onBackPressed() {
//    Intent next = new Intent(LivePreviewActivity.this, MainActivity.class);
//    startActivity(next);
//    stopLocationService();
    exit();
  }


  private int imgCount = 0;
  ArrayList<File> imgList = new ArrayList<>();
  ArrayList<Uri> imgUri = new ArrayList<>();
  private void takeImage() {
    try {
      mCameraSource.takePicture(null, new CameraSource.PictureCallback() {

        @Override
        public void onPictureTaken(byte[] bytes) {
          imgCount++;
          ContextWrapper wrapper = new ContextWrapper(getApplicationContext());
          File file = wrapper.getDir("Images", MODE_PRIVATE);
          file = new File(file, "UniqueFileName"+imgCount+".jpg");
          try {

            // convert byte array into bitmap
            Bitmap loadedImage = null;
            Bitmap rotatedBitmap = null;
            loadedImage = BitmapFactory.decodeByteArray(bytes, 0,
                    bytes.length);

            // rotate Image
            Matrix rotateMatrix = new Matrix();
            rotatedBitmap = Bitmap.createBitmap(loadedImage, 0, 0,
                    loadedImage.getWidth(), loadedImage.getHeight(),
                    rotateMatrix, false);
            String state = Environment.getExternalStorageState();
            OutputStream stream = null;
            stream = new FileOutputStream(file);
            rotatedBitmap.compress(Bitmap.CompressFormat.JPEG,100,stream);
            String path = MediaStore.Images.Media.insertImage(getApplicationContext().getContentResolver(), rotatedBitmap, "Title"+imgCount, null);
            Log.d(TAG, "onPictureTaken: "+path);
            Uri uri = Uri.parse(path);
            imgUri.add(uri);
//            Log.d(TAG, "onPictureTaken: "+imgUri.get(imgUri.size()));
            stream.flush();
            stream.close();
            Toast.makeText(getApplicationContext(), "Captured!", Toast.LENGTH_LONG).show();
          } catch (Exception e) {
            Log.d(TAG, "Fail");
            e.printStackTrace();
          }
        }
      });

    } catch (Exception ex) {

    }
  }

  private Bitmap resize(Bitmap image, int maxWidth, int maxHeight) {
    if (maxHeight > 0 && maxWidth > 0) {
      int width = image.getWidth();
      int height = image.getHeight();
      float ratioBitmap = (float) width / (float) height;
      float ratioMax = (float) maxWidth / (float) maxHeight;

      int finalWidth = maxWidth;
      int finalHeight = maxHeight;
      if (ratioMax > 1) {
        finalWidth = (int) ((float) maxHeight * ratioBitmap);
      } else {
        finalHeight = (int) ((float) maxWidth / ratioBitmap);
      }
      image = Bitmap.createScaledBitmap(image, finalWidth, finalHeight, true);
      return image;
    } else {
      return image;
    }
  }

  public void startVibrate() {
    long[] pattern = { 300, 700 };
    vibrator = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);
    vibrator.vibrate(pattern, 0);
  }
  public void stopVibrate() {
    vibrator.cancel();
  }

  private boolean isLocationServiceRunning(){
    ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
    if(activityManager != null){
      for(ActivityManager.RunningServiceInfo service : activityManager.getRunningServices(Integer.MAX_VALUE)){
        if(MyLocationService.class.getName().equals(service.service.getClassName())){
          if(service.foreground){
            return true;
          }
        }
      }
      return false;
    }
    return false;
  }

  private void startLocationService(){
    if(!isLocationServiceRunning()){
      Intent intent = new Intent(getApplicationContext(), MyLocationService.class);
      intent.setAction("startLocationService");
      startService(intent);
//      Toast.makeText(LivePreviewActivity.this, "Location service started", Toast.LENGTH_SHORT).show();
    }
  }

  private void stopLocationService(){
    if(isLocationServiceRunning()){
      Intent intent = new Intent(getApplicationContext(), MyLocationService.class);
      intent.setAction("stopLocationService");
      startService(intent);
//      Toast.makeText(LivePreviewActivity.this, "Location service stopped", Toast.LENGTH_SHORT).show();
    }
  }

  private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
    @Override
    public void onReceive(Context context, Intent intent) {
      // Get extra data included in the Intent
      double latitude = intent.getDoubleExtra("lat", 0);
      double longitude = intent.getDoubleExtra("long", 0);
      latLongs.add(new LatLong(latitude, longitude));
    }
  };

  public void exit(){
    AlertDialog.Builder builder = new AlertDialog.Builder(this);
    builder.setMessage("Bạn có muốn kết thúc quá trình sử dụng?")
            .setCancelable(false)
            .setPositiveButton("Có", new DialogInterface.OnClickListener() {
              public void onClick(DialogInterface dialog, int id) {
                stopLocationService();
                try {
                  createHistory();
                } catch (IOException e) {
                  e.printStackTrace();
                }
//                LivePreviewActivity.this.finish();
              }
            })
            .setNegativeButton("Không", new DialogInterface.OnClickListener() {
              public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
              }
            });
    AlertDialog alert = builder.create();
    alert.show();
  }

  private void createHistory() throws IOException {
    geocoder = new Geocoder(this, Locale.getDefault());
    String startPoint = null;
    String endPoint = null;

    if(latLongs.size()>0) {
      start = geocoder.getFromLocation(latLongs.get(0).getLatitude(), latLongs.get(0).getLongitude(), 1);
      end = geocoder.getFromLocation(latLongs.get(latLongs.size() - 1).getLatitude(), latLongs.get(latLongs.size() - 1).getLongitude(), 1);
      startPoint = start.get(0).getAddressLine(0);
      endPoint = end.get(0).getAddressLine(0);
    }

    String date = new SimpleDateFormat("dd-MM-yyyy hh:mm aa", Locale.getDefault()).format(new Date());

    DatabaseReference lichtrinh = databaseReference.child("lichtrinh");
    String key = lichtrinh.push().getKey();
    String finalStartPoint = startPoint;
    String finalEndPoint = endPoint;
    lichtrinh.addListenerForSingleValueEvent(new ValueEventListener() {
      @Override
      public void onDataChange(@NonNull DataSnapshot snapshot) {
        Log.d(TAG, "onChildAdded: "+snapshot.getChildrenCount());
        lichtrinh.child(key).child("tieude").setValue("Lịch trình "+(snapshot.getChildrenCount()));
        lichtrinh.child(key).child("batdau").setValue(finalStartPoint);
        lichtrinh.child(key).child("ketthuc").setValue(finalEndPoint);
        lichtrinh.child(key).child("thoigian").setValue(clock.getText());
        lichtrinh.child(key).child("ngaythang").setValue(date);
      }

      @Override
      public void onCancelled(@NonNull DatabaseError error) {

      }
    });


    storage = FirebaseStorage.getInstance();
    storageReference = storage.getReference();
    DatabaseReference chitietlichtrinh = lichtrinh.child(key).child("chitietlichtrinh");
    chitietlichtrinh.child("toado").setValue(latLongs);
    if (detectedLocations != null){
        uploadImageToFirebaseStorage(imgUri, storageReference, chitietlichtrinh);
    }

//      for (int i = 0; i < imgUri.size(); i++) {
//        stoRef.putFile(imgUri.get(i)).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//          @Override
//          public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//            stoRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
//              @Override
//              public void onSuccess(Uri uri) {
//                chitietlichtrinh.child("imageURI").setValue(uri);
//              }
//            });
//            Toast
//                    .makeText(LivePreviewActivity.this,
//                            "Image Uploaded!!",
//                            Toast.LENGTH_SHORT)
//                    .show();
//          }
//        }).addOnFailureListener(new OnFailureListener() {
//          @Override
//          public void onFailure(@NonNull Exception e) {
//            Toast
//                    .makeText(LivePreviewActivity.this,
//                            "Failed " + e.getMessage(),
//                            Toast.LENGTH_SHORT)
//                    .show();
//          }
//        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
//          @Override
//          public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
//
//          }
//        });
    Log.d(TAG, "createHistory: "+imgUri.size());


//    Log.d(TAG, "createHistory: "+key);
  }

  private void startClock(){
    Thread t = new Thread() {

      @Override
      public void run() {
        try {
          while (!isInterrupted()) {
            Thread.sleep(1000);
            runOnUiThread(new Runnable() {
              @Override
              public void run() {
                int hours = seconds / 3600;
                int minutes = (seconds % 3600) / 60;
                int secs = seconds % 60;

                // Format the seconds into hours, minutes,
                // and seconds.
                time = String
                        .format(Locale.getDefault(),
                                "%d:%02d:%02d", hours,
                                minutes, secs);

                // Set the text view text.
                clock.setText(time);
                seconds++;
              }
            });
          }
        } catch (InterruptedException e) {
        }
      }
    };

    t.start();
  }

  private void uploadImageToFirebaseStorage(ArrayList<Uri> imageUriList, StorageReference storageReference, DatabaseReference databaseReference) {
    if (imageUriList.size() > 0) {
      mProgressBar.setVisibility(View.VISIBLE);
      loadTV.setVisibility(View.VISIBLE);
      getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
              WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
      Uri imageUri = imageUriList.get(0);
      StorageReference ref = storageReference.child("images/" + UUID.randomUUID().toString());
      ref.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
        @Override
        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
          ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
              Uri downloadUrl = uri;
              Log.d(TAG, "onSuccess: " + downloadUrl);
              String ltKey = databaseReference.push().getKey();
              databaseReference.child(ltKey).child("diadiemphathien").setValue(detectedLocations.get(counter));
              databaseReference.child(ltKey).child("taithoidiem").setValue(detectedTime.get(counter));
              databaseReference.child(ltKey).child("hinhanhUrl").setValue(downloadUrl.toString());
              //Do what you want with the url
            }
          });
        }
      }).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
        @Override
        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
          if (task.isSuccessful()) {
            imageUriList.remove(0);
            counter++;
            uploadImageToFirebaseStorage(imageUriList, storageReference, databaseReference); //Call when completes
          }
        }
      });
//      UploadTask uploadTask = storageReference.child(UUID.randomUUID().toString()).putFile(imageUri);
//      uploadTask.continueWithTask(new Continuation() {
//        @Override
//        public Object then(@NonNull Task task) throws Exception {
//          if(!task.isSuccessful()){
//            throw task.getException();
//          }
//          return storageReference.getDownloadUrl();
//        }
//      }).addOnCompleteListener(new OnCompleteListener<Uri>() {
//        @Override
//        public void onComplete(@NonNull Task<Uri> task) {
//          if (task.isSuccessful()) {
//            imageUriList.remove(0);
////            Log.d(TAG, "onComplete: "+task.getResult());
//            uploadImageToFirebaseStorage(imageUriList, storageReference, databaseReference); //Call when completes
////            }
//          }
//        }
//      });
    }
    else {
      mProgressBar.setVisibility(View.GONE);
      loadTV.setVisibility(View.GONE);
      LivePreviewActivity.this.finish();
    }
  }
}



