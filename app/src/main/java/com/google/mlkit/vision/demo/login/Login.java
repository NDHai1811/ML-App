package com.google.mlkit.vision.demo.login;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.telecom.Call;
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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.identity.SignInClient;
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
import com.google.firebase.auth.FirebaseUser;
import com.google.mlkit.vision.demo.R;
import com.google.mlkit.vision.demo.ui.MainActivity;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.Arrays;
import java.util.Objects;


public class Login extends AppCompatActivity {

    EditText mEmail,mPassword;
    TextView mLabel,msignup,mloginphone;
    Button msignin;
    CheckBox remeber;
    private ProgressBar pgbar;
    ImageView mfbtn,mggbtn;
//    CallbackManager callbackManager;
    FirebaseAuth mAuth;
    GoogleSignInOptions gso;
    GoogleSignInClient gsc;
    CallbackManager callbackManager;
    boolean google = false;
    boolean facebook = false;

    private static final String TAG = "GoogleActivity";
    private static final int RC_SIGN_IN = 9001;

    private static final String EMAIL = "email";
    private String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_login);

        SharedPreferences sharedPreferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        mggbtn=findViewById(R.id.ggbtn);
        mEmail=findViewById(R.id.username);
        mPassword=findViewById(R.id.password);
        msignup=findViewById(R.id.txtpro);
        msignin=findViewById(R.id.button);
        pgbar=findViewById(R.id.pgb1);
        remeber=findViewById(R.id.checkBox);
        mfbtn=findViewById(R.id.fbtn);

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        gsc = GoogleSignIn.getClient(this,gso);

        //check google login
        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
        if(acct!=null){
            id = acct.getId();
            Log.d(TAG, "onCreate: "+id);
            editor.putString("id", id);
            editor.apply();
            navigateToSecondActivity();
        }
        //check facebook login
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        if (accessToken!=null && !accessToken.isExpired()){
            GraphRequest request = GraphRequest.newMeRequest(accessToken,
                    new GraphRequest.GraphJSONObjectCallback() {
                        @Override
                        public void onCompleted(JSONObject object, GraphResponse response) {
                            // Application code
                            try {
                                id = response.getJSONObject().getString("id");
                                editor.putString("id", id);
                                editor.apply();
                                Log.d(TAG, "onCreate: "+id);
                            }
                            catch (Exception e){
                                e.printStackTrace();
                                Log.d("TAG", "onCompleted: get data fail");
                            }

                        }
                    });
            Bundle parameters = new Bundle();
            parameters.putString("fields", "id,name,link,picture.type(large), email");
            request.setParameters(parameters);
            request.executeAsync();

            navigateToSecondActivity();
        }
//google login button
        mggbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                google = true;
                signIn();

            }
        });

//facebook login button
        mfbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                facebook = true;
                callbackManager = CallbackManager.Factory.create();

                LoginManager.getInstance().registerCallback(callbackManager,
                        new FacebookCallback<LoginResult>() {
                            @Override
                            public void onSuccess(LoginResult loginResult) {
                                navigateToSecondActivity();
                                Log.d(TAG, "onSuccess: success");
                            }

                            @Override
                            public void onCancel() {
                                // App code
                                Log.d(TAG, "onCancel: cancel");
                            }

                            @Override
                            public void onError(FacebookException exception) {
                                // App code
                                Log.d(TAG, "onError: fail "+exception.toString());
                            }
                        });
                LoginManager.getInstance().logInWithReadPermissions(Login.this, Arrays.asList("public_profile, email"));
            }
        });

        Intent intent1 = getIntent();
        if (intent1.hasExtra("username")) {
            String user = intent1.getStringExtra("username");
            Log.d("TAG", "user: "+user);
            mEmail.setText(user);
            intent1.removeExtra("username");
        } else {
            mEmail.setText("");
        }

        if (intent1.hasExtra("pass")) {
            String pass = intent1.getStringExtra("pass");
            Log.d("TAG", "pass: "+pass);
            mPassword.setText(pass);
            intent1.removeExtra("pass");
        } else {
            mPassword.setText("");
        }

