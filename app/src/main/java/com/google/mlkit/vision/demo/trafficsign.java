package com.google.mlkit.vision.demo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.Toast;

import com.google.mlkit.vision.demo.traffic_sign.CourseAdapter;
import com.google.mlkit.vision.demo.traffic_sign.CourseModal;
import com.google.mlkit.vision.demo.traffic_sign.TrafficSign;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class trafficsign extends AppCompatActivity {

    // creating variables for
    // our ui components.
    private RecyclerView courseRV;

    // variable for our adapter
    // class and array list
    private CourseAdapter adapter;
    private ArrayList<CourseModal> courseModalArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        ColorDrawable colorDrawable
                = new ColorDrawable(Color.parseColor("#02457A"));
        getSupportActionBar().setBackgroundDrawable(colorDrawable);
        getSupportActionBar().setTitle("Tra cứu biển báo");
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_trafficsign);

        // initializing our variables.
        courseRV = findViewById(R.id.idRVCourses);

        // calling method to
        // build recycler view.
        buildRecyclerView();
    }

    // calling on create option menu
    // layout to inflate our menu file.
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // below line is to get our inflater
        MenuInflater inflater = getMenuInflater();

        // inside inflater we are inflating our menu file.
        inflater.inflate(R.menu.search_menu, menu);

        // below line is to get our menu item.
        MenuItem searchItem = menu.findItem(R.id.actionSearch);

        // getting search view of our item.
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
        searchView.setInputType(InputType.TYPE_CLASS_TEXT);
        // below line is to call set on query text listener method.
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // inside on query text change method we are
                // calling a method to filter our recycler view.
                filter(newText);
                return false;
            }
        });
        return true;
    }

    private void filter(String text) {
        // creating a new array list to filter our data.
        ArrayList<CourseModal> filteredlist = new ArrayList<>();

        // running a for loop to compare elements.
        for (CourseModal item : courseModalArrayList) {
            // checking if the entered string matched with any item of our recycler view.
            if (item.getCourseName().toLowerCase().contains(text.toLowerCase())) {
                // if the item is matched we are
                // adding it to our filtered list.
                filteredlist.add(item);
            }
        }
        if (filteredlist.isEmpty()) {
            // if no item is added in filtered list we are
            // displaying a toast message as no data found.
//            Toast.makeText(this, "No Data Found..", Toast.LENGTH_SHORT).show();
        } else {
            // at last we are passing that filtered
            // list to our adapter class.
            adapter.filterList(filteredlist);
        }
    }

    private List<TrafficSign> trafficSign = new ArrayList<>();
    private void buildRecyclerView() {

        // below line we are creating a new array list
        courseModalArrayList = new ArrayList<>();

        InputStream is = getResources().openRawResource(R.raw.data);
        BufferedReader reader = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
        ArrayList<CourseModal> list = new ArrayList<>();
        String line="";
        try {
            while((line=reader.readLine())!=null){
//                Log.d("this", "Line "+line);
                String[] token = line.split(",");

                TrafficSign sample = new TrafficSign();
                sample.setImgUrl(token[0]);
                if (token.length>=4 && token[1].length()>0){
                    sample.setNameSign(token[1]);
                }else {
                    sample.setNameSign("");
                }

                if (token.length>=4 && token[2].length()>0){
                    sample.setNumberSign(token[2]);
                }else {
                    sample.setNumberSign("");
                }
//                sample.setNumberSign(token[2]);
                if (token.length>=4 && token[3].length()>0){
                    sample.setDescription(token[3]);
                }else {
                    sample.setDescription("");
                }
//                sample.setDescription(token[3]);

                trafficSign.add(sample);


                if (token[0].length()>0&&token[1].length()>0&&token[3].length()>0){
                    courseModalArrayList.add(new CourseModal(sample.getNameSign(), sample.getDescription(), sample.getImgUrl()));
                }

                Log.d("this", "Just created "+sample);
            }
        } catch (IOException e) {
            Log.wtf("this", "Error reading data on file"+line, e);
            e.printStackTrace();
        }

        // initializing our adapter class.
        adapter = new CourseAdapter(courseModalArrayList, trafficsign.this);

        // adding layout manager to our recycler view.
        LinearLayoutManager manager = new LinearLayoutManager(this);
        courseRV.setHasFixedSize(true);

        // setting layout manager
        // to our recycler view.
        courseRV.setLayoutManager(manager);

        // setting adapter to
        // our recycler view.
        courseRV.setAdapter(adapter);
    }
}