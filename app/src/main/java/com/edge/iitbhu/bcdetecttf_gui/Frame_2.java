package com.edge.iitbhu.bcdetecttf_gui;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.DialogInterface;
import android.net.Uri;
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


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.lang.Math;
import java.util.Objects;


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

    private Classifier classifier,  rmclassifier;
    private static final int IMAGE_PICK_CODE = 1000;
    private Button detectButton, selectButton, multiple;


    private TextView selectAnotherButton, entryText, comment, nameView, nSelected;
    private TextView aboutButton;

    private ImageView imageView;
    private List<ImageView> m;
    private List<TextView> cmt;
    private String commie, userId;

    private Boolean detectFlag = false, tryFlag = false, multiFlag =false;
    private List<Bitmap> imageList;
    public  List<String> imageUris;

    public static final int REQUEST_IMAGE_CAPTURE = 1;
    public static final int PICK_IMAGES = 2;
    public int imgCount;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;



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

        imageUris = new ArrayList<String>();
        nSelected = view.findViewById(R.id.n_selected);
        detectButton = view.findViewById(R.id.detect_button);
        detectButton.setText("Predict");
        selectAnotherButton = view.findViewById(R.id.select_another_button);
        selectAnotherButton.setText("New Test");

        multiple = view.findViewById(R.id.multiple);
        //for image scrollview
        imageList = new ArrayList<Bitmap>();
        m = new ArrayList<ImageView>();