//goi lai email va mk sau khi dang ky xong
        SharedPreferences preferences=getSharedPreferences("checkbox",MODE_PRIVATE);
        String checkbox=preferences.getString("remember","");
        if(checkbox.equals("true")){
            Intent intent2=new Intent(Login.this,MainActivity.class);
            startActivity(intent2);
        }
        else if(checkbox.equals("false")){
            Toast.makeText(this,"Bạn chưa đăng nhập",Toast.LENGTH_SHORT).show();
        }
//Dang nhap
        msignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email= mEmail.getText().toString().trim();
                String password=mPassword.getText().toString().trim();
                if(TextUtils.isEmpty(email)){
                    mEmail.setError("Vui lòng điền Email");
                }
                else if(TextUtils.isEmpty(password)){
                    mPassword.setError("Vui lòng điền mật khẩu");
                }
                else if(password.length()<6){
                    mPassword.setError("Mật khẩu phải có 6 kí tự");
                }

                else{
                    pgbar.setVisibility(View.VISIBLE);
                    FirebaseAuth.getInstance().signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(Login.this,"Login thành công",Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(getApplicationContext(),MainActivity.class));
                                Intent intent = getIntent();
                                if(intent.hasExtra("id")){
                                    Log.d(TAG, "onComplete: "+intent.getStringExtra("id"));
                                    editor.putString("id", task.getResult().getUser().getUid());
                                    editor.apply();
                                }
                                pgbar.setVisibility(View.GONE);

                            }else{
                                pgbar.setVisibility(View.GONE);
//                                Toast.makeText(Login.this,"Lỗi "+task.getException().toString(),Toast.LENGTH_LONG).show();
                            }
                        }

                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            if(Objects.equals(e.getMessage(), "The email address is badly formatted.")){
                                Toast.makeText(Login.this,"Tài khoản không hợp lệ",Toast.LENGTH_LONG).show();
                            }
                            else if (Objects.equals(e.getMessage(), "The password is invalid or the user does not have a password.")){
                                Toast.makeText(Login.this,"Mật khẩu không đúng",Toast.LENGTH_LONG).show();
                            }
                            else if (Objects.equals(e.getMessage(),"There is no user record corresponding to this identifier. The user may have been deleted.")){
                                Toast.makeText(Login.this,"Tài khoản này không có trong hệ thống",Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            }

        });
//Dang ky moi
        msignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Login.this,register.class));
            }
        });

        remeber.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(compoundButton.isChecked()){
                    SharedPreferences preferences=getSharedPreferences("checkbox",MODE_PRIVATE);
                    SharedPreferences.Editor editor=preferences.edit();
                    editor.putString("remember","true");
                    editor.apply();
                    Toast.makeText(Login.this,"Checked",Toast.LENGTH_SHORT).show();

                }else if(!compoundButton.isChecked()){
                    SharedPreferences preferences=getSharedPreferences("checkbox",MODE_PRIVATE);
                    SharedPreferences.Editor editor=preferences.edit();
                    editor.putString("remember","false");
                    editor.apply();
                    Toast.makeText(Login.this,"Unchecked",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(google==true) {
            if (requestCode == 1000) {
                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);

                try {
                    task.getResult(ApiException.class);
                    navigateToSecondActivity();
                } catch (ApiException e) {
                    Toast.makeText(getApplicationContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
                }
            }
        }
        if(facebook==true) {
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
        super.onActivityResult(requestCode, resultCode, data);

    }
    void navigateToSecondActivity(){
        finish();
        Intent intent = new Intent(Login.this,MainActivity.class);
        intent.putExtra("google", google);
        intent.putExtra("facebook", facebook);
        facebook = false;
        google = false;
        startActivity(intent);

    }

    // [START signin]
    private void signIn() {
        Intent signInIntent = gsc.getSignInIntent();
        startActivityForResult(signInIntent,1000);
    }
    // [END signin]

    private void updateUI(FirebaseUser user) {

    }


    @Override
    protected void onStart() {
        super.onStart();
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        if(accessToken != null && accessToken.isExpired()==false){
            navigateToSecondActivity();
        }
    }
}