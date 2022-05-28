package com.google.mlkit.vision.demo;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.os.CountDownTimer;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

public class ViewDialog extends Dialog {

    public Dialog dialog = null;
    int count = 0;
    public ViewDialog(Context context) {
        super(context);
    }
    Button dialogButton;

    public void showDialog(Activity activity, String msg){
        dialog = new Dialog(activity);

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        TextView text = dialog.findViewById(R.id.text_dialog);
        text.setText(msg);

        Button dialogButton = dialog.findViewById(R.id.btn_dialog);
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

            }
        });
        if (dialog == null && !dialog.isShowing()&&count==0) {
            dialog.show();
            count++;
        }

    }

    AlertCalculate alertCalculate;


    public void loseAttention(Dialog dialog){
        alertCalculate = new AlertCalculate(getContext(), getOwnerActivity());
        dialog.setContentView(R.layout.dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        TextView text = dialog.findViewById(R.id.text_dialog);
        text.setText("Bạn đang mất tập trung!!!");
        TextView text_tips = dialog.findViewById(R.id.tt_tips);
        text_tips.setText("Hãy cố gắng tập trung lái xe an toàn nhé");
        alertCalculate.startVibrate();
        startTimer();
        dialogButton = dialog.findViewById(R.id.btn_dialog);
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                alertCalculate.stopVibrate();
                cancelTimer();
            }
        });
        dialog.show();
    }

    public void sleepyAlert(Dialog dialog){
        alertCalculate = new AlertCalculate(getContext(), getOwnerActivity());
        dialog.setContentView(R.layout.dialog_sleepy);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        TextView text = dialog.findViewById(R.id.text_dialog);
        text.setText("Bạn đang buồn ngủ!!!");
        TextView text_tips = dialog.findViewById(R.id.tt_tips);
        text_tips.setText("Hãy dừng xe uống nước hoặc rửa mặt để cảm thấy tỉnh táo rồi tiếp tục lái xe nhé");
        alertCalculate.startVibrate();
        dialog.setCanceledOnTouchOutside(false);
        MediaPlayer mp = MediaPlayer.create(getContext(), R.raw.khobau);
        mp.setLooping(true);
        mp.start();
        Button dialogButton = dialog.findViewById(R.id.btn_dialog);
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                mp.stop();
                alertCalculate.stopVibrate();
            }
        });
        dialog.show();
    }

    //Declare timer
    CountDownTimer cTimer = null;

    //start timer function
    void startTimer() {
        cTimer = new CountDownTimer(11000, 1000) {
            public void onTick(long millisUntilFinished) {
                dialogButton.setText("Bỏ qua("+millisUntilFinished/1000+"s)");
            }
            public void onFinish() {
                dialogButton.performClick();
            }
        };
        cTimer.start();
    }


    //cancel timer
    void cancelTimer() {
        if(cTimer!=null)
            cTimer.cancel();
    }
}
