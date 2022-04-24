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


import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.collection.ArraySet;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityCompat.OnRequestPermissionsResultCallback;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.common.annotation.KeepName;
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
import com.google.mlkit.vision.demo.map.MapsFragment;
import com.google.mlkit.vision.demo.preference.PreferenceUtils;
import com.google.mlkit.vision.demo.preference.SettingsActivity;
import com.google.mlkit.vision.demo.realtime_data.DataInfo;
import com.google.mlkit.vision.demo.realtime_data.DataInfo_Adapter;
import com.google.mlkit.vision.demo.ui.TestUi;
import com.google.mlkit.vision.face.FaceDetectorOptions;

import java.io.IOException;
import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;

/** Live preview demo for ML Kit APIs. */
@KeepName
public final class LivePreviewActivity extends AppCompatActivity
    implements OnRequestPermissionsResultCallback,
        OnItemSelectedListener,
        CompoundButton.OnCheckedChangeListener, MapsFragment.onSomeEventListener {
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
  boolean isOut;
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
  private Runnable runnable2;
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

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    //hide tool bar and show in full screen
    requestWindowFeature(Window.FEATURE_NO_TITLE);
    Objects.requireNonNull(getSupportActionBar()).hide();
    getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN);

    setContentView(R.layout.activity_vision_live_preview);

    loadData();
    delay2 = PreferenceUtils.getDuration(this);
    eyesWidth = PreferenceUtils.getEyeRatio(this);
    Log.d(TAG, "onResume: "+eyesWidth+" duration "+delay2);

    viewDialog = new ViewDialog(this);

    cites = readCSVData();

    this.cities = findViewById(R.id.rtInfo);
    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
    this.cities.setLayoutManager(mLayoutManager);

    adapter = new DataInfo_Adapter(cites);
    this.cities.setAdapter(adapter);
    button = findViewById(R.id.button);
    button.setOnClickListener(view -> {
      Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT+7:00"));
      Date currentLocalTime = cal.getTime();
      DateFormat date = new SimpleDateFormat("HH:mm a", new Locale("vi", "VN"));
      date.setTimeZone(TimeZone.getTimeZone("GMT+7:00"));

      String localTime = date.format(currentLocalTime);
      info = dataAddress.replace(", Vietnam", "");
      list.add(0, new DataInfo("Bạn đang ở vị trí: "+info, ""+localTime, "Phát hiện buồn ngủ: "+ (isWake?"Không":"Có")));
      adapter.notifyItemInserted(0);
      cities.smoothScrollToPosition(0);
      if (startDestination==null){
        startDestination = info;
      }
    });


    handler2.postDelayed(runnable2 = () -> {
      handler2.postDelayed(runnable2, delay2);
      button.performClick();
      Log.d("Handler", "run: "+delay2);
    }, delay2);

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
    exitBtn.setOnClickListener(view -> exit());

    ToggleButton facingSwitch = findViewById(R.id.facing_switch);

//    create map fragment
    FragmentTransaction ft_add = fm.beginTransaction();

    int orientation = getResources().getConfiguration().orientation;
    if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
      ft_add.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right);

    } else {
      ft_add.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_left);
    }

    ft_add.add(R.id.frame_layout, new MapsFragment(), "fragment1");
    ft_add.commit();

    Fragment fragment = fm.findFragmentById(R.id.frame_layout);
    FragmentTransaction ft_remo = fm.beginTransaction();
    if (fragment!=null){
      ft_remo.hide(fragment);
    }
    ft_remo.commit();

    //turn on/off map fragment
    facingSwitch.setOnCheckedChangeListener((compoundButton, b) -> {
      Fragment fragment1 = fm.findFragmentById(R.id.frame_layout);
      FragmentTransaction ft_remo1 = fm.beginTransaction();
      if (b) {
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
          ft_remo1.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_left);
        } else {
          ft_remo1.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right);
        }
        assert fragment1 != null;
        ft_remo1.hide(fragment1);
      } else {
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
          ft_remo1.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right);

        } else {
          ft_remo1.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_left);
        }
        assert fragment1 != null;
        ft_remo1.show(fragment1);
      }
      ft_remo1.commit();
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
//    Toast.makeText(this, "Here is data"+lOrR, Toast.LENGTH_SHORT).show();
    Log.v("ddd", "OK " + data);
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
    Log.d(TAG, "onDestroy");
    handler.removeCallbacks(runnable);
    handler2.removeCallbacks(runnable2);
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

  @Override
  public void someEvent(String s, String speed) {
    if (s !=null){
      dataAddress = s;
    }

    else dataAddress = "Chưa xác định";
//    speed_stats.setText("Speed: "+speed);
    Log.d("Data", "data from fragment "+s);
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
}
