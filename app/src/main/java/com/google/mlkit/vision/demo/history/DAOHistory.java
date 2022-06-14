package com.google.mlkit.vision.demo.history;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.mlkit.vision.demo.java.LivePreviewActivity;

import java.util.HashMap;

public class DAOHistory
{
    String id;
    private DatabaseReference databaseReference;
    public DAOHistory(Context context)
    {
        SharedPreferences sharedPreferences = context.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
        id = sharedPreferences.getString("id", null);
        FirebaseDatabase db =FirebaseDatabase.getInstance();
        databaseReference = db.getReference("user").child(id).child("lichtrinh");
    }
    public Task<Void> add(History his)
    {
        return databaseReference.push().setValue(his);
    }

    public Task<Void> update(String key, HashMap<String ,Object> hashMap)
    {
        return databaseReference.child(key).updateChildren(hashMap);
    }
    public Task<Void> remove(String key)
    {
        return databaseReference.child(key).removeValue();
    }

    public Query get(String key)
    {
        if(key == null)
        {
           return databaseReference.orderByKey().limitToFirst(8);
        }
        return databaseReference.orderByKey().startAfter(key).limitToFirst(8);
    }

    public Query get()
    {
        return databaseReference;
    }
}
