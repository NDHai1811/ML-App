package com.google.mlkit.vision.demo.login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.mlkit.vision.demo.R;
import com.niwattep.materialslidedatepicker.SlideDatePickerDialog;
import com.niwattep.materialslidedatepicker.SlideDatePickerDialogCallback;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class register extends AppCompatActivity implements SlideDatePickerDialogCallback {
    private TextView dk;
    private EditText mhoten,memail,mpassword,repass;
    TextView mtuoi;
    private ProgressBar pgbar;
    private FirebaseAuth fAuth;
    private Button btr;
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference;
    String id;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getSupportActionBar().hide();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mhoten=findViewById(R.id.name);
        mtuoi=findViewById(R.id.date);
        memail=findViewById(R.id.email);
        mpassword=findViewById(R.id.password);
        repass=findViewById(R.id.repass);

        btr=findViewById(R.id.btn1);
        fAuth=FirebaseAuth.getInstance();
        pgbar=findViewById(R.id.pgb1);

        SharedPreferences sharedPreferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        databaseReference = firebaseDatabase.getReference("user");

        String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
        mtuoi.setText(currentDate);

        mtuoi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar endDate = Calendar.getInstance();
                endDate.set(Calendar.YEAR, 2100);
                SlideDatePickerDialog.Builder builder = new SlideDatePickerDialog.Builder();
                builder.setEndDate(endDate);
                SlideDatePickerDialog dialog = builder.build();
                dialog.show(getSupportFragmentManager(), "Dialog");
            }
        });

        btr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email= memail.getText().toString().trim();
                String password=mpassword.getText().toString().trim();
                if(TextUtils.isEmpty(email)){
                    memail.setError("Vui lòng điền Email");
                    Log.d("loi","loi");
                }
                else if(TextUtils.isEmpty(password)){
                    mpassword.setError("Vui lòng điền mật khẩu");
                    Log.d("loi","loi");
                }
                else if(password.length()<6){
                    mpassword.setError("Mật khẩu phải có 6 kí tự");
                    Log.d("loi","loi");
                }
                else if(!repass.getText().toString().equals(password)){
                    repass.setError("Cần nhập lại đúng mật khẩu");
                    Log.d("loi","loi");
                }
                else{

                    pgbar.setVisibility(View.VISIBLE);


                    fAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(register.this,"User create",Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getApplicationContext(),Login.class);
                                intent.putExtra("username", memail.getText().toString());
                                intent.putExtra("pass", mpassword.getText().toString());
                                String id = task.getResult().getUser().getUid();
                                databaseReference.child(id).child("phuongthucdangnhap").setValue("email_va_password");
                                databaseReference.child(id).child("email").setValue(memail.getText().toString());
                                databaseReference.child(id).child("ten").setValue(mhoten.getText().toString());
                                databaseReference.child(id).child("ngaytao").setValue(new SimpleDateFormat("dd-MM-yyyy hh:mm aa", Locale.getDefault()).format(new Date()));
//                                databaseReference.child(id).addValueEventListener(new ValueEventListener() {
//                                    @Override
//                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                                        String check = snapshot.child("dangnhaplandau").getValue().toString();
//                                        if(check==null){
//                                            databaseReference.child(id).child("ngaytao").setValue(new SimpleDateFormat("dd-MM-yyyy hh:mm aa", Locale.getDefault()).format(new Date()));
//                                            databaseReference.child(id).child("dangnhaplandau").setValue(false);
//                                        }
//                                    }
//
//                                    @Override
//                                    public void onCancelled(@NonNull DatabaseError error) {
//
//                                    }
//                                });
                                startActivity(intent);
                                pgbar.setVisibility(View.INVISIBLE);
                            }else{
                                pgbar.setVisibility(View.INVISIBLE);
                                if(task.getException().getMessage()=="The email address is badly formatted") {
                                    Toast.makeText(register.this, "Cần nhập đúng dạng email", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    });

                }

            }
        });
    }


    @Override
    public void onPositiveClick(int i, int i1, int i2, Calendar calendar) {
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy", new Locale("vi", "VN"));
        mtuoi.setText(format.format(calendar.getTime()));
    }

}