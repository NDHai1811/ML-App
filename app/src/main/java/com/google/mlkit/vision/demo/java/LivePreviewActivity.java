/*
 * Copyright 2020 Google LLC. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.mlkit.vision.demo.java;


import android.Manifest;
import android.app.ActivityManager;
import android.app.Dialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityCompat.OnRequestPermissionsResultCallback;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.common.annotation.KeepName;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.vision.face.Face;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.mlkit.vision.demo.AlertCalculate;
import com.google.mlkit.vision.demo.CameraSource;
import com.google.mlkit.vision.demo.CameraSourcePreview;
import com.google.mlkit.vision.demo.GraphicOverlay;
import com.google.mlkit.vision.demo.R;
import com.google.mlkit.vision.demo.ViewDialog;
import com.google.mlkit.vision.demo.history.History;
import com.google.mlkit.vision.demo.java.facedetector.FaceDetectorProcessor;
import com.google.mlkit.vision.demo.map.Constants;
import com.google.mlkit.vision.demo.map.MyLocationService;
import com.google.mlkit.vision.demo.preference.PreferenceUtils;
import com.google.mlkit.vision.demo.preference.SettingsActivity;
import com.google.mlkit.vision.demo.realtime_data.DataInfo;
import com.google.mlkit.vision.face.FaceDetectorOptions;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

/** Live preview demo for ML Kit APIs. */
@KeepName
public final class LivePreviewActivity extends AppCompatActivity
    implements OnRequestPermissionsResultCallback,
        OnItemSelectedListener,
        CompoundButton.OnCheckedChangeListener{
  private static final String FACE_DETECTION = "Face Detection";

  private static final String TAG = "LivePreviewActivity";
  private static final int PERMISSION_REQUESTS = 1;

  private CameraSource cameraSource = null;
  private CameraSourcePreview preview;
  private GraphicOverlay graphicOverlay;
  private String selectedModel = FACE_DETECTION;

  Handler handler = new Handler();
  Runnable runnable;
  int delay = 10;
  FragmentManager fm;
  AlertCalculate alertCalculate;

  private RecyclerView cities;
  private RecyclerView.Adapter adapter;
  ArrayList<DataInfo> list = new ArrayList<>();
  ArrayList<DataInfo> cites;
  private Button button;

  SwitchMaterial mySwitch;
  boolean isOut, faceChecker;
  double data;
  float left, right;
  AlertCalculate check = new AlertCalculate(this, this);
  int timeout;
  float value;

  private int state = 0;

  private int seconds = 0;
  private boolean running;
  private boolean wasRunning;
  private final Handler handler2 = new Handler();
  public int delay2=10000;
  private int blinkTime;
  public boolean isWake=true;
  public float eyesWidth;
  public ViewDialog viewDialog;
  private int countAwake;
  String info;
  String time;

  String startDestination, endDestination, totalTime, sleepyCount;
  private int counter=0;
  ArrayList<History> savedList =  new ArrayList<>();

  int timeUsed=0;
  ConstraintLayout llCameraView;

  @Override
  protected void onCreate(Bundle savedInstanceState) {

    //hide tool bar and show in full screen
    requestWindowFeature(Window.FEATURE_NO_TITLE);
    Objects.requireNonNull(getSupportActionBar()).hide();
//    getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//            WindowManager.LayoutParams.FLAG_FULLSCREEN);
    View decorView = getWindow().getDecorView();
    decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    getWindow().setStatusBarColor(Color.TRANSPARENT);

    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_vision_live_preview);
    loadData();
    delay2 = PreferenceUtils.getDuration(this);
    eyesWidth = PreferenceUtils.getEyeRatio(this);
    Log.d(TAG, "onResume: "+eyesWidth+" duration "+delay2);
    llCameraView = (ConstraintLayout) findViewById(R.id.llCameraView);

    viewDialog = new ViewDialog(this);



    running = true;
    if (savedInstanceState != null) {

      // Get the previous state of the stopwatch
      // if the activity has been
      // destroyed and recreated.
      seconds
              = savedInstanceState
              .getInt("seconds");
      running
              = savedInstanceState
              .getBoolean("running");
      wasRunning
              = savedInstanceState
              .getBoolean("wasRunning");
    }
    runTimer();

    preview = findViewById(R.id.preview_view);
    if (preview == null) {
      Log.d(TAG, "Preview is null");
    }
    graphicOverlay = findViewById(R.id.graphic_overlay);
    if (graphicOverlay == null) {
      Log.d(TAG, "graphicOverlay is null");
    }

    //A trick to display alert=))
    mySwitch = findViewById(R.id.switch1);
    Dialog dialog = new Dialog(LivePreviewActivity.this);
    isOut = check.isLoseAttention();
    mySwitch.setOnCheckedChangeListener((compoundButton, b) -> {
      if (mySwitch.isChecked()){
        imageCapture();
        if(!dialog.isShowing()) {

          if(isWake){
            viewDialog.loseAttention(dialog);
          }
          else{
            viewDialog.sleepyAlert(dialog);
            counter++;
          }

        }
      }
    });


    fm = getSupportFragmentManager();//A fragment manager to control map fragment
    alertCalculate = new AlertCalculate(this, this);//Declare a class AlertCalculate to use its method

// exit yea for sure
    Button exitBtn = findViewById(R.id.exitBtn);
    exitBtn.setOnClickListener(view -> {
//      exit();
      getLocation();
    });



    //open setting

    ImageView settingsButton = findViewById(R.id.settings_button);
    settingsButton.setOnClickListener(
        v -> {
          Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
          intent.putExtra(
              SettingsActivity.EXTRA_LAUNCH_SOURCE, SettingsActivity.LaunchSource.LIVE_PREVIEW);
          startActivity(intent);

        });
//check permission - if granted, do detect
    if (allPermissionsGranted()) {
      createCameraSource(selectedModel);

    } else {
      getRuntimePermissions();
    }

    if(ContextCompat.checkSelfPermission(
            getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION
    ) != PackageManager.PERMISSION_GRANTED){
      ActivityCompat.requestPermissions(
              LivePreviewActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUESTS
      );
    }else{
      startLocationService();
    }
  }

  @Override
  public synchronized void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
    // An item was selected. You can retrieve the selected item using
    // parent.getItemAtPosition(pos)
    selectedModel = parent.getItemAtPosition(pos).toString();
    Log.d(TAG, "Selected model: " + selectedModel);
    preview.stop();
    if (allPermissionsGranted()) {
      createCameraSource(selectedModel);
      startCameraSource();
    } else {
      getRuntimePermissions();
    }
  }

  @Override
  public void onNothingSelected(AdapterView<?> parent) {
    // Do nothing.
  }

  @Override
  public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
    Log.d(TAG, "Set facing");
    if (cameraSource != null) {
      if (isChecked) {
        cameraSource.setFacing(CameraSource.CAMERA_FACING_FRONT);
      } else {
        cameraSource.setFacing(CameraSource.CAMERA_FACING_BACK);
      }
    }
    preview.stop();
    startCameraSource();
  }

  private void createCameraSource(String model) {
    // If there's no existing cameraSource, create one.
    if (cameraSource == null) {
      cameraSource = new CameraSource(this, graphicOverlay);
    }

    try {
      if (FACE_DETECTION.equals(model)) {
        Log.i(TAG, "Using Face Detector Processor");
        FaceDetectorOptions faceDetectorOptions =
                PreferenceUtils.getFaceDetectorOptionsForLivePreview(this);
        FaceDetectorProcessor processor = new FaceDetectorProcessor(this, faceDetectorOptions);
        cameraSource.setMachineLearningFrameProcessor(processor);
        handler.postDelayed(runnable = () -> {
          handler.postDelayed(runnable, delay);
          getData(processor);
        }, delay);
      } else {
        Log.e(TAG, "Unknown model: " + model);
      }
    } catch (RuntimeException e) {
      Log.e(TAG, "Can not create image processor: " + model, e);
      Toast.makeText(
              getApplicationContext(),
              "Can not create image processor: " + e.getMessage(),
              Toast.LENGTH_LONG)
          .show();
    }
  }

  /**
   * Starts or restarts the camera source, if it exists. If the camera source doesn't exist yet
   * (e.g., because onResume was called before the camera source was created), this will be called
   * again when the camera source is created.
   */
  private void startCameraSource() {
    if (cameraSource != null) {
      try {
        if (preview == null) {
          Log.d(TAG, "resume: Preview is null");
        }
        if (graphicOverlay == null) {
          Log.d(TAG, "resume: graphOverlay is null");
        }
        preview.start(cameraSource, graphicOverlay);
      } catch (IOException e) {
        Log.e(TAG, "Unable to start camera source.", e);
        cameraSource.release();
        cameraSource = null;
      }
    }
  }

  @Override
  public void onResume() {
    super.onResume();
    Log.d(TAG, "onResume");
    delay2 = PreferenceUtils.getDuration(this);
    eyesWidth = PreferenceUtils.getEyeRatio(this);
    Log.d(TAG, "onResume: "+eyesWidth/1.2+" duration "+delay2);
    if (wasRunning) {
      running = true;
    }
    createCameraSource(selectedModel);
    startCameraSource();
  }


