package com.google.mlkit.vision.demo.ui;

import android.content.Intent;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.transition.MaterialFadeThrough;
import com.google.mlkit.vision.demo.LuatXP;
import com.google.mlkit.vision.demo.R;
import com.google.mlkit.vision.demo.note;
import com.google.mlkit.vision.demo.trafficsign;
import com.google.mlkit.vision.demo.web.WebActivity;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Fragment_Setting#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Fragment_Setting extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Fragment_Setting() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Fragment_Cards.
     */
    // TODO: Rename and change types and number of parameters
    public static Fragment_Setting newInstance(String param1, String param2) {
        Fragment_Setting fragment = new Fragment_Setting();
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
        View view=inflater.inflate(R.layout.fragment_setting,container,false);
        CardView button = view.findViewById(R.id.cardStart);
        button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(getActivity(), trafficsign.class);
                startActivity(intent);
            }
        });

        CardView button2 = view.findViewById(R.id.tips);
        button2.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(getActivity(), note.class);
                startActivity(intent);
            }
        });

        CardView button3 = view.findViewById(R.id.tracuu);
        button3.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(getActivity(), WebActivity.class);
                startActivity(intent);
            }
        });

        CardView button4 = view.findViewById(R.id.luatxuphat);
        button4.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(getActivity(), LuatXP.class);
                startActivity(intent);
            }
        });
        // Inflate the layout for this fragment
        return view;
    }
}