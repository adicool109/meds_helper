package com.justapp.meds;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.WebView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.sql.SQLException;

public class DrugActivity extends Activity {
    static String drugInfo = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        String title = getIntent().getExtras().getString("title");
        int selectedId = getIntent().getExtras().getInt("id");

        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.drug_info);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.window_title);

        final LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View curtain = inflater.inflate(R.layout.curtain, null);
        curtain.setVisibility(View.VISIBLE);
        addContentView(curtain, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));


        DBHelper myDbHelper = new DBHelper(this);
        myDbHelper = new DBHelper(this);
        try {
            myDbHelper.createDataBase();
        } catch (IOException ioe) {
            throw new Error("Unable to create database");
        }
        try {
            myDbHelper.openDataBase();

            drugInfo = myDbHelper.getDrugInfoById(selectedId);

            myDbHelper.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        //TextView drugTitle = (TextView) findViewById(R.id.drugName);
        //drugTitle.setText(title);
        TextView headerText = (TextView) findViewById(R.id.headerTitle);
        headerText.setText(title);

        WebView drugInfoContainer = (WebView) findViewById(R.id.drugInfoWebView);
        drugInfoContainer.getSettings().setJavaScriptEnabled(true);
        String header = "<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head>\n" +
                "    <meta content=\"text/html; charset=utf-8\" http-equiv=\"Content-Type\" />\n" +
                "</head>\n" +
                "<body style='background-color:#fefdfb'><p style='font-size:5px;'>&nbsp</p>\n" +
                drugInfo +
                "</body>\n" +
                "</html>";
            drugInfoContainer.loadDataWithBaseURL("",header,"text/html","utf-8",null);
    }
    public void headerSearchButtonClicked(View view) {
        Toast.makeText(this, "TODO", Toast.LENGTH_LONG).show();
    }
}