//  Get data from FaceProcessor and use it
  public void getData(FaceDetectorProcessor processor) {
    data = processor.look;
    left = processor.left;
    right = processor.right;
    faceChecker = processor.faceCheck;
//    Toast.makeText(this, "Here is data"+lOrR, Toast.LENGTH_SHORT).show();
//    Log.v("ddd", "OK " + faceChecker);
    Log.v("ddd", "eye " + value);
    sleepDetection();


    //get time calculated when lose attention
    timeout = check.checkHead(processor.eulerX, processor.eulerY, processor.eulerZ);

    mySwitch.setChecked(check.count > 500);

  }
  int blinkCountMs = 0;
//check eyes are blinking or not and calculate time of this
  public void sleepDetection(){
    if ((left == Face.UNCOMPUTED_PROBABILITY) ||
            (right == Face.UNCOMPUTED_PROBABILITY)) {
      // One of the eyes was not detected.
      return;
    }
    float OPEN_THRESHOLD = (float) 0.85;
    float CLOSE_THRESHOLD = (float) 0.05;
    switch (state) {
      case 0:
        if ((left > OPEN_THRESHOLD) && (right > OPEN_THRESHOLD)) {
          // Both eyes are initially open
          Log.d("BlinkTracker", "Both eyes are initially open");
          state = 1;
        }
        break;

      case 1:
        if ((left < CLOSE_THRESHOLD) && (right < CLOSE_THRESHOLD)) {
          // Both eyes become closed
          Log.d("BlinkTracker", "Both eyes become closed");
          state = 2;
        }
        break;

      case 2:
        if ((left > OPEN_THRESHOLD) && (right > OPEN_THRESHOLD)) {
          // Both eyes are open again
          Log.d("BlinkTracker", "Both eyes are open again");
          state = 0;
        }
        break;
    }

    value = Math.min(left, right);
    Log.d(TAG, "sleepDetection: "+value);

    if(value<(eyesWidth/1.5)){
      blinkCountMs++;
//      Log.d("TAG4", "sleepDetection: "+blinkCountMs);
    }
    else if(value>(eyesWidth/1.5)){
      blinkCountMs=0;
      isWake = true;
      countAwake++;
//      Log.d("TAG4", "Awaken: "+countAwake);
    }

    if (blinkCountMs>200){
      blinkCountMs=0;
      blinkTime++;
      countAwake=0;
      Log.d("TAG4", "blink times: "+blinkTime);
    }
    if (blinkTime>5){
      isWake=false;
      mySwitch.setChecked(true);
      blinkTime=0;
    }

    if (countAwake>2500){
      resetValue();
    }
    Log.d("TAG4", "sleepDetection: "+isWake);
  }

  public boolean getHeight() {
    return this.isWake;
  }
  private void resetValue() {
    blinkCountMs=0;
    blinkTime=0;
    isWake=true;
    countAwake=0;
  }

  /** Stops the camera. */
  @Override
  protected void onPause() {
    super.onPause();
    Log.d(TAG, "onPause");
    wasRunning = running;
    running = false;
    preview.stop();
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    if (cameraSource != null) {
      cameraSource.release();
    }
    stopLocationService();
    Log.d(TAG, "onDestroy");
    handler.removeCallbacks(runnable);
  }

  private String[] getRequiredPermissions() {
    try {
      PackageInfo info =
          this.getPackageManager()
              .getPackageInfo(this.getPackageName(), PackageManager.GET_PERMISSIONS);
      String[] ps = info.requestedPermissions;
      if (ps != null && ps.length > 0) {
        return ps;
      } else {
        return new String[0];
      }
    } catch (Exception e) {
      return new String[0];
    }
  }

  private boolean allPermissionsGranted() {
    for (String permission : getRequiredPermissions()) {
      if (isPermissionGranted(this, permission)) {
        return false;
      }
    }
    return true;
  }

  private void getRuntimePermissions() {
    List<String> allNeededPermissions = new ArrayList<>();
    for (String permission : getRequiredPermissions()) {
      if (isPermissionGranted(this, permission)) {
        allNeededPermissions.add(permission);
      }
    }

    if (!allNeededPermissions.isEmpty()) {
      ActivityCompat.requestPermissions(
          this, allNeededPermissions.toArray(new String[0]), PERMISSION_REQUESTS);
    }
  }

  @Override
  public void onRequestPermissionsResult(
          int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    Log.i(TAG, "Permission granted!");
    if (allPermissionsGranted()) {
      createCameraSource(selectedModel);
    }
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
  }

  private static boolean isPermissionGranted(Context context, String permission) {
    if (ContextCompat.checkSelfPermission(context, permission)
        == PackageManager.PERMISSION_GRANTED) {
      Log.i(TAG, "Permission granted: " + permission);
      return false;
    }
    Log.i(TAG, "Permission NOT granted: " + permission);
    return true;
  }




  public String dataAddress = "Chưa xác định";
  public void dataFromFm(double lat, double lng, String address, String city,String zip, String state, String country) {

    Log.d("Address", lat+" "+lng+" "+address+" "+city+" "+zip+" "+state+" "+country);


  }

  private void runTimer()
  {

    // Get the text view.
    final TextView timeView
            = findViewById(
            R.id.countdown_text);

    // Creates a new Handler
    final Handler handler
            = new Handler();

    // Call the post() method,
    // passing in a new Runnable.
    // The post() method processes
    // code without a delay,
    // so the code in the Runnable
    // will run almost immediately.
    handler.post(new Runnable() {
      @Override

      public void run()
      {
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
        timeView.setText(time);

        // If running is true, increment the
        // seconds variable.
        if (running) {
          seconds++;
        }

        // Post the code again
        // with a delay of 1 second.
        handler.postDelayed(this, 1000);
      }
    });
  }

  @Override
  protected void onSaveInstanceState(@NonNull Bundle outState) {
    super.onSaveInstanceState(outState);
    outState
            .putInt("seconds", seconds);
    outState
            .putBoolean("running", running);
    outState
            .putBoolean("wasRunning", wasRunning);
  }

  @Override
  public void onBackPressed() {
    exit();
  }

  private ArrayList<DataInfo> readCSVData() {
    //    list.add(new DataInfo("Bắt đầu tại"+dataAddress, "Thời điểm"+datetime1, ""));
    return list;
  }

  public void exit(){
    new AlertDialog.Builder(LivePreviewActivity.this)
      .setIcon(android.R.drawable.ic_dialog_alert)
      .setTitle("Quiting App?")
      .setMessage("Are you sure to exit?")
      .setPositiveButton(R.string.yes, (dialog, which) -> {
        //Stop the activity
        endDestination=info;
        totalTime = time;
        sleepyCount= String.valueOf(counter);
        savedList.add(new History("Điểm đầu: "+startDestination, "Điểm cuối: "+endDestination, "Tổng thời gian đã đi: "+totalTime, "Số lần phát hiện buồn ngủ: "+sleepyCount+" lần"));
        saveData();
        Log.d("TAG", "exit: "+startDestination+endDestination+totalTime+sleepyCount);
        cameraSource.release();
        stopLocationService();
        finish();
      })
      .setNegativeButton(R.string.no, null)
      .show();
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
    String json = gson.toJson(savedList);

    // below line is to save data in shared
    // prefs in the form of string.
    editor.putString("courses", json);

    timeUsed++;
    editor.putInt("counter", timeUsed);
    // below line is to apply changes
    // and save data in shared prefs.
    editor.apply();
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
    savedList = gson.fromJson(json, type);

    timeUsed = sharedPreferences.getInt("counter", 0);

    // checking below if the array list is empty or not
    if (savedList == null) {
      // if the array list is empty
      // creating a new array list.
      savedList = new ArrayList<>();
    }
  }

  private void imageCapture(){
    ContextWrapper wrapper = new ContextWrapper(getApplicationContext());
    File file = wrapper.getDir("Images", MODE_PRIVATE);
    file = new File(file, "UniqueFileName"+".jpg");


    llCameraView.buildDrawingCache();
    Bitmap captureView;
    llCameraView.setDrawingCacheEnabled(true);
    captureView = Bitmap.createBitmap(llCameraView.getDrawingCache());
    llCameraView.setDrawingCacheEnabled(false);


    try{
      OutputStream stream = null;
      stream = new FileOutputStream(file);
      captureView.compress(Bitmap.CompressFormat.JPEG,100,stream);
      stream.flush();
      stream.close();
      Toast.makeText(getApplicationContext(), "Captured!", Toast.LENGTH_LONG).show();
    }catch (IOException e) // Catch the exception
    {
      e.printStackTrace();
      Toast.makeText(getApplicationContext(), "Failed!", Toast.LENGTH_LONG).show();
    }
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
      Toast.makeText(LivePreviewActivity.this, "Location service started", Toast.LENGTH_SHORT).show();
    }
  }

  private void stopLocationService(){
    if(isLocationServiceRunning()){
      Intent intent = new Intent(getApplicationContext(), MyLocationService.class);
      intent.setAction("stopLocationService");
      startService(intent);
      Toast.makeText(LivePreviewActivity.this, "Location service stopped", Toast.LENGTH_SHORT).show();
    }
  }
  String locate;
  public void getLocation(){
    final LocationCallback mLocationCallback = new LocationCallback() {
      @Override
      public void onLocationResult(LocationResult locationResult) {
        List<Location> locationList = locationResult.getLocations();
        if (locationList.size() > 0) {
          //The last location in the list is the newest
          Location location = locationList.get(locationList.size() - 1);
          Log.i("MapsActivity", "Location: " + location.getLatitude() + " " + location.getLongitude());

          //Place current location marker
          LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());


          try {
            Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
            if (addresses.size() > 0) {
              Address address = addresses.get(0);
              locate = address.getAddressLine(0);
//                            sb.append(address.getAddressLine(0));
            }
            Toast.makeText(LivePreviewActivity.this, ""+locate, Toast.LENGTH_SHORT).show();

          } catch (IOException e) {
            e.printStackTrace();
          }
        }

      }
    };
  }
}
