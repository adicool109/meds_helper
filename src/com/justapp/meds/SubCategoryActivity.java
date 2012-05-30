package com.justapp.meds;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SubCategoryActivity extends ListActivity {
    static List<String> allSubCatsTitle = new ArrayList<String>();
    static List<Integer> allSubCatsIds = new ArrayList<Integer>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        allSubCatsTitle.clear();
        allSubCatsIds.clear();
        String title = getIntent().getExtras().getString("title");
        int selectedId = getIntent().getExtras().getInt("id");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.list);
        DBHelper myDbHelper = new DBHelper(this);
        myDbHelper = new DBHelper(this);

        try {
            myDbHelper.createDataBase();
        } catch (IOException ioe) {
            throw new Error("Unable to create database");
        }
        try {
            myDbHelper.openDataBase();
            Cursor cursor = myDbHelper.getSubCatsById(selectedId);
            if (cursor.moveToFirst()) {
                do {
                    allSubCatsTitle.add(stringHelper.asUpperCaseFirstChar(cursor.getString(1)));
                    allSubCatsIds.add(Integer.parseInt(cursor.getString(0)));
                } while (cursor.moveToNext());
            }
            myDbHelper.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        ListView lv = getListView();
        LayoutInflater inflater = getLayoutInflater();
        View header = inflater.inflate(R.layout.list_header, (ViewGroup) findViewById(R.id.header_layout_root));
        TextView categoryName = (TextView) header.findViewById(R.id.categoryName);
        categoryName.setText(title);
        lv.addHeaderView(header, null, false);
        lv.setAdapter(new ArrayAdapter<String>(this,R.layout.list_black_text,R.id.list_content, allSubCatsTitle));

    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        Intent i = new Intent(SubCategoryActivity.this, DrugsListActivity.class);
        i.putExtra("title", allSubCatsTitle.get(position - 1));
        i.putExtra("id", allSubCatsIds.get(position - 1));
        startActivity(i);
    }
}