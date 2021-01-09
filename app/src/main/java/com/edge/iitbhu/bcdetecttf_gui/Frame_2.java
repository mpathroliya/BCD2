package com.edge.iitbhu.bcdetecttf_gui;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.ListIterator;

import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Frame_2#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Frame_2 extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private Classifier classifier;
    private Classifier rmclassifier;
    private static final int IMAGE_PICK_CODE = 1000;
    private Button detectButton;
    private Button selectButton;

    private TextView selectAnotherButton;
    private TextView entryText;
    private TextView scoreText;
    private TextView comment;

    private ImageView imageView;
    private Boolean detectFlag = false;
    private Boolean tryFlag = false;
//    int currImageScore;
    int currScore;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Frame_2() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Frame_2.
     */
    // TODO: Rename and change types and number of parameters
    public static Frame_2 newInstance(String param1, String param2) {
        Frame_2 fragment = new Frame_2();
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_frame_2, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState){
        super.onViewCreated(view,savedInstanceState);
        view.setClipToOutline(true);
        // state 2
        imageView = view.findViewById(R.id.imageView);
        detectButton = view.findViewById(R.id.detect_button);
        selectAnotherButton = view.findViewById(R.id.select_another_button);
        scoreText = view.findViewById(R.id.score_text);
        comment = view.findViewById(R.id.comment);

        //state 1
        selectButton = view.findViewById(R.id.select_button);
        entryText = view.findViewById(R.id.this_is_mac);

        try {
            classifier = new Classifier(getActivity(),1,"labels.txt","bc_mobileNetV2.tflite");
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            rmclassifier = new Classifier(getActivity(),1,"rmlabels.txt","mmc4.tflite");
        } catch (IOException e) {
            e.printStackTrace();
        }

        selectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                selectImageFromGallery();
            }
        });

        selectAnotherButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                selectImageFromGallery();
                imageView.setVisibility(View.GONE);
                detectButton.setVisibility(View.GONE);
                selectAnotherButton.setVisibility(View.GONE);

                //set gone for state 1
                entryText.setVisibility(View.VISIBLE);
                selectButton.setVisibility(View.VISIBLE);
                comment.setVisibility(View.GONE);
            }
        });


        detectButton.setOnClickListener(new View.OnClickListener(){
            @SuppressLint("ResourceType")
            @Override
            public void onClick(View v){
                if(detectFlag || tryFlag){
                    Bitmap bitmap = convertImageViewToBitmap(imageView);

                    List<Classifier.Recognition> list = classifier.classify(bitmap,0);

                    String primary = list.get(0).getTitle();
                    Float primaryConfidence = roundOff(list.get(0).getConfidence());
                    Float secondaryConfidence = roundOff(list.get(1).getConfidence());
                    String secondary=list.get(1).getTitle();


                    NavController navController = Navigation.findNavController(v);

                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    byte[] byteArray = stream.toByteArray();

                    Bundle bundle = new Bundle();
                    bundle.putByteArray("image",byteArray);
                    bundle.putString("primary",primary);
                    bundle.putString("secondary",secondary);
                    bundle.putFloat("primaryConfidence",primaryConfidence);
                    bundle.putFloat("secondaryConfidence",secondaryConfidence);



                    navController.navigate(R.id.action_frame_22_to_frame_32,bundle);

                }
                else{
                    Toast toast1 = Toast.makeText(getActivity().getApplicationContext(), "No picture!", Toast.LENGTH_SHORT);
                    toast1.show();
                }
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        detectFlag = false;
        if(resultCode == RESULT_OK &&  requestCode == IMAGE_PICK_CODE){
            //set image to image view
            detectFlag = true;
            imageView.setImageURI(data.getData());

            // Convert it to a bitmap which will be used for inference
            Bitmap imageBitmap = convertImageViewToBitmap(imageView);
            float k = randomVsMammogramCheck(imageBitmap);
            int prscore = proximityScoreCheck(imageBitmap);
            // now we will have 4 cases as per the case we will have the comment.
            final String comm;

            if(k==0 && prscore<6){
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setMessage("This does not look like a Histopathological image, choose another")
                        .setTitle("Random image detected")
                        .setPositiveButton("retry", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // CANCEL

                                selectImageFromGallery();
                                imageView.setVisibility(View.GONE);
                                detectButton.setVisibility(View.GONE);
                                selectAnotherButton.setVisibility(View.GONE);

                                //set gone for state 1
                                entryText.setVisibility(View.VISIBLE);
                                selectButton.setVisibility(View.VISIBLE);
                                comment.setVisibility(View.GONE);

                            }
                        });
                // Create the AlertDialog object and return it
                AlertDialog dialog = builder.create();
                dialog.show();
            }

            else if(k==0 && prscore>=6){

                comm = "Good proximity score "+prscore+"/9 but this was not detected as a Mammogram. results might not be reliable";
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setMessage("This does not look like a Histopathological image, would you still to continue?")
                        .setTitle("Random image detected")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                // CONFIRM
                                // set visible for state 2
                                imageView.setVisibility(View.VISIBLE);
                                detectButton.setVisibility(View.VISIBLE);
                                selectAnotherButton.setVisibility(View.VISIBLE);

                                comment.setTextColor(Color.parseColor("#8B0000"));
                                comment.setText(comm);
                                comment.setVisibility(View.VISIBLE);

                                //set gone for state 1
                                entryText.setVisibility(View.GONE);
                                selectButton.setVisibility(View.GONE);
                            }
                        })
                        .setNegativeButton("retry", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // CANCEL

                                selectImageFromGallery();
                                imageView.setVisibility(View.GONE);
                                detectButton.setVisibility(View.GONE);
                                selectAnotherButton.setVisibility(View.GONE);

                                //set gone for state 1
                                entryText.setVisibility(View.VISIBLE);
                                selectButton.setVisibility(View.VISIBLE);
                                comment.setVisibility(View.GONE);

                            }
                        });
                // Create the AlertDialog object and return it
                AlertDialog dialog = builder.create();
                dialog.show();
            }
            else if(k==1 && prscore<6){

                comm = "Poor proximity score "+prscore+"/9 but this was detected as a Mammogram. results might not be reliable";
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setMessage("This does not look like a Histopathological image, would you still to continue?")
                        .setTitle("Random image detected")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                // CONFIRM
                                // set visible for state 2
                                imageView.setVisibility(View.VISIBLE);
                                detectButton.setVisibility(View.VISIBLE);
                                selectAnotherButton.setVisibility(View.VISIBLE);

                                comment.setTextColor(Color.parseColor("#8B0000"));
                                comment.setText(comm);
                                comment.setVisibility(View.VISIBLE);

                                //set gone for state 1
                                entryText.setVisibility(View.GONE);
                                selectButton.setVisibility(View.GONE);
                            }
                        })
                        .setNegativeButton("retry", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // CANCEL

                                selectImageFromGallery();
                                imageView.setVisibility(View.GONE);
                                detectButton.setVisibility(View.GONE);
                                selectAnotherButton.setVisibility(View.GONE);

                                //set gone for state 1
                                entryText.setVisibility(View.VISIBLE);
                                selectButton.setVisibility(View.VISIBLE);
                                comment.setVisibility(View.GONE);

                            }
                        });
                // Create the AlertDialog object and return it
                AlertDialog dialog = builder.create();
                dialog.show();
            }

            else{
                comm =  "Good proximity score "+prscore+"/9 you can go ahead!";

                imageView.setVisibility(View.VISIBLE);
                detectButton.setVisibility(View.VISIBLE);
                selectAnotherButton.setVisibility(View.VISIBLE);

                comment.setTextColor(Color.parseColor("#008000"));
                comment.setText(comm);
                comment.setVisibility(View.VISIBLE);

                //set gone for state 1
                entryText.setVisibility(View.GONE);
                selectButton.setVisibility(View.GONE);
            }

        }

    }

    private float randomVsMammogramCheck(Bitmap bitmap){
        List<Classifier.Recognition> list = rmclassifier.classify(bitmap,0);
        int score;
        String primary = list.get(0).getTitle();
        if(primary.equals("Random")){
            score =  0;
        }
        else{
            Float primaryConfidence = roundOff(list.get(0).getConfidence());
            if(primaryConfidence < 98) score = 0;
            else
                score = 1;
        }
        //Toast toast1 = Toast.makeText(getActivity().getApplicationContext(), primary, Toast.LENGTH_SHORT);
        //toast1.show();

        currScore =  score;
        return score;

    }

    private Bitmap convertImageViewToBitmap(ImageView iView){
        BitmapDrawable drawable = (BitmapDrawable) iView.getDrawable();
        Bitmap bitmap = drawable.getBitmap();
        return bitmap;
    }

    private int proximityScoreCheck(Bitmap bitmap){
        int score =0;
        double[] mean = {195.8494419642857, 163.7113887117348, 195.12751992984695};
        double[] stdDev = {12.595864429286115, 32.17582772629138, 13.283874465175264};
        double[] img = getPixelMean(bitmap);
        for(int color = 0; color<3;color+=1 ){
            double diff = Math.abs(img[color] - mean[color]);
            if(diff < stdDev[color]){
                score+=3;
            }
            else if (diff < 2*stdDev[color]){
                score+=2;
            }
            else if (diff < 3*stdDev[color]){
                score+=1;
            }
        }

        return score;
    }

    private double[] getPixelMean(Bitmap bitmap){
        bitmap = Bitmap.createScaledBitmap(bitmap,224,224,true);
        double redColors = 0;
        double greenColors = 0;
        double blueColors = 0;
        double pixelCount = 0;

        for (int y = 0; y < bitmap.getHeight(); y++)
        {
            for (int x = 0; x < bitmap.getWidth(); x++)
            {
                int c = bitmap.getPixel(x, y);
                pixelCount++;
                redColors += Color.red(c);
                greenColors += Color.green(c);
                blueColors += Color.blue(c);
            }
        }
        double red = (redColors/pixelCount);
        double green = (greenColors/pixelCount);
        double blue = (blueColors/pixelCount);
        double[] imgArr =  {red,green,blue};
        return imgArr;
    }

    private void selectImageFromGallery(){
        // Create a new intent to pick image for external app
        Intent imageIntent =new Intent(Intent.ACTION_PICK);
        // set type of file being excepted, image in our case, hence it opens gallery
        imageIntent.setType("image/*");
        // Run the activity for when we've picked the image
        if(imageIntent.resolveActivity(getActivity().getPackageManager())!=null){
            startActivityForResult(imageIntent,IMAGE_PICK_CODE);
        }
    }

    private Float roundOff(Float val){
        val = val*10000;
        int temp = Math.round(val);
        val = (float) temp;
        val =val/100;
        return val;
    }
}
