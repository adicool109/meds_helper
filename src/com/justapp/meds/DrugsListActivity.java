package com.justapp.meds;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DrugsListActivity extends ListActivity {
    static List<String> allDrugsTitles = new ArrayList<String>();
    static List<Integer> allDrugsIds = new ArrayList<Integer>();
    static boolean isSubCat;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        String title = getIntent().getExtras().getString("title");
        int selectedId = getIntent().getExtras().getInt("id");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.drug_list);
        DBHelper myDbHelper = new DBHelper(this);
        myDbHelper = new DBHelper(this);

        try {
            myDbHelper.createDataBase();
        } catch (IOException ioe) {
            throw new Error("Unable to create database");
        }
        try {
            myDbHelper.openDataBase();
            allDrugsTitles = myDbHelper.getDrugsTitleById(selectedId);
            allDrugsIds = myDbHelper.getDrugsIdById(selectedId);
            myDbHelper.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        //add header to list
        ListView lv = getListView();
        LayoutInflater inflater = getLayoutInflater();
        View header = inflater.inflate(R.layout.list_header, (ViewGroup) findViewById(R.id.header_layout_root));
        TextView categoryName = (TextView) header.findViewById(R.id.categoryName);
        categoryName.setText(title);
        lv.addHeaderView(header, null, false);
//        lv.setAdapter(new ArrayAdapter(this, android.R.layout.simple_list_item_1, allDrugsTitles));
        lv.setAdapter(new ArrayAdapter<String>(this,R.layout.list_black_text,R.id.list_content, allDrugsTitles));
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        Intent i = new Intent(DrugsListActivity.this, DrugActivity.class);
        i.putExtra("title", allDrugsTitles.get(position - 1));
        i.putExtra("id", allDrugsIds.get(position - 1));
        startActivity(i);
    }
}
