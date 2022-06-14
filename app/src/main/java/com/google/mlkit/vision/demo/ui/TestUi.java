package com.google.mlkit.vision.demo.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.RectF;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.mlkit.vision.demo.R;
import com.google.mlkit.vision.demo.history.DAOHistory;
import com.google.mlkit.vision.demo.history.History;
import com.google.mlkit.vision.demo.history.History_Adapter;

import org.json.JSONObject;

import java.util.ArrayList;

public class TestUi extends AppCompatActivity {

    RecyclerView historyRV;
    private ArrayList<History> histories;
    private History_Adapter adapter;
    ArrayList<History> list = new ArrayList<>();
    private boolean deleteButtonVisible = false;
    private boolean editButtonVisible = false;
    private int posSwiped = -1;
    private int lastSwipe = -1;
    private boolean moving = false;

    SwipeRefreshLayout swipeRefreshLayout;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    DAOHistory dao;
    boolean isLoading=false;
    String key =null;
    Bitmap icon;
    int position;
    TextView info;
    String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle("Lịch trình");
        setContentView(R.layout.activity_test_ui);

        SharedPreferences sharedPreferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
        id = sharedPreferences.getString("id", null);

        info = findViewById(R.id.textView5);
        historyRV = findViewById(R.id.historyRV);
        historyRV.setHasFixedSize(true);
        adapter = new History_Adapter(this, list);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        this.historyRV.setLayoutManager(linearLayoutManager);
        this.historyRV.setAdapter(adapter);
        dao = new DAOHistory(this);
//        loadData();
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(this.historyRV);
    }


    public ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            moving = true;
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            moving = false;
            position = viewHolder.getLayoutPosition();
            // Close item swiped before
            if (lastSwipe != -1 && lastSwipe != position)
                adapter.notifyItemChanged(lastSwipe);
//            Log.d("lastSwiped", "" + lastSwipe);
//            Log.d("Item current position", "" + position);
            lastSwipe = position;
            Log.d("Button Visible", "" + deleteButtonVisible);
        }

        @Override
        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            posSwiped = viewHolder.getAbsoluteAdapterPosition();

            View itemView = viewHolder.itemView;
            if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
