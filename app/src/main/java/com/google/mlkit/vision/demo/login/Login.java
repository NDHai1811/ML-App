package com.google.mlkit.vision.demo.login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.mlkit.vision.demo.R;
import com.google.mlkit.vision.demo.ui.MainActivity;

import java.util.Arrays;
import java.util.Objects;

public class Login extends AppCompatActivity {

    EditText mEmail,mPassword;
    TextView mLabel,msignup,mloginphone;
    Button msignin;
    CheckBox remeber;
    private ProgressBar pgbar;
    ImageView mfbtn,mggbtn;
    CallbackManager callbackManager;
    FirebaseAuth fAuth;
    GoogleSignInOptions gso;
    GoogleSignInClient gsc;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mggbtn = findViewById(R.id.ggbtn);
        mEmail = findViewById(R.id.username);
        mPassword = findViewById(R.id.password);
        msignup = findViewById(R.id.txtpro);
        msignin = findViewById(R.id.button);
        pgbar = findViewById(R.id.pgb1);
        remeber = findViewById(R.id.checkBox);

        mfbtn = findViewById(R.id.fbtn);


        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        gsc = GoogleSignIn.getClient(this, gso);

        mggbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();

            }
        });

       callbackManager = CallbackManager.Factory.create();
//
//        LoginManager.getInstance().registerCallback(callbackManager,
//                new FacebookCallback<LoginResult>() {
//                    @Override
//                    public void onSuccess(LoginResult loginResult) {
//                        startActivity(new Intent(Login.this, MainActivity.class));
//                        finish();
//                    }
//
//                    @Override
//                    public void onCancel() {
//                        // App code
//                    }
//
//                    @Override
//                    public void onError(FacebookException exception) {
//                        // App code
//                    }
//                });
//
//        mfbtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//
//                LoginManager.getInstance().logInWithReadPermissions(Login.this, Arrays.asList("public_profile"));
//            }
//        });


        Log.d("TAG", "onCreate: ");
        Intent intent1 = getIntent();
        if (intent1.hasExtra("username")) {
            String user = intent1.getStringExtra("username");
            Log.d("TAG", "user: " + user);
            mEmail.setText(user);
            intent1.removeExtra("username");
        } else {
            mEmail.setText("");
        }

        if (intent1.hasExtra("pass")) {
            String pass = intent1.getStringExtra("pass");
            Log.d("TAG", "pass: " + pass);
            mPassword.setText(pass);
            intent1.removeExtra("pass");
        } else {
            mPassword.setText("");
        }


        SharedPreferences preferences = getSharedPreferences("checkbox", MODE_PRIVATE);
        String checkbox = preferences.getString("remember", "");
        if (checkbox.equals("true")) {
            Intent intent2 = new Intent(Login.this, MainActivity.class);
            startActivity(intent2);
        } else if (checkbox.equals("false")) {
            Toast.makeText(this, "Bạn chưa đăng nhập", Toast.LENGTH_SHORT).show();
        }

        msignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = mEmail.getText().toString().trim();
                String password = mPassword.getText().toString().trim();
                if (TextUtils.isEmpty(email)) {
                    mEmail.setError("Vui lòng điền Email");
                } else if (TextUtils.isEmpty(password)) {
                    mPassword.setError("Vui lòng điền mật khẩu");
                } else if (password.length() < 6) {
                    mPassword.setError("Mật khẩu phải có 6 kí tự");
                } else {
                    pgbar.setVisibility(View.VISIBLE);
                    FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(Login.this, "Login thành công", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                pgbar.setVisibility(View.GONE);

                            } else {
                                pgbar.setVisibility(View.GONE);
//                                Toast.makeText(Login.this,"Lỗi "+task.getException().toString(),Toast.LENGTH_LONG).show();
                            }
                        }

                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            if (Objects.equals(e.getMessage(), "The email address is badly formatted.")) {
                                Toast.makeText(Login.this, "Tài khoản không hợp lệ", Toast.LENGTH_LONG).show();
                            } else if (Objects.equals(e.getMessage(), "The password is invalid or the user does not have a password.")) {
                                Toast.makeText(Login.this, "Mật khẩu không đúng", Toast.LENGTH_LONG).show();
                            } else if (Objects.equals(e.getMessage(), "There is no user record corresponding to this identifier. The user may have been deleted.")) {
                                Toast.makeText(Login.this, "Tài khoản này không có trong hệ thống", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            }

        });
//        msignup.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                startActivity(new Intent(getApplicationContext(), register_class.class));
//            }
//        });

        remeber.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (compoundButton.isChecked()) {
                    SharedPreferences preferences = getSharedPreferences("checkbox", MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("remember", "true");
                    editor.apply();
                    Toast.makeText(Login.this, "Checked", Toast.LENGTH_SHORT).show();

                } else if (!compoundButton.isChecked()) {
                    SharedPreferences preferences = getSharedPreferences("checkbox", MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("remember", "false");
                    editor.apply();
                    Toast.makeText(Login.this, "Unchecked", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1000){
            Task<GoogleSignInAccount>task=GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                task.getResult(ApiException.class);
                navigateToSecondActivity();
            } catch (ApiException e) {
                Toast.makeText(getApplicationContext(),"Lỗi",Toast.LENGTH_SHORT).show();
            }
        }
    }
    void signIn(){
        Intent signInIntent = gsc.getSignInIntent();
        startActivityForResult(signInIntent,1000);
    }
    void navigateToSecondActivity(){
        finish();
        Intent intent = new Intent(Login.this, MainActivity.class);
        startActivity(intent);
    }
}