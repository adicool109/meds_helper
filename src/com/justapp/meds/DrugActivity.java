package com.justapp.meds;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;
import android.widget.TextView;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.sql.SQLException;

public class DrugActivity extends Activity {
    static String drugInfo = new String();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        String title = getIntent().getExtras().getString("title");
        int selectedId = getIntent().getExtras().getInt("id");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.drug_info);
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
        TextView drugTitle = (TextView) findViewById(R.id.drugName);
        drugTitle.setText(title);
        WebView drugInfoContainer = (WebView) findViewById(R.id.drugInfoWebView);
        drugInfoContainer.getSettings().setJavaScriptEnabled(true);
        String header = "<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head>\n" +
                "    <meta content=\"text/html; charset=utf-8\" http-equiv=\"Content-Type\" />\n" +
                "</head>\n" +
                "<body>\n" +
                drugInfo +
                "</body>\n" +
                "</html>";
            drugInfoContainer.loadDataWithBaseURL("",header,"text/html","utf-8",null);
    }
}
