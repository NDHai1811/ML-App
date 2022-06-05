package com.google.mlkit.vision.demo.history;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.mlkit.vision.demo.R;
import com.google.mlkit.vision.demo.history.History_Adapter;
import com.google.mlkit.vision.demo.map.MapContainer;
import com.google.mlkit.vision.demo.ui.TestUi;

import java.util.ArrayList;

public class History_Adapter extends RecyclerView.Adapter<History_Adapter.ViewHolder>{

    private final ArrayList<History> history;
    private Context mContext;
    ArrayList<History> list = new ArrayList<>();


    public History_Adapter(Context context, ArrayList<History> his) {
        this.history = his;
        mContext = context;
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
        holder.title.setText(data.getTieude());
        holder.start.setText("Bắt đầu: "+data.getBatdau());
        holder.end.setText("Kết thúc: "+data.getKetthuc());
        holder.totalTime.setText("Thời gian đã đi: "+data.getThoigian());
        String[] datetime = data.getNgaythang().split(" ", 2);
        String[] arrOfStr = datetime[0].split("-");
        holder.daynmonth.setText((arrOfStr[0]+"/"+arrOfStr[1]));
        holder.year.setText(arrOfStr[2]);
        holder.time.setText(datetime[1]);
//        holder.cardView.setCardBackgroundColor(Color.parseColor("#378EE8"));

        View view = holder.itemView;
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Intent intent;
                intent =  new Intent(mContext, MapContainer.class);
                intent.putExtra("key", data.getKey());
                intent.putExtra("title", data.getTieude());
                intent.putExtra("start", data.getBatdau());
                intent.putExtra("end", data.getKetthuc());
                intent.putExtra("time", data.getThoigian());
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
        public final TextView title;
        public final TextView start;
        public final TextView end;
        public final TextView totalTime;
        public final TextView daynmonth, year, time;
        public final CardView cardView;

        public ViewHolder(View view) {
            super(view);
            this.view = view;
            title = view.findViewById(R.id.title);
            start = view.findViewById(R.id.start);
            end = view.findViewById(R.id.end);
            totalTime = view.findViewById(R.id.totalTime);
            cardView = view.findViewById(R.id.cardView);
            daynmonth = view.findViewById(R.id.daynmonth);
            year = view.findViewById(R.id.year);
            time = view.findViewById(R.id.time);
        }
    }
}
