package com.google.mlkit.vision.demo.ui;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.google.android.material.transition.MaterialFadeThrough;
import com.google.mlkit.vision.demo.R;
import com.google.mlkit.vision.demo.preference.SettingsActivity;

import org.w3c.dom.Text;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Fragment_Profile#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Fragment_Profile extends Fragment {

    ImageView setting;
    ToggleButton editImg;
    TextView tv1, tv2;
    EditText ed1, ed2;

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
        setting = view.findViewById(R.id.settingView);
        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), SettingsActivity.class);
                intent.putExtra(SettingsActivity.EXTRA_LAUNCH_SOURCE, SettingsActivity.LaunchSource.LIVE_PREVIEW);
                startActivity(intent);
            }
        });
        tv1 = view.findViewById(R.id.emailtv);
        tv2 = view.findViewById(R.id.phonetv);
        ed1 = view.findViewById(R.id.emailet);
        ed2 = view.findViewById(R.id.phoneet);
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

        loadData();
        TextView timeUsedTV = (TextView) view.findViewById(R.id.timeUsedTV);
        timeUsedTV.setText(String.valueOf(timeUsed));


        // Inflate the layout for this fragment
        return view;
    }

    private void loadData() {
        // method to load arraylist from shared prefs
        // initializing our shared prefs with name as
        // shared preferences.
        SharedPreferences sharedPreferences = this.getActivity().getSharedPreferences("shared preferences", MODE_PRIVATE);

        timeUsed = sharedPreferences.getInt("counter", 0);
    }
}