package com.google.mlkit.vision.demo.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.mlkit.vision.demo.R;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    public int count=0;
    public int your_pos=0;
    BottomNavigationView navigation;
    List<Integer> state = new ArrayList<>();
    boolean pressed=false;
    String id;
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();

        setContentView(R.layout.activity_main);
        SharedPreferences sharedPreferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        databaseReference = firebaseDatabase.getReference("user");

        FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser() ;
        if(currentFirebaseUser!=null) {
            editor.putString("id", currentFirebaseUser.getUid());
            editor.apply();

            id = sharedPreferences.getString("id", null);
            Log.d("TAG", "onCreate: "+sharedPreferences.getString("id", null));
        }
        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
        if(acct!=null){
            id = acct.getId();
            editor.putString("id", id);
            editor.apply();
            databaseReference.child(id).child("phuongthucdangnhap").setValue("google");
            databaseReference.child(id).child("email").setValue(acct.getEmail());
            databaseReference.child(id).child("ten").setValue((acct.getFamilyName()+" "+acct.getGivenName()));
            databaseReference.child(id).child("anhUrl").setValue(acct.getPhotoUrl().toString());
            databaseReference.child(id).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot element : snapshot.getChildren()){
                        String check = null;
                        check = snapshot.child("dangnhaplandau").getValue().toString();
                        if(check==null){
                            databaseReference.child(id).child("ngaytao").setValue(new SimpleDateFormat("dd-MM-yyyy hh:mm aa", Locale.getDefault()).format(new Date()));
                            databaseReference.child(id).child("dangnhaplandau").setValue(false);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        if (accessToken!=null && !accessToken.isExpired()){
            GraphRequest request = GraphRequest.newMeRequest(accessToken,
                    new GraphRequest.GraphJSONObjectCallback() {
                        @Override
                        public void onCompleted(JSONObject object, GraphResponse response) {
                            // Application code
                            try {
                                id = response.getJSONObject().getString("id");
                                editor.putString("id", id);
                                editor.apply();
                                databaseReference.child(id).child("phuongthucdangnhap").setValue("facebook");
                                databaseReference.child(id).child("email").setValue(object.getString("email"));
                                databaseReference.child(id).child("ten").setValue(object.getString("name"));
                                databaseReference.child(id).child("anhUrl").setValue(response.getJSONObject().getJSONObject("picture").getJSONObject("data").getString("url"));
                                databaseReference.child(id).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        for (DataSnapshot element : snapshot.getChildren()){

                                            String check = snapshot.child("dangnhaplandau").getValue().toString();
                                            if(check==null){
                                                databaseReference.child(id).child("ngaytao").setValue(new SimpleDateFormat("dd-MM-yyyy hh:mm aa", Locale.getDefault()).format(new Date()));
                                                databaseReference.child(id).child("dangnhaplandau").setValue(false);
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            }
                            catch (Exception e){
                                e.printStackTrace();
                                Log.d("TAG", "onCompleted: get data fail");
                            }

                        }
                    });
            Bundle parameters = new Bundle();
            parameters.putString("fields", "id,name,link,picture.type(large), email");
            request.setParameters(parameters);
            request.executeAsync();
        }
        navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fragment;
                count++;
                    if (!pressed==true){
                        state.add(item.getItemId());
                        pressed=false;
                    }
                switch (item.getItemId()) {
                    case R.id.navigation_home:
                        your_pos=R.id.navigation_home;
                        fragment=new Fragment_Home();
                        Fragment_load(fragment);
                        return true;
//                    case R.id.navigation_notify:
//                        your_pos=R.id.navigation_notify;
//                        fragment=new Fragment_Notify();
//                        Fragment_load(fragment);
//                        return true;
                    case R.id.navigation_setting:
                        your_pos=R.id.navigation_setting;
                        fragment=new Fragment_Setting();
                        Fragment_load(fragment);
                        return true;
                    case R.id.navigation_profile:
                        your_pos=R.id.navigation_profile;
                        fragment=new Fragment_Profile();
                        Fragment_load(fragment);
                        return true;
                }

                return false;
            }

        });

        navigation.setOnNavigationItemReselectedListener(new BottomNavigationView.OnNavigationItemReselectedListener() {
            @Override
            public void onNavigationItemReselected(@NonNull MenuItem item) {

            }
        });

        getSupportFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                if( getSupportFragmentManager().getBackStackEntryCount() <= count) {
                    Log.d("Frag", "onBackStackChanged: "+your_pos);
                }
            }
        });
        ///////////////////////////////////////////////////////////
        state.add(R.id.navigation_home);
        Fragment_load(new Fragment_Home());
        ///////////////////////////////////////////////////////////
        Intent intent = getIntent();
        if(intent.getIntExtra("notice", 0)==1){

        }
    }
    private void Fragment_load(Fragment fragment){
        FragmentTransaction transaction=getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_container,fragment);
        if (!getSupportFragmentManager().getFragments().isEmpty()){
            transaction.addToBackStack(null);
            transaction.setReorderingAllowed(true);
        }


        transaction.commit();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        pressed=true;
        if (state.size()>1){
            state.remove(state.size()-1);
            navigation.setSelectedItemId(state.get(state.size()-1));
        }else if (state.size()==1){
            finish();
        }super.onBackPressed();
        Log.d("TAG", "remove state: "+state.toString());

    }
}