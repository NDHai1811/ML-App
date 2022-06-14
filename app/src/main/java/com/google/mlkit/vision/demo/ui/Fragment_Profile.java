package com.google.mlkit.vision.demo.ui;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.vision.text.Text;
import com.google.android.material.transition.MaterialFadeThrough;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.mlkit.vision.demo.R;
import com.google.mlkit.vision.demo.login.Login;
import com.google.mlkit.vision.demo.preference.SettingsActivity;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Fragment_Profile#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Fragment_Profile extends Fragment {

    ImageView setting, imgProfile;
    ToggleButton editImg;
    TextView tv1, tv2, name, tv_address;
    EditText ed1, ed2;
    CardView logout;
    GoogleSignInOptions gso;
    GoogleSignInClient gsc;
    private FirebaseAuth mAuth;
    AccessToken accessToken;
    String id;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private int timeUsed;


    public Fragment_Profile() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Fragment_Profile.
     */
    // TODO: Rename and change types and number of parameters
    public static Fragment_Profile newInstance(String param1, String param2) {
        Fragment_Profile fragment = new Fragment_Profile();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        setEnterTransition(new MaterialFadeThrough());
        setExitTransition(new MaterialFadeThrough());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_profile,container,false);
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        gsc = GoogleSignIn.getClient(getActivity(),gso);

        accessToken = AccessToken.getCurrentAccessToken();

        setting = view.findViewById(R.id.settingView);
        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), SettingsActivity.class);
                intent.putExtra(SettingsActivity.EXTRA_LAUNCH_SOURCE, SettingsActivity.LaunchSource.LIVE_PREVIEW);
                startActivity(intent);
            }
        });

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
        id = sharedPreferences.getString("id", null);

        name = view.findViewById(R.id.tv_name);
        tv1 = view.findViewById(R.id.emailtv);
        tv2 = view.findViewById(R.id.phonetv);
        ed1 = view.findViewById(R.id.emailet);
        ed2 = view.findViewById(R.id.phoneet);
        imgProfile = view.findViewById(R.id.imgProfile);

        Intent intent = getActivity().getIntent();
        GoogleSignInAccount acc =GoogleSignIn.getLastSignedInAccount(getActivity());
        FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser() ;
        if(currentFirebaseUser!=null) {
            id = sharedPreferences.getString("id", null);
            Log.d("TAG", "onCreate: "+sharedPreferences.getString("id", null));
            FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
            DatabaseReference databaseReference = firebaseDatabase.getReference("user").child(id);
            databaseReference.child("ten").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    name.setText(snapshot.getValue(String.class));
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            databaseReference.child("email").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    tv1.setText(snapshot.getValue(String.class));
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            loadData(view);
            TextView timeUsedTV = (TextView) view.findViewById(R.id.timeUsedTV);
            timeUsedTV.setText(String.valueOf(timeUsed));
        }

        if(acc != null){
            name.setText(acc.getFamilyName()+" "+acc.getGivenName());
            tv1.setText(acc.getEmail());
            Picasso.get().load(acc.getPhotoUrl()).into(imgProfile);
            loadData(view);
            TextView timeUsedTV = (TextView) view.findViewById(R.id.timeUsedTV);
            timeUsedTV.setText(String.valueOf(timeUsed));
            Log.d("TAG", "onCreateView: "+acc.getId());
        }
        if (accessToken!=null && !accessToken.isExpired()){
            GraphRequest request = GraphRequest.newMeRequest(accessToken,
                    new GraphRequest.GraphJSONObjectCallback() {
                        @Override
                        public void onCompleted(JSONObject object, GraphResponse response) {
                            // Application code
                            try {
                                loadData(view);
                                name.setText(object.getString("name"));
                                tv1.setText(object.getString("email"));
                                String url = response.getJSONObject().getJSONObject("picture").getJSONObject("data").getString("url");
                                Picasso.get().load(url).into(imgProfile);
                                Log.d("TAG", "onCompleted: "+object.getString("id"));
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
        }


        logout = view.findViewById(R.id.logout);
        editImg = view.findViewById(R.id.adjustImg);
        editImg.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){
                    tv1.setVisibility(View.GONE);
                    tv2.setVisibility(View.GONE);
                    ed1.setVisibility(View.VISIBLE);
                    ed2.setVisibility(View.VISIBLE);
                }
                else{
                    tv1.setVisibility(View.VISIBLE);
                    tv2.setVisibility(View.VISIBLE);
                    ed1.setVisibility(View.GONE);
                    ed2.setVisibility(View.GONE);
                }
                tv1.setText(ed1.getText());
                tv2.setText(ed2.getText());
            }
        } );

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signOut();
            }
        });



        // Inflate the layout for this fragment
        return view;
    }

    private void loadData(View view) {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference("user").child(id);
        DatabaseReference lichtrinh = databaseReference.child("lichtrinh");
        lichtrinh.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                long tours = snapshot.getChildrenCount();
                long solancanhbao=0;
                TextView timeUsedTV = view.findViewById(R.id.timeUsedTV);
                timeUsedTV.setText(String.valueOf(tours));
                for(DataSnapshot ds : snapshot.getChildren()) {
                    long result = ds.child("chitietlichtrinh").getChildrenCount()-1;
                    if(result<0){
                        result=0;
                    }
                    solancanhbao+=result;
                }
                TextView canhbaoTV = view.findViewById(R.id.solancanhbao);
                canhbaoTV.setText(String.valueOf(solancanhbao));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        timeUsed = 0;
    }

    void signOut(){
        FirebaseAuth.getInstance().signOut();
        LoginManager.getInstance().logOut();
        gsc.signOut();
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("id");
        editor.apply();
        SharedPreferences sharedPreferences1 = getActivity().getSharedPreferences("checkbox", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor1 = sharedPreferences1.edit();
        editor1.remove("remember");
        editor1.apply();
        getActivity().finish();
        startActivity(new Intent(getActivity(), Login.class));
    }
}