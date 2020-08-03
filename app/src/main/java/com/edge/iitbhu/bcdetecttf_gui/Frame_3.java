package com.edge.iitbhu.bcdetecttf_gui;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Frame_3#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Frame_3 extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "primary";
    private static final String ARG_PARAM2 = "secondary";
    private static final String ARG_PARAM3 = "primaryConfidence";
    private static final String ARG_PARAM4 = "secondaryConfidence";
    private static final String ARG_PARAM5 = "image";
    TextView resPrimary;
    TextView resSecondary;
    TextView confPrimary;
    TextView confSecondary;
    byte[] imageArray;
    ImageView imageView;


    // TODO: Rename and change types of parameters
    private String primary;
    private String secondary;
    private Float primaryConfidence;
    private Float secondaryConfidence;
    private Bitmap bmp;

    public Frame_3() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Frame_3.
     */
    // TODO: Rename and change types and number of parameters
    public static Frame_3 newInstance(String param1, String param2, Float param3, Float param4, byte[] param5) {
        Frame_3 fragment = new Frame_3();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        args.putFloat(ARG_PARAM3, param3);
        args.putFloat(ARG_PARAM4, param4);
        args.putByteArray(ARG_PARAM5,param5);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            primary = getArguments().getString(ARG_PARAM1);
            secondary = getArguments().getString(ARG_PARAM2);
            primaryConfidence = getArguments().getFloat(ARG_PARAM3);
            secondaryConfidence = getArguments().getFloat(ARG_PARAM4);
            imageArray = getArguments().getByteArray("image");
            bmp = BitmapFactory.decodeByteArray(imageArray, 0, imageArray.length);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_frame_3, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState){
        super.onViewCreated(view,savedInstanceState);
        resPrimary = view.findViewById(R.id.res_primary);
        resSecondary = view.findViewById(R.id.res_secondary);
        confPrimary = view.findViewById(R.id.conf_primary);
        confSecondary = view.findViewById(R.id.conf_secondary);
        imageView = view.findViewById(R.id.imageView);

        imageView.setImageBitmap(bmp);
        resPrimary.setText(primary);
        resSecondary.setText(secondary);
        confPrimary.setText(Float.toString(primaryConfidence)+" %");
        confSecondary.setText(Float.toString(secondaryConfidence)+" %");
//        selectButton = view.findViewById(R.id.select_button);
//
//        selectButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v){
//                NavController navController = Navigation.findNavController(v);
//                navController.navigate(R.id.action_frame_12_to_frame_22);
//            }
//        });
    }
}
