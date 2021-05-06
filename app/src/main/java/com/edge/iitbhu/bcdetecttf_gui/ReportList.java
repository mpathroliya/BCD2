package com.edge.iitbhu.bcdetecttf_gui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

public class ReportList extends AppCompatActivity {
    private LinearLayout LL1;
    private TextView emptyTview;
    private Button backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_list);
        backButton = findViewById(R.id.report_back_button);

        LL1 = findViewById(R.id.ll_1);
        emptyTview = findViewById(R.id.empty_tview);

        loadReports();

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Main2Activity.class));
                finish();
            }
        });


    }

    private void loadReports(){

        File baseDir = new File(Environment.getExternalStorageDirectory(),"BC Reports");
        //if folder is not found
        if (!baseDir.exists()) {
            Toast.makeText(this, " No folder", Toast.LENGTH_SHORT).show();
            emptyTview.setVisibility(View.VISIBLE);
        }
        else{
            File[] files = baseDir.listFiles();
            //if folder is found but no files are in it.
            if(files.length==0) {
                Toast.makeText(this, " No files", Toast.LENGTH_SHORT).show();
                emptyTview.setVisibility(View.VISIBLE);
            }
            //display all the files

            else{
//                Toast.makeText(this, Integer.toString(files.length), Toast.LENGTH_SHORT).show();
                makePdfViews(files);
            }
        }

    }

    private void makePdfViews(File[] files){
        int num  = files.length;
        for(int i=0;i<num;i++){
            Button text = new Button(this);
            text.setTextSize(20);
            text.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
            text.setTextColor(Color.parseColor("#2180FF"));
            text.setPadding(15,15,15,15);
            text.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
            String path = files[i].getName();
            text.setText((i+1)+"\t\t"+path);
            setPdfLinks(text,files[i]);
            LL1.addView(text);
        }
    }

    private void setPdfLinks(TextView textView, final File file){

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent target = new Intent(Intent.ACTION_VIEW);

//                Uri uri = Uri.fromFile(file);
                Uri uri = FileProvider.getUriForFile(ReportList.this, BuildConfig.APPLICATION_ID + ".provider",file);

                target.setDataAndType(uri,"application/pdf");
                target.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

                target.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                target.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

                try {
                    startActivity(target);
                } catch (ActivityNotFoundException e) {
                    // Instruct the user to install a PDF reader here, or something
                    Toast.makeText(getApplicationContext(), " No pdf viewer on your device", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
