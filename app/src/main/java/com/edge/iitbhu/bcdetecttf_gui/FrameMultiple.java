package com.edge.iitbhu.bcdetecttf_gui;
import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Html;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.google.protobuf.Empty;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOError;
import java.io.IOException;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FrameMultiple#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FrameMultiple extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private int imgcount;
    private Classifier classifier;
    private List<ImageView> m;
    private TextView summaryText;
    private Button backButton;
    private ImageView iconView;
    private List<TextView> res;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private String s1,s2;
    private String reportName,reportId, patient;

    private List<Bitmap> imageList;
    private Button saveButton;

    public FrameMultiple() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FrameMultiple.
     */
    // TODO: Rename and change types and number of parameters
    private static FrameMultiple newInstance(String param1, String param2) {
        FrameMultiple fragment = new FrameMultiple();
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
        return inflater.inflate(R.layout.fragment_frame_multiple, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState){
        super.onViewCreated(view,savedInstanceState);

        iconView = view.findViewById(R.id.icon_bc);
        m = new ArrayList<>();
        res = new ArrayList<>();

        imgcount = BcUtils.get().getImageCount();
        imageList = BcUtils.get().getImageList();

        // define view.
        summaryText = view.findViewById(R.id.summary);
        backButton = view.findViewById(R.id.main_back_button);
        ImageView mx = view.findViewById(R.id.multi_im_1); m.add(mx);
        mx = view.findViewById(R.id.multi_im_2); m.add(mx);
        mx = view.findViewById(R.id.multi_im_3); m.add(mx);
        mx = view.findViewById(R.id.multi_im_4); m.add(mx);
        mx = view.findViewById(R.id.multi_im_5); m.add(mx);
        for(int i=0;i<5;i++) m.get(i).setVisibility(View.GONE);

        TextView resx = view.findViewById(R.id.res1); res.add(resx);
        resx = view.findViewById(R.id.res2); res.add(resx);
        resx = view.findViewById(R.id.res3); res.add(resx);
        resx = view.findViewById(R.id.res4); res.add(resx);
        resx = view.findViewById(R.id.res5); res.add(resx);

        saveButton = view.findViewById(R.id.save_button);
        for(int i=0;i<5;i++) res.get(i).setVisibility(View.GONE);

        //set images
        for(int i=0;i<imgcount;i++){
            m.get(i).setImageBitmap(imageList.get(i));
            BcUtils.get().scaleForDisplay(m.get(i));
            m.get(i).setVisibility(View.VISIBLE);
        }

        //Run Classifier
        try {
            classifier = new Classifier(getActivity(),1,"labels.txt","model_5.1.tflite");
        } catch (IOException e) {
            e.printStackTrace();
        }
        setResults();


        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityCompat.requestPermissions(getActivity(),new String[]{
                        Manifest.permission.WRITE_EXTERNAL_STORAGE}, PackageManager.PERMISSION_GRANTED);
                reportId = getTime();
                makeReport();
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavController navController = Navigation.findNavController(v);
                navController.navigate(R.id.action_frameMultiple_to_frame_22);
            }
        });
    }

    private void makeReport(){
//        createPDF();
        reportName = "";
        final EditText patientName = new EditText(getContext());
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        builder.setMessage("Add Patient's Name")
                .setView(patientName)
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        patient = patientName.getText().toString();
                        reportName = reportId +"_"+patient+".pdf";
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

    private void createPDF() {
        PdfDocument myPdfDocument = new PdfDocument();


        PdfDocument.PageInfo myPageInfo1 = new PdfDocument.PageInfo.Builder(879, 1240 ,1).create();
        PdfDocument.Page myPage1 = myPdfDocument.startPage(myPageInfo1);
        Canvas canvas = myPage1.getCanvas();
        canvas = drawReport(canvas);
        myPdfDocument.finishPage(myPage1);

        File file = new File(Environment.getExternalStorageDirectory()+ "/" + "BC Reports",reportName);
        try{
            myPdfDocument.writeTo(new FileOutputStream(file));
            Toast toast1 = Toast.makeText(getActivity().getApplicationContext(), "Saved to BC Reports as \n"+reportName, Toast.LENGTH_SHORT);
            toast1.show();
        } catch(IOException e){
//            e.printStackTrace();
            Log.d("Pdf Error", e.getMessage());
            Toast toast1 = Toast.makeText(getActivity().getApplicationContext(), "Couldn't save", Toast.LENGTH_SHORT);
            toast1.show();
        }
        myPdfDocument.close();
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
        canvas.drawText("Patient: ",80,260,credTitles);
        canvas.drawText("Diagnosis Id:",400,200,credTitles);
        canvas.drawText("Diagnosis Summary: ",400,230,credTitles);

        // cred text
        Paint credText = new Paint();
        credText.setTextSize(20);
        credText.setColor(Color.parseColor("#686868"));
        credText.setTypeface(Typeface.DEFAULT);
        canvas.drawText(BcUtils.get().getfName()+" "+BcUtils.get().getlName(),150,200,credText);
        canvas.drawText(BcUtils.get().getEmail(),150,230,credText);
        canvas.drawText(reportId,630,200,credText);
        canvas.drawText(s1,630,230,credText);
        canvas.drawText(patient,160,260,credText);
        canvas.drawText(s2,630,250,credText);


        //images start  at 220
        Paint picPaint = new Paint();
        picPaint.setTextSize(18);
        picPaint.setColor(Color.parseColor("#2C85FF"));
        if(0<imgcount){
            canvas.drawBitmap(BcUtils.get().convertImageViewToBitmap(m.get(0)),80,295,picPaint);
//            canvas.drawBitmap(icon,80,295,picPaint);
            canvas.drawText(res.get(0).getText().toString(),85,545,picPaint);
        }
        if(1<imgcount){
            canvas.drawBitmap(BcUtils.get().convertImageViewToBitmap(m.get(1)),450,295,picPaint);
            canvas.drawText(res.get(1).getText().toString(),455,545,picPaint);
        }
        if(2<imgcount){
            canvas.drawBitmap(BcUtils.get().convertImageViewToBitmap(m.get(2)),80,595,picPaint);
            canvas.drawText(res.get(2).getText().toString(),85,845,picPaint);
        }
        if(3<imgcount){
            canvas.drawBitmap(BcUtils.get().convertImageViewToBitmap(m.get(3)),450,595,picPaint);
            canvas.drawText(res.get(3).getText().toString(),455,845,picPaint);
        }
        if(4<imgcount){
            canvas.drawBitmap(BcUtils.get().convertImageViewToBitmap(m.get(4)),80,895,picPaint);
            canvas.drawText(res.get(4).getText().toString(),85,1145,picPaint);
        }
        float stop_y =0;
        if((imgcount-1)/2==0) stop_y = 560;
        else if((imgcount-1)/2==1) stop_y = 860;
        else stop_y = 1160;

        Paint linePaint = new Paint();

        linePaint.setColor(Color.parseColor("#686868"));
        canvas.drawLine(70,120,810,120,linePaint);
        canvas.drawLine(70,stop_y,810,stop_y,linePaint);

        return canvas;
    };

    private String getTime(){
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());

        return timeStamp;
    }

    private void setResults(){
        int benignCount =0;
        for(int i=0;i<imgcount;i++){
            List<Classifier.Recognition> list = classifier.classify(imageList.get(i),0);

            String primary = list.get(0).getTitle();
            Float primaryConfidence = BcUtils.get().roundOff(list.get(0).getConfidence());

            if(primary.equals("Benign")) benignCount+=1;

            primary = "<font color=#5A5A5A>"+(i+1)+ " : " + primary  +"</font> - <font color=#4295FF>" + primaryConfidence + "% </font>";
            res.get(i).setText(Html.fromHtml(primary));
            res.get(i).setVisibility(View.VISIBLE);
        }
        String summ;
        if(benignCount>=imgcount/2){
            summ =  "Benign: " +"<font color=#4899FF size=35sp >" +benignCount + "/"+imgcount+"</font>";
            summ += "\n Malignant: "+ "<font color=#4899FF size=35sp >" + (imgcount-benignCount) + "/"+imgcount +"</font>";
            s1 =  "Benign: "+ benignCount+"/"+imgcount;
            s2 = "Malignant: "+ (imgcount-benignCount) +"/"+imgcount;
        }
        else{
            summ = "Malignant: "+ "<font color=#4899FF size=35sp >" +(imgcount-benignCount) + "/"+imgcount +"</font>";
            summ += "\n Benign: " +"<font color=#4899FF size=35sp >" +benignCount + "/"+imgcount+"</font>";
            s2 =  "Benign: "+ benignCount+"/"+imgcount;
            s1 = "Malignant: "+ (imgcount-benignCount) +"/"+imgcount;
        }
        summaryText.setText(Html.fromHtml(summ));

    }





}
