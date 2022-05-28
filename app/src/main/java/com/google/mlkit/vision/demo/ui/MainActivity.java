package com.google.mlkit.vision.demo.ui;

import android.content.Intent;
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

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.mlkit.vision.demo.R;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    public int count=0;
    public int your_pos=0;
    BottomNavigationView navigation;
    List<Integer> state = new ArrayList<>();
    boolean pressed=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();

        setContentView(R.layout.activity_main);
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

                Log.d("TAG", "add state: "+state.toString());
                switch (item.getItemId()) {
                    case R.id.navigation_home:
                        your_pos=R.id.navigation_home;
                        fragment=new Fragment_Home();
                        Fragment_load(fragment);
                        return true;
                    case R.id.navigation_notify:
                        your_pos=R.id.navigation_notify;
                        fragment=new Fragment_Notify();
                        Fragment_load(fragment);
                        return true;
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