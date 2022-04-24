package com.google.mlkit.vision.demo.history;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.mlkit.vision.demo.R;
import com.google.mlkit.vision.demo.history.History_Adapter;
import com.google.mlkit.vision.demo.ui.TestUi;

import java.util.ArrayList;

public class History_Adapter extends RecyclerView.Adapter<History_Adapter.ViewHolder>{

    private final ArrayList<History> history;


    public History_Adapter(ArrayList<History> his) {
        this.history = his;
    }
    @NonNull
    @Override
    public History_Adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.history, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull History_Adapter.ViewHolder holder, int position) {
        History data = history.get(position);
        holder.start.setText(data.getStartDestination());
        holder.end.setText(data.getEndDestination());
        holder.totalTime.setText(data.getTotalTime());
        holder.counter.setText(data.getSleepyCount());

        holder.cardView.setCardBackgroundColor(Color.parseColor("#378EE8"));

    }

    @Override
    public int getItemCount() {
        if (history != null) {
            return history.size();
        } else {
            return 0;
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final View view;
        public final TextView start;
        public final TextView end;
        public final TextView totalTime;
        public final TextView counter;
        public final CardView cardView;

        public ViewHolder(View view) {
            super(view);
            this.view = view;
            start = view.findViewById(R.id.start);
            end = view.findViewById(R.id.end);
            totalTime = view.findViewById(R.id.totalTime);
            counter = view.findViewById(R.id.counter);
            cardView = view.findViewById(R.id.cardView);
        }
    }
}
