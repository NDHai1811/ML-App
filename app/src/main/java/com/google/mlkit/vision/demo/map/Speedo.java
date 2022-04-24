//package com.google.mlkit.vision.demo.map;
//
//import android.Manifest;
//import android.app.Activity;
//import android.content.Context;
//import android.content.pm.PackageManager;
//import android.location.Location;
//import android.location.LocationManager;
//import android.os.Bundle;
//import android.widget.CheckBox;
//import android.widget.CompoundButton;
//import android.widget.TextView;
//
//import androidx.core.app.ActivityCompat;
//
//import com.google.mlkit.vision.demo.R;
//
//import java.util.Formatter;
//import java.util.Locale;
//
//public class Speedo extends Activity implements IBaseGpsListener {
//
//    public String getSpeed_info() {
//        return speed_info;
//    }
//
//    public void setSpeed_info(String speed_info) {
//        this.speed_info = speed_info;
//    }
//
//    private String speed_info;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.speed_layout);
//        LocationManager locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            // TODO: Consider calling
//            //    ActivityCompat#requestPermissions
//            // here to request the missing permissions, and then overriding
//            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//            //                                          int[] grantResults)
//            // to handle the case where the user grants the permission. See the documentation
//            // for ActivityCompat#requestPermissions for more details.
//            return;
//        }
//        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
//        this.updateSpeed(null);
//
//        CheckBox chkUseMetricUntis = (CheckBox) this.findViewById(R.id.chkMetricUnits);
//        chkUseMetricUntis.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                // TODO Auto-generated method stub
//                Speedo.this.updateSpeed(null);
//            }
//        });
//    }
//
//    public void finish()
//    {
//        super.finish();
//        System.exit(0);
//    }
//
//    private void updateSpeed(CLocation location) {
//        // TODO Auto-generated method stub
//        float nCurrentSpeed = 0;
//
//        if(location != null)
//        {
//            location.setUseMetricunits(this.useMetricUnits());
//            nCurrentSpeed = location.getSpeed();
//        }
//
//        Formatter fmt = new Formatter(new StringBuilder());
//        fmt.format(Locale.US, "%5.1f", nCurrentSpeed);
//        String strCurrentSpeed = fmt.toString();
//
//        strCurrentSpeed = strCurrentSpeed.replace(' ', '0');
//        setSpeed_info(strCurrentSpeed);
//
//        String strUnits = "miles/hour";
//        if(this.useMetricUnits())
//        {
//            strUnits = "meters/second";
//        }
//
//        TextView txtCurrentSpeed = (TextView) this.findViewById(R.id.txtCurrentSpeed);
//        txtCurrentSpeed.setText(strCurrentSpeed + " " + strUnits);
//    }
//
//    private boolean useMetricUnits() {
//        // TODO Auto-generated method stub
//        CheckBox chkUseMetricUnits = (CheckBox) this.findViewById(R.id.chkMetricUnits);
//        return chkUseMetricUnits.isChecked();
//    }
//
//    @Override
//    public void onLocationChanged(Location location) {
//        // TODO Auto-generated method stub
//        if(location != null)
//        {
//            CLocation myLocation = new CLocation(location, this.useMetricUnits());
//            this.updateSpeed(myLocation);
//        }
//    }
//
//    @Override
//    public void onProviderDisabled(String provider) {
//        // TODO Auto-generated method stub
//
//    }
//
//    @Override
//    public void onProviderEnabled(String provider) {
//        // TODO Auto-generated method stub
//
//    }
//
//    @Override
//    public void onStatusChanged(String provider, int status, Bundle extras) {
//        // TODO Auto-generated method stub
//
//    }
//
//    @Override
//    public void onGpsStatusChanged(int event) {
//        // TODO Auto-generated method stub
//
//    }
//
//
//
//}
