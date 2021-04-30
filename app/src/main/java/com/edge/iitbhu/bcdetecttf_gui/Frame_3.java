package com.edge.iitbhu.bcdetecttf_gui;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.fonts.FontStyle;
import android.util.Log;
import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.DataOutputStream;
import java.net.*;

import java.text.SimpleDateFormat;
import java.util.Calendar;
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
    private Button saveButton;
    private String reportName;


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
        saveButton = view.findViewById(R.id.save_button);

        imageView.setImageBitmap(bmp);
        resPrimary.setText(primary);
        resSecondary.setText(secondary);
        confPrimary.setText(Float.toString(primaryConfidence)+" %");
        confSecondary.setText(Float.toString(secondaryConfidence)+" %");

        saveButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
//                Toast toast1 = Toast.makeText(getActivity().getApplicationContext(), "Save button works!", Toast.LENGTH_SHORT);
//                toast1.show();

                ActivityCompat.requestPermissions(getActivity(),new String[]{
                        Manifest.permission.WRITE_EXTERNAL_STORAGE}, PackageManager.PERMISSION_GRANTED);
//                if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
//                    makeReport();
//                }
//                else{
//                    Toast toast1 = Toast.makeText(getActivity().getApplicationContext(), "Write permission not granted!", Toast.LENGTH_SHORT);
//                    toast1.show();
//                }


                makeReport();
            }
        });
    }

    // write the alert box for assigning name.
    private void makeReport(){
//        createPDF();
        reportName = "";
        final EditText fileName = new EditText(getContext());
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        builder.setTitle("Save as")
                .setMessage("Choose file name")
                .setView(fileName)
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        reportName = fileName.getText().toString()+".pdf";
//                        Toast toast1 = Toast.makeText(getActivity().getApplicationContext(), reportName, Toast.LENGTH_SHORT);
//                        toast1.show();
                        createPDF();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Write your code here to execute after dialog
                        dialog.cancel();
                    }
                });
        // Create the AlertDialog object and return it
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private Canvas drawReport(Canvas canvas){
//        to do


        // Defining all the paints

        Paint titlePaint = new Paint();
        //title
        titlePaint.setTextSize(35);
        titlePaint.setTypeface(Typeface.SERIF);
        titlePaint.setColor(Color.parseColor("#2C85FF"));
        canvas. drawText("Inference Report",80,100,titlePaint);

        //Diagnostics by
        Paint diagPaint = new Paint();
        diagPaint.setColor(Color.parseColor("#686868"));
        diagPaint.setTextSize(22);
        canvas.drawText("Diagnostics by: ",60,170,diagPaint);

        // cred titles
        Paint credTitles = new Paint();
        credTitles.setColor(Color.parseColor("#2C85FF"));
        credTitles.setTextSize(20);
        credTitles.setTypeface(Typeface.DEFAULT_BOLD);
        canvas.drawText("Name: ",80,200,credTitles);
        canvas.drawText("Email: ",80,230,credTitles);
        canvas.drawText("Diagnosis Id:",400,200,credTitles);

        // cred text
        Paint credText = new Paint();
        credText.setTextSize(20);
        credText.setColor(Color.parseColor("#686868"));
        credText.setTypeface(Typeface.DEFAULT);
        canvas.drawText(BcUtils.get().getfName()+" "+BcUtils.get().getlName(),150,200,credText);
        canvas.drawText(BcUtils.get().getEmail(),150,230,credText);
        canvas.drawText(getTime(),630,200,credText);


        //images start  at 220
        Paint picPaint = new Paint();
        picPaint.setTextSize(21);
        picPaint.setTypeface(Typeface.DEFAULT_BOLD);
        picPaint.setColor(Color.parseColor("#686868"));

        canvas.drawBitmap(BcUtils.get().convertImageViewToBitmap(imageView),80,295,picPaint);
        canvas.drawText(resPrimary.getText().toString() + " " + confPrimary.getText().toString(),400,305,picPaint);


        float stop_y = 560;


        Paint linePaint = new Paint();

        linePaint.setColor(Color.parseColor("#686868"));
        canvas.drawLine(70,120,810,120,linePaint);
        canvas.drawLine(70,stop_y,810,stop_y,linePaint);

        return canvas;
    };

    private void createPDF() {
        PdfDocument myPdfDocument = new PdfDocument();


        PdfDocument.PageInfo myPageInfo1 = new PdfDocument.PageInfo.Builder(879, 1240 ,1).create();
        PdfDocument.Page myPage1 = myPdfDocument.startPage(myPageInfo1);
        Canvas canvas = myPage1.getCanvas();
        canvas = drawReport(canvas);
        myPdfDocument.finishPage(myPage1);

        File file = new File(Environment.getExternalStorageDirectory(),reportName);
        try{
            myPdfDocument.writeTo(new FileOutputStream(file));
            Toast toast1 = Toast.makeText(getActivity().getApplicationContext(), "Saved", Toast.LENGTH_SHORT);
            toast1.show();
        } catch(IOException e){
//            e.printStackTrace();
            Log.d("Pdf Error", e.getMessage());
            Toast toast1 = Toast.makeText(getActivity().getApplicationContext(), "Couldn't save", Toast.LENGTH_SHORT);
            toast1.show();
        }
        myPdfDocument.close();
    }

    private static Paint setTextSizeForWidth(Paint paint, float desiredWidth,
                                            String text) {

        // Pick a reasonably large value for the test. Larger values produce
        // more accurate results, but may cause problems with hardware
        // acceleration. But there are workarounds for that, too; refer to
        // http://stackoverflow.com/questions/6253528/font-size-too-large-to-fit-in-cache
        final float testTextSize = 48f;

        // Get the bounds of the text, using our testTextSize.
        paint.setTextSize(testTextSize);
        Rect bounds = new Rect();
        paint.getTextBounds(text, 0, text.length(), bounds);

        // Calculate the desired size as a proportion of our testTextSize.
        float desiredTextSize = testTextSize * desiredWidth / bounds.width();

        // Set the paint for that size.
        paint.setTextSize(desiredTextSize);
        return paint;
    }

    private String getTime(){
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());

        return timeStamp;
    }

}
