package com.justapp.meds;

import android.app.ListActivity;
import android.app.SearchManager;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.SearchRecentSuggestions;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends ListActivity {
    static List<String> allCategoriesTitles = new ArrayList<String>();
    static List<Integer> allCategoriesIds = new ArrayList<Integer>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        allCategoriesIds.clear();
        allCategoriesTitles.clear();
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
            Cursor cursor = myDbHelper.getAllCategories();
            if (cursor.moveToFirst()) {
                do {
                    allCategoriesTitles.add(stringHelper.asUpperCaseFirstChar(cursor.getString(1)));
                    allCategoriesIds.add(Integer.parseInt(cursor.getString(0)));
                } while (cursor.moveToNext());
            }
            myDbHelper.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        ListView listView = getListView();
        listView.setTextFilterEnabled(true);
        //todo add header
        listView.setAdapter(new ArrayAdapter<String>(this, R.layout.list_black_text, R.id.list_content, allCategoriesTitles));

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
        Intent i = new Intent(MainActivity.this, SearchActivity.class);
        i.putExtra("searchString", query);
        startActivity(i);
    }
}