//        ImageView mx;
        imageView = view.findViewById(R.id.multi_im_1); m.add(imageView);
        imageView = view.findViewById(R.id.multi_im_2); m.add(imageView);
        imageView = view.findViewById(R.id.multi_im_3); m.add(imageView);
        imageView = view.findViewById(R.id.multi_im_4); m.add(imageView);
        imageView = view.findViewById(R.id.multi_im_5); m.add(imageView);
        imageView = view.findViewById(R.id.imageView);
        nameView = view.findViewById(R.id.full_name);

        //for comment scrollview
        cmt = new ArrayList<TextView>();
        comment = view.findViewById(R.id.comm_1); cmt.add(comment);
        comment = view.findViewById(R.id.comm_2); cmt.add(comment);
        comment = view.findViewById(R.id.comm_3); cmt.add(comment);
        comment = view.findViewById(R.id.comm_4); cmt.add(comment);
        comment = view.findViewById(R.id.comm_5); cmt.add(comment);
        comment = view.findViewById(R.id.comment);


        //state 1
        selectButton = view.findViewById(R.id.select_button);
        aboutButton =  view.findViewById(R.id.about_button);
        entryText = view.findViewById(R.id.this_is_mac);

        //firebase

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        String userId = fAuth.getCurrentUser().getUid();
        DocumentReference docRef = fStore.collection("users").document(userId);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot document = task.getResult();
                String firstName = document.getString("fName");
                String lastName = document.getString("lName");
                String emailId = document.getString("email");
                BcUtils.get().setfName(firstName);
                nameView.setText("Welcome\n"+BcUtils.get().getfName()+" !");
                BcUtils.get().setlName(lastName);
                BcUtils.get().setEmail(emailId);
            }
        });

        //classifier
        try {
            classifier = new Classifier(getActivity(),1,"labels.txt","model_5.1.tflite");
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
                multiFlag = false;

                selectImageFromGallery();
            }
        });

        multiple.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                multiFlag = true;
                getPickImageIntent();
            }
        });

        selectAnotherButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                getPickImageIntent();
            }
        });


        aboutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity().getApplicationContext()," helloo" , Toast.LENGTH_SHORT).show();
                NavController navController = Navigation.findNavController(v);
                navController.navigate(R.id.action_frame_22_to_frame_12);

            }
        });


        detectButton.setOnClickListener(new View.OnClickListener(){
            @SuppressLint("ResourceType")
            @Override
            public void onClick(View v){
                if(multiFlag==false){
                    if(detectFlag || tryFlag){
                        Bitmap bitmap = BcUtils.get().convertImageViewToBitmap(imageView);

                        List<Classifier.Recognition> list = classifier.classify(bitmap,0);

                        String primary = list.get(0).getTitle();
                        Float primaryConfidence = BcUtils.get().roundOff(list.get(0).getConfidence());
                        Float secondaryConfidence = BcUtils.get().roundOff(list.get(1).getConfidence());
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
                else{
//                    Toast toast1 = Toast.makeText(getActivity().getApplicationContext(), " Multiple selected", Toast.LENGTH_SHORT);
//                    toast1.show();
                    NavController navController = Navigation.findNavController(v);
                    navController.navigate(R.id.action_frame_22_to_frameMultiple);

                }
            }
        });
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        detectFlag = false;
        if(resultCode == RESULT_OK){
            BcUtils.get().clearImageList();
            if(requestCode == IMAGE_PICK_CODE){
                ActivityResultSingleImage(data);
            }
            if(requestCode == PICK_IMAGES){
                ActivityResultMultipleImage(data);
            }
            if(imgCount==1) nSelected.setText("1 image Selected");
            else nSelected.setText(imgCount +" images selected");
        }

    }


    public void ActivityResultSingleImage(@Nullable Intent data){
        //set image to image view
        detectFlag = true;
        imageView.setImageURI(data.getData());
        nSelected.setVisibility(View.INVISIBLE);

        // Convert it to a bitmap which will be used for inference
        Bitmap imageBitmap = BcUtils.get().convertImageViewToBitmap(imageView);
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
                            selectImageFromGallery();
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
                            comment.setTextColor(Color.parseColor("#8B0000"));
                            comment.setText(comm);
                            setState2Multiple(false);
                            setState1(false);
                            setState2Single(true);
                            BcUtils.get().scaleForDisplay(imageView);

                        }
                    })
                    .setNegativeButton("retry", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // CANCEL
                            selectImageFromGallery();
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
                            comment.setTextColor(Color.parseColor("#8B0000"));
                            comment.setText(comm);

                            setState1(false);
                            setState2Multiple(false);
                            setState2Single(true);
                            BcUtils.get().scaleForDisplay(imageView);
                        }
                    })
                    .setNegativeButton("retry", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // CANCEL

                            selectImageFromGallery();
                        }
                    });
            // Create the AlertDialog object and return it
            AlertDialog dialog = builder.create();
            dialog.show();
        }
        else{

            comm =  "Good proximity score "+prscore+"/9 you can go ahead!";
            comment.setTextColor(Color.parseColor("#008000"));
            comment.setText(comm);
            setState1(false);
            setState2Multiple(false);
            setState2Single(true);

            BcUtils.get().scaleForDisplay(imageView);
        }
    }

    public void ActivityResultMultipleImage(@Nullable Intent data){
        if(data.getClipData() != null){

            ClipData mClipData = data.getClipData();

//             adding bitmaps to imageList
            imageList = new ArrayList<Bitmap>();
            imgCount = Math.min(5,mClipData.getItemCount());
            if(mClipData.getItemCount()!=imgCount){
                Toast toast1 = Toast.makeText(
                        getActivity().getApplicationContext(),
                        "App allows only 5 images,\n First 5 images selected",
                        Toast.LENGTH_LONG
                );
                toast1.show();
            }
            BcUtils.get().setImageCount(imgCount);
            for (int i=0;i<imgCount;i++){
                ClipData.Item item = mClipData.getItemAt(i);
                Uri uri = item.getUri();
                m.get(i).setImageURI(uri);
                BcUtils.get().scaleForDisplay(m.get(i));
                Bitmap bitmap = BcUtils.get().convertImageViewToBitmap(m.get(i));
                imageList.add(bitmap);
                BcUtils.get().addImageToList(bitmap);

            }
//            mClipData.getItemAt(0).getU

            // find scores for both
            List<Integer> scoreCheck = new ArrayList<Integer>();
            final List<Integer> prscoreList = new ArrayList<Integer>();
            final List<Float>kList = new ArrayList<Float>();
            for(int i=0;i<imgCount;i++){
                int prscore = proximityScoreCheck(imageList.get(i));
                prscoreList.add(prscore);
                Float k = randomVsMammogramCheck(imageList.get(i));
                kList.add(k);

                if(k==0 && prscore<6) scoreCheck.add(-1);
                else if(k==1 && prscore>=6) scoreCheck.add(1);
                else scoreCheck.add(0);
            }

            Boolean negFlag = false;
            String zeroScoreImNames = "";

            for(int i=0;i<imgCount;i++){
                if(scoreCheck.get(i)==-1){
                    negFlag = true;
                    break;
                }
                else if(scoreCheck.get(i)==0) zeroScoreImNames += " "+Integer.toString(i);
            }

            // define behaviours

            if(negFlag==true){
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setMessage("This does not look like a Histopathological image, choose another")
                        .setTitle("Random image detected")
                        .setPositiveButton("retry", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                getPickImageIntent();

                            }
                        });
                // Create the AlertDialog object and return it
                AlertDialog dialog = builder.create();
                dialog.show();
            }
            else if(zeroScoreImNames.equals("")==false){
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setMessage("Image"+ zeroScoreImNames + " do not look like a Histopathological image, would you still to continue?")
                        .setTitle("Random image detected")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                // CONFIRM
                                // set visible for state 2

                                for(int i=0;i<imgCount;i++){
                                    String comm;
                                    if(kList.get(i)==1 && prscoreList.get(i)<6){
                                        comm = "Poor proximity score "+prscoreList.get(i)+"/9 but this was detected as a Mammogram. results might not be reliable";
                                        comment.setTextColor(Color.parseColor("#8B0000"));
                                    }
                                    else if(kList.get(i)==0 && prscoreList.get(i)>=6){
                                        comm = "Good proximity score "+prscoreList.get(i)+"/9 but this was not detected as a Mammogram. results might not be reliable";
                                        comment.setTextColor(Color.parseColor("#8B0000"));
                                    }
                                    else{
                                        comm =  "Good proximity score "+prscoreList.get(i)+"/9 ";
                                        cmt.get(i).setTextColor(Color.parseColor("#008000"));

                                    }
                                    cmt.get(i).setText(comm);
                                }
                                setState2Single(false);
                                setState1(false);
                                setState2Multiple(true);
                            }
                        })
                        .setNegativeButton("retry", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // CANCEL

                                getPickImageIntent();
                                //set gone for state 2
                            }
                        });
                // Create the AlertDialog object and return it
                AlertDialog dialog = builder.create();
                dialog.show();
            }
            else{
                // set visible for state 2
                for(int i=0;i<imgCount;i++){
                    String comm;
                    if(kList.get(i)==1 && prscoreList.get(i)<6){
                        comm = "Poor proximity score "+prscoreList.get(i)+"/9 but this was detected as a Mammogram. results might not be reliable";
                        comment.setTextColor(Color.parseColor("#8B0000"));
                    }
                    else if(kList.get(i)==0 && prscoreList.get(i)>=6){
                        comm = "Good proximity score "+prscoreList.get(i)+"/9 but this was not detected as a Mammogram. results might not be reliable";
                        comment.setTextColor(Color.parseColor("#8B0000"));
                    }
                    else{
                        comm =  "Good proximity score "+prscoreList.get(i)+"/9 ";
                        cmt.get(i).setTextColor(Color.parseColor("#008000"));

                    }
                    cmt.get(i).setText(comm);
                }
                setState2Single(false);
                setState1(false);
                setState2Multiple(true);

            }
        }


        else if (data.getData() != null){
            multiFlag = false;
            ActivityResultSingleImage(data);
//            Toast toast1 = Toast.makeText(getActivity().getApplicationContext(), " only one image", Toast.LENGTH_SHORT);
//            toast1.show();
        }
        else {
            Toast toast1 = Toast.makeText(getActivity().getApplicationContext(), " no image", Toast.LENGTH_SHORT);
            toast1.show();
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
            Float primaryConfidence = BcUtils.get().roundOff(list.get(0).getConfidence());
            if(primaryConfidence < 98) score = 0;
            else
                score = 1;
        }
        //Toast toast1 = Toast.makeText(getActivity().getApplicationContext(), primary, Toast.LENGTH_SHORT);
        //toast1.show();

        currScore =  score;
        return score;

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

    public void getPickImageIntent(){
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        if(intent.resolveActivity(getActivity().getPackageManager())!=null) {
            startActivityForResult(intent, PICK_IMAGES);
        }
    }

    private void setState2Single(boolean x){
        if(x==true){
            imageView.setVisibility(View.VISIBLE);
            detectButton.setVisibility(View.VISIBLE);
            selectAnotherButton.setVisibility(View.VISIBLE);
            comment.setVisibility(View.VISIBLE);
        }
        else{
            imageView.setVisibility(View.GONE);
            detectButton.setVisibility(View.GONE);
            selectAnotherButton.setVisibility(View.GONE);
            comment.setVisibility(View.GONE);
            for(int i=0;i<5;i++){
                m.get(i).setVisibility(View.GONE);
                cmt.get(i).setVisibility(View.GONE);
            }
            nSelected.setVisibility(View.GONE);
        }
    }

    private void setState2Multiple(boolean x){
        if(x==true){
            for(int i=0;i<imgCount;i++){
                m.get(i).setVisibility(View.VISIBLE);
                cmt.get(i).setVisibility(View.VISIBLE);
            }
            detectButton.setVisibility(View.VISIBLE);
            selectAnotherButton.setVisibility(View.VISIBLE);
            nSelected.setVisibility(View.VISIBLE);
        }
        else{
            imageView.setVisibility(View.GONE);
            detectButton.setVisibility(View.GONE);
            selectAnotherButton.setVisibility(View.GONE);
            comment.setVisibility(View.GONE);
            for(int i=0;i<5;i++){
                m.get(i).setVisibility(View.GONE);
                cmt.get(i).setVisibility(View.GONE);
            }
            nSelected.setVisibility(View.GONE);
        }
    }

    private void setState1(boolean x){
        if(x==true){
            entryText.setVisibility(View.VISIBLE);
            aboutButton.setVisibility(View.VISIBLE);
            selectButton.setVisibility(View.VISIBLE);
            multiple.setVisibility(View.VISIBLE);
            nameView.setVisibility(View.VISIBLE);
        }
        else{
            entryText.setVisibility(View.GONE);
            aboutButton.setVisibility(View.GONE);
            selectButton.setVisibility(View.GONE);
            multiple.setVisibility(View.GONE);
            nameView.setVisibility(View.GONE);
        }
    }

}
