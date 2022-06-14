package com.google.mlkit.vision.demo.notification;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.mlkit.vision.demo.NotiDetailActivity;
import com.google.mlkit.vision.demo.R;
import com.google.mlkit.vision.demo.map.MapContainer;

import java.util.ArrayList;

public class Notify_Adapter extends RecyclerView.Adapter<Notify_Adapter.ViewHolder>{

    private final ArrayList<Notify> history;
    private Context mContext;
    ArrayList<Notify> list = new ArrayList<>();


    public Notify_Adapter(Context context, ArrayList<Notify> his) {
        this.history = his;
        mContext = context;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.notify, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Notify data = history.get(position);
        holder.start.setText(data.getTieude());
        holder.end.setText(data.getMota());
        holder.img.setBackgroundResource(R.drawable.logodawn);





//        holder.cardView.setCardBackgroundColor(Color.parseColor("#378EE8"));

        View view = holder.itemView;
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Intent intent;
                intent =  new Intent(mContext, NotiDetailActivity.class);

                intent.putExtra("title", data.getTieude());

                intent.putExtra("detail", data.getChitiet());
                mContext.startActivity(intent);
            }
        });
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
        public final ImageView img;
        public final TextView start;
        public final TextView end;


        public final CardView cardView;

        public ViewHolder(View view) {
            super(view);
            this.view = view;
            img= view.findViewById(R.id.imglogo);
            start = view.findViewById(R.id.tieude);
            end = view.findViewById(R.id.mota);

            cardView = view.findViewById(R.id.cardView);

        }
    }
}
