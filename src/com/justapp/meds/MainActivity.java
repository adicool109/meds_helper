package com.justapp.meds;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends ListActivity {
    static List<String> allCategoriesTitles = new ArrayList<String>();
    static List<Integer> allCategoriesIds = new ArrayList<Integer>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
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

            allCategoriesTitles = myDbHelper.getAllCategories();
            allCategoriesIds = myDbHelper.getAllCategoriesId();

            myDbHelper.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
          //
       // setListAdapter(new ArrayAdapter<String>(this, R.layout.main, allCategoriesTitles));
        ListView listView = getListView();
        listView.setTextFilterEnabled(true);
        //todo add header
        listView.setAdapter(new ArrayAdapter<String>(this,R.layout.list_black_text,R.id.list_content, allCategoriesTitles));

        final DBHelper finalMyDbHelper = myDbHelper;
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                int checkSubcat = finalMyDbHelper.checkSubcats(allCategoriesIds.get(position));
                if (checkSubcat > 0) {
                    Intent i = new Intent(MainActivity.this, SubCategoryActivity.class);
                    i.putExtra("title", allCategoriesTitles.get(position));
                    i.putExtra("id", allCategoriesIds.get(position));
                    startActivity(i);
                } else {
                    Intent i = new Intent(MainActivity.this, DrugsListActivity.class);
                    i.putExtra("title", allCategoriesTitles.get(position));
                    i.putExtra("id", allCategoriesIds.get(position));
                    startActivity(i);
                }
            }
        });
    }
}
