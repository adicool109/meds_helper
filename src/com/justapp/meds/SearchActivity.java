package com.justapp.meds;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends ListActivity {
    private DBHelper mDbHelper;
    static List searchElementsIds = new ArrayList<Integer>();
    static List searchElementsTitles = new ArrayList<String>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search);
        mDbHelper = new DBHelper(this);
        try {
            mDbHelper.openDataBase();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        TextView searchHeader = (TextView) findViewById(R.id.searchString);
        String queryString = getQueryString();
        searchHeader.setText(getString(R.string.search_for) + " " + queryString);
        showResults(queryString);
    }

    private void showResults(String query) {
        Cursor cursor = mDbHelper.fetchRecordsByQuery(query);
        startManagingCursor(cursor);

        searchElementsIds = new ArrayList<Integer>();
        searchElementsTitles = new ArrayList<String>();
        if (cursor.moveToFirst()) {
            do {
                searchElementsIds.add(Integer.parseInt(cursor.getString(0)));
                searchElementsTitles.add(stringHelper.asUpperCaseFirstChar(cursor.getString(1)));
            } while (cursor.moveToNext());
        }
        ListView listView = getListView();
        listView.setTextFilterEnabled(true);
        //todo add header
        listView.setAdapter(new ArrayAdapter<String>(this, R.layout.list_black_text, R.id.list_content, searchElementsTitles));
    }

    public String getQueryString() {
        return getIntent().getExtras().getString("searchString");
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        Intent i = new Intent(SearchActivity.this, DrugActivity.class);
        i.putExtra("title", (String) searchElementsTitles.get(position));
        i.putExtra("id", (Integer) searchElementsIds.get(position));
        startActivity(i);
    }
}
