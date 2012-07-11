package com.justapp.meds;

import android.app.ListActivity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.SearchRecentSuggestions;
import android.view.*;
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
    private int selectedId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        allSubCatsTitle.clear();
        allSubCatsIds.clear();
        String title = getIntent().getExtras().getString("title");
        selectedId = getIntent().getExtras().getInt("id");

        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.list);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.window_title);
        DBHelper myDbHelper = new DBHelper(this);
        myDbHelper = new DBHelper(this);

        final LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View curtain = inflater.inflate(R.layout.curtain, null);
        curtain.setVisibility(View.VISIBLE);
        addContentView(curtain, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
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
//        LayoutInflater inflater = getLayoutInflater();
//        View header = inflater.inflate(R.layout.list_header, (ViewGroup) findViewById(R.id.header_layout_root));
//        TextView categoryName = (TextView) header.findViewById(R.id.categoryName);
//        categoryName.setText(title);
//        lv.addHeaderView(header, null, false);
        TextView categoryName = (TextView) findViewById(R.id.headerTitle);
        categoryName.setText(title);
        lv.setAdapter(new ArrayAdapter<String>(this, R.layout.list_black_text, R.id.list_content, allSubCatsTitle));


    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        Intent i = new Intent(SubCategoryActivity.this, DrugsListActivity.class);
        i.putExtra("title", allSubCatsTitle.get(position));
        i.putExtra("id", allSubCatsIds.get(position));
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
                Intent i = new Intent(SubCategoryActivity.this, AboutActivity.class);
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
        Intent i = new Intent(SubCategoryActivity.this, SearchActivity.class);
        i.putExtra("searchString", query);
        i.putExtra("categoryId", selectedId);
        startActivity(i);
    }
    public void headerSearchButtonClicked(View view) {
        onSearchRequested();
    }
}