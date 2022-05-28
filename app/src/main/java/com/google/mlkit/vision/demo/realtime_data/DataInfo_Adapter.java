package com.google.mlkit.vision.demo.realtime_data;

import android.graphics.Color;
import android.media.MediaRouter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.google.mlkit.vision.demo.R;

import java.util.ArrayList;

public class DataInfo_Adapter extends RecyclerView.Adapter<DataInfo_Adapter.ViewHolder>{

    private final ArrayList<DataInfo> dates;

    public DataInfo_Adapter(ArrayList<DataInfo> dates) {
        this.dates = dates;
    }

    @NonNull
    @Override
    public DataInfo_Adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.realtime_info, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull DataInfo_Adapter.ViewHolder holder, int position) {
        DataInfo data = dates.get(position);
        holder.location.setText(data.getLocation());
        holder.realtime.setText(data.getrTime());
        holder.counter.setText(data.getCounter());
        holder.cardView.setCardBackgroundColor(Color.parseColor("#378EE8"));
    }

    @Override
    public int getItemCount() {
        if (dates != null) {
            return dates.size();
        } else {
            return 0;
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final View view;
        public final TextView location;
        public final TextView realtime;
        public final TextView counter;
        public final CardView cardView;

        public ViewHolder(View view) {
            super(view);
            this.view = view;
            location = view.findViewById(R.id.locateTV);
            realtime = view.findViewById(R.id.timeTV);
            counter = view.findViewById(R.id.sleepCountTV);
            cardView = view.findViewById(R.id.cardView);
        }
    }


}
