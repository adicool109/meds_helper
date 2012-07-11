package com.justapp.meds;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.provider.SearchRecentSuggestions;
import android.text.Html;
import android.view.*;
import android.view.ViewGroup.LayoutParams;
import android.widget.*;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends ListActivity {
    static List<String> allCategoriesTitles = new ArrayList<String>();
    static List<Integer> allCategoriesIds = new ArrayList<Integer>();
    private String sqliteVersion;
    public static final String PREFS_NAME = "MyPrefsFile";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        allCategoriesIds.clear();
        allCategoriesTitles.clear();
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.list);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.window_title);

        final LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View curtain = inflater.inflate(R.layout.curtain, null);
        curtain.setVisibility(View.VISIBLE);
        addContentView(curtain, new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));

        DBHelper myDbHelper;
        myDbHelper = new DBHelper(this);


        try {
            myDbHelper.createDataBase();
            myDbHelper.openDataBase();
            sqliteVersion = myDbHelper.getSqliteVersion();
            Cursor cursor = myDbHelper.getAllCategories();
            checkFirstAppRun();
            if (cursor.moveToFirst()) {
                do {
                    allCategoriesTitles.add(stringHelper.asUpperCaseFirstChar(cursor.getString(1)));
                    allCategoriesIds.add(Integer.parseInt(cursor.getString(0)));
                } while (cursor.moveToNext());
            }
            myDbHelper.close();

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IOException ioe) {
            throw new Error("Unable to create database");
        } catch (SQLiteException e) {
            e.printStackTrace();
            if ("3.5.9".equals(sqliteVersion)) {
                AlertDialog dialogError = new AlertDialog.Builder(this).create();
                dialogError.setButton("Close", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        System.exit(1);
                    }
                });
                dialogError.setMessage(getString(R.string.android_sqlite_bug));
                dialogError.show();
            }
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

    private void checkFirstAppRun() {
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        boolean dialogShown = settings.getBoolean("dialogShown", false);
        if (!dialogShown) {
            new AlertDialog.Builder(this)
                    .setTitle(MainActivity.this.getString(R.string.welcome)) //set the Title text
                    .setIcon(R.drawable.icon) //Set the picture in the top left of the popup
                    .setMessage(Html.fromHtml(getString(R.string.about_text)))
                    .setNeutralButton("OK", null).show(); //Sets the button type
            SharedPreferences.Editor editor = settings.edit();
            editor.putBoolean("dialogShown", true);
            editor.commit();
        }
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
                Intent i = new Intent(MainActivity.this, AboutActivity.class);
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
        Intent i = new Intent(MainActivity.this, SearchActivity.class);
        i.putExtra("searchString", query);
        i.putExtra("categoryId", -1);
        startActivity(i);
    }

    public void headerSearchButtonClicked(View view) {
        onSearchRequested();
    }
}
