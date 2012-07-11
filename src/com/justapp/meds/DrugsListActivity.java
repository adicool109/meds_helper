package com.justapp.meds;

import android.app.ListActivity;
import android.app.SearchManager;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.SearchRecentSuggestions;
import android.view.*;
import android.widget.*;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DrugsListActivity extends ListActivity {
    static List<String> allDrugsTitles = new ArrayList<String>();
    static List<Integer> allDrugsIds = new ArrayList<Integer>();
    private int selectedId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        allDrugsIds.clear();
        allDrugsTitles.clear();
        String title = getIntent().getExtras().getString("title");
        selectedId = getIntent().getExtras().getInt("id");

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
            Cursor cursor = myDbHelper.getDrugsByParentId(selectedId);
            if (cursor.moveToFirst()) {
                do {
                    allDrugsTitles.add(stringHelper.asUpperCaseFirstChar(cursor.getString(1)));
                    allDrugsIds.add(Integer.parseInt(cursor.getString(0)));
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
        lv.setAdapter(new ArrayAdapter<String>(this,R.layout.list_black_text,R.id.list_content, allDrugsTitles));
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        Intent i = new Intent(DrugsListActivity.this, DrugActivity.class);
        i.putExtra("title", allDrugsTitles.get(position - 1));
        i.putExtra("id", allDrugsIds.get(position - 1));
        startActivity(i);
    }
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.search_record:
                onSearchRequested();
                return true;
            case R.id.quit:
                finish();
                return true;
            case R.id.clear_recent_suggestions:
                SearchRecentSuggestions suggestions = new SearchRecentSuggestions(this,
                        SuggestionProvider.AUTHORITY, SuggestionProvider.MODE);
                suggestions.clearHistory();
                return true;
            case R.id.about:
                Intent i = new Intent(DrugsListActivity.this, AboutActivity.class);
                startActivity(i);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        handleIntent(intent);
    }
    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            doMySearch(query);
        }
    }
    private void doMySearch(String query){
        SearchRecentSuggestions suggestions = new SearchRecentSuggestions(this,
                SuggestionProvider.AUTHORITY, SuggestionProvider.MODE);
        suggestions.saveRecentQuery(query, null);
        Intent i = new Intent(DrugsListActivity.this, SearchActivity.class);
        i.putExtra("searchString", query);
        i.putExtra("categoryId", selectedId);
        startActivity(i);
    }
}
