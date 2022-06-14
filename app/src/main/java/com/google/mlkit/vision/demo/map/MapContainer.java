package com.google.mlkit.vision.demo.map;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.android.SphericalUtil;
import com.google.mlkit.vision.demo.R;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MapContainer extends AppCompatActivity implements OnMapReadyCallback{

    private GoogleMap mMap;
    ImageView img;
    EditText editText;
    TextView start, end, time;
    ImageView editIcon;
    LocationRequest mLocationRequest;
    LatLng TamWorth = new LatLng(20.8281, 106.7134);
    LatLng NewCastle = new LatLng(20.7839, 106.7230);
    LatLng Brisbane = new LatLng(20.7765, 106.7294);

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    ProgressBar progressBar;
    private String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        Objects.requireNonNull(getSupportActionBar()).hide();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_container);

        progressBar = findViewById(R.id.progressBar);
        editText = findViewById(R.id.title);
        start = findViewById(R.id.start);
        end = findViewById(R.id.end);
        time = findViewById(R.id.time);
        progressBar.setVisibility(View.VISIBLE);
        img = findViewById(R.id.icon);
        editIcon = findViewById(R.id.editIcon);
        SlidingUpPanelLayout layout = findViewById(R.id.slidingUp);
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
        editIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!editText.isEnabled()){
                    editText.setEnabled(true);
                    editText.requestFocus();
                    editText.setSelection(editText.getText().length());
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(editText, 0);
                }
            }
        });
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    /* Write your logic here that will be executed when user taps next button */
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    editText.setEnabled(false);

                    handled = true;
                }
                return handled;
            }
        });
        layout.addPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {

            }

            @Override
            public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {
                if (newState == SlidingUpPanelLayout.PanelState.EXPANDED) {
                    img.setImageResource(R.drawable.expanded);
                } else if (newState == SlidingUpPanelLayout.PanelState.COLLAPSED) {
                    img.setImageResource(R.drawable.collapse);
                }
            }
        });
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);

    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if ( v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int)ev.getRawX(), (int)ev.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    editText.setEnabled(false);
                }
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(2000); // two minute interval
        mLocationRequest.setFastestInterval(2000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        double result = 0.0;
        result = SphericalUtil.computeDistanceBetween(Brisbane, NewCastle);
        Intent intent = getIntent();
        String key  = "";
        if(intent.hasExtra("key")&&intent.hasExtra("title")&&intent.hasExtra("start")&&intent.hasExtra("end")&&intent.hasExtra("time")){
            key = intent.getStringExtra("key");
            editText.setText(intent.getStringExtra("title"));
            start.setText("Bắt đầu tại: "+intent.getStringExtra("start"));
            end.setText("Kết thúc tại: "+intent.getStringExtra("end"));
            time.setText("Tổng thời gian đã đi: "+intent.getStringExtra("time"));
        }
        else
            Toast.makeText(this, "There's no data at all", Toast.LENGTH_LONG).show();

        //EditText change
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        String finalKey = key;
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                Log.d("TestUI", "onTextChanged: "+charSequence+", "+i+", "+i1+", "+i2);
            }

            @Override
            public void afterTextChanged(Editable editable) {
                Log.d("TestUI", "Text has changed "+editable.toString());
                ref.child("user").child("lichtrinh").child(finalKey).child("tieude").setValue(editable.toString());
            }
        });

        SharedPreferences sharedPreferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
        id = sharedPreferences.getString("id", null);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("user").child(id);
        Query query = databaseReference.child("lichtrinh").child(key).child("chitietlichtrinh").child("toado");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                progressBar.setVisibility(View.GONE);
                List<LatLng> cities = new ArrayList<>();
                if (snapshot.exists()) {
                    for(DataSnapshot element : snapshot.getChildren()) {
                        LatLng data = new LatLng(element.getValue(LatLong.class).getLatitude(), element.getValue(LatLong.class).getLongitude());
                        cities.add(data);
                    }
                    PolylineOptions routeDraw = new PolylineOptions().width(8).color(Color.BLUE);
                    for(LatLng latLng : cities){
                        Log.d("aaaa", "onDataChange: "+ latLng);
                        routeDraw.add(latLng);
                    }
                    mMap.addPolyline(routeDraw);
                    mMap.addMarker(new MarkerOptions().position(cities.get(0)).title("Bắt đầu"));
                    mMap.addMarker(new MarkerOptions().position(cities.get(cities.size()-1)).title("Kết thúc"));
//                    drawCircle(cities.get(0));
//                    drawCircle(cities.get(cities.size()-1));
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(cities.get(0), 13));

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
    }

    private void drawCircle(LatLng point){

        // Instantiating CircleOptions to draw a circle around the marker
        CircleOptions circleOptions = new CircleOptions();

        // Specifying the center of the circle
        circleOptions.center(point);

        // Radius of the circle
        circleOptions.radius(50);

        // Border color of the circle
        circleOptions.strokeColor(Color.WHITE);

        // Fill color of the circle
        circleOptions.fillColor(Color.BLUE);

        // Border width of the circle
        circleOptions.strokeWidth(15);


        // Adding the circle to the GoogleMap
        mMap.addCircle(circleOptions);

    }

}