//                View itemView = viewHolder.itemView;
                float height = itemView.getBottom() - itemView.getTop();
                float width = height / 3;

                if (dX < 0) {
                    Paint p = new Paint();
                    p.setColor(Color.RED);
                    RecyclerView.LayoutParams lp = (RecyclerView.LayoutParams) itemView.getLayoutParams();
                    Log.d("TAG", "Params: "+lp.topMargin+" "+lp.bottomMargin);
                    float deleteButtonLeft = itemView.getRight() - (itemView.getRight() / 5f);
                    float deleteButtonTop = itemView.getTop() + lp.topMargin+10;
                    float deleteButtonRight = itemView.getRight() - itemView.getPaddingRight();
                    float deleteButtonBottom = itemView.getBottom() - lp.bottomMargin-10;

                    // Draw a button
                    float radius = 15f;
                    RectF deleteButtonDelete = new RectF(deleteButtonLeft, deleteButtonTop, deleteButtonRight, deleteButtonBottom);
                    c.drawRoundRect(deleteButtonDelete, radius, radius, p);
                    icon = BitmapFactory.decodeResource(getResources(), R.drawable.delete);
                    float margin = (dX / 5 - width) / 2;
                    RectF iconDest = new RectF(deleteButtonRight + margin, itemView.getTop() + width, deleteButtonLeft - margin, itemView.getBottom() - width);
                    c.drawBitmap(icon, null, iconDest, p);
                    if (dX <= -deleteButtonLeft) {
                        deleteButtonVisible = true;
                        editButtonVisible = false;
                        moving = false;
                    } else {
                        deleteButtonVisible = false;
                        moving = true;
                    }
                }
                if (dX == 0.0f)
                    moving = false;
            }
            else {
                c.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
            }
            if (deleteButtonVisible)
                clickDeleteButtonListener(recyclerView, posSwiped);
            super.onChildDraw(c, recyclerView, viewHolder, dX / 5, dY, actionState, isCurrentlyActive);
        }

        // Swipe back (start, end, top, down)
        @Override
        public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder){
            return makeMovementFlags(0, ItemTouchHelper.LEFT);
        }
    };



    @SuppressLint("ClickableViewAccessibility")
    private void clickDeleteButtonListener(RecyclerView recyclerView, int posSwiped) {
        RecyclerView.ViewHolder viewHolder = recyclerView.findViewHolderForAdapterPosition(posSwiped);
        View item = viewHolder != null ? viewHolder.itemView : null;
        if(item != null){
            recyclerView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    if (motionEvent.getAction() == MotionEvent.ACTION_DOWN && motionEvent.getY() > item.getY() && motionEvent.getY() < item.getY() + item.getHeight()
                            && motionEvent.getX() > item.getX() + item.getWidth() && !moving) {
                        if (deleteButtonVisible) {
//                            Toast.makeText(getApplicationContext(), "Click to Button Delete " + posSwiped, Toast.LENGTH_SHORT).show();
                            new AlertDialog.Builder(TestUi.this)
                                    .setTitle("Nhac nho")
                                    .setMessage("Ban co chac muon xoa lich trinh nay?")
                                    .setIcon(android.R.drawable.ic_dialog_alert)
                                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                                        public void onClick(DialogInterface dialog, int whichButton) {

                                            String key = list.get(posSwiped).getKey();
                                            DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                                            ref.child("user").child(id).child("lichtrinh").child(key).removeValue();

                                            list.remove(posSwiped);

                                            adapter.notifyItemRemoved(posSwiped);
                                            adapter.notifyItemRangeChanged(posSwiped, list.size());
                                            deleteButtonVisible = false;
                                            Toast.makeText(TestUi.this, "Đã xoá!", Toast.LENGTH_SHORT).show();
                                        }})
                                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            adapter.notifyItemChanged(posSwiped);
                                        }
                                    }).show();
                            deleteButtonVisible = false;
                        }
                    }
                    return false;
                }
            });
        }
        
    }


    private void deleteItem(int position) {
        if (position < list.size()) {
            list.remove(position);
            adapter.notifyItemRemoved(position);
            adapter.notifyItemRangeChanged(position, list.size());
        }
    }

//    public void init() {
//        firebaseDatabase = FirebaseDatabase.getInstance();
//        databaseReference = firebaseDatabase.getReference("user");
//        Query query = databaseReference.child("lichtrinh");
//        query.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                JSONObject data = new JSONObject();
//                History history = null;
//                if (snapshot.exists()) {
//                    for(DataSnapshot element : snapshot.getChildren()) {
//                        String txt = element.getValue(History.class).getBatdau();
////                        list.add(new History(element.getValue(History.class).getTieude(), element.getValue(History.class).getBatdau(), element.getValue(History.class).getKetthuc(), element.getValue(History.class).getThoigian()));
//                        Log.d("TestUi", "onDataChange: "+txt);
//                    }
////                    Log.d("TestUi", "onDataChange: "+history);
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//    }

    private void loadData()
    {
        dao.get(key).addListenerForSingleValueEvent(new ValueEventListener()
        {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                info.setVisibility(View.GONE);
                ArrayList<History> emps = new ArrayList<>();
                for (DataSnapshot data : snapshot.getChildren())
                {
                    History his = data.getValue(History.class);
                    Log.d("TestUI", "onDataChange: "+data.getKey());
                    his.setKey(data.getKey());
                    emps.add(his);
                    list.add(his);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error)
            {
                info.setVisibility(View.VISIBLE);
//                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        list.removeAll(list);
        loadData();
    }
}