package com.justapp.meds;

import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import com.justapp.meds.helpers.ZIP;

import java.io.*;
import java.sql.SQLException;

public class DBHelper extends SQLiteOpenHelper {
    private static String DB_PATH = "/data/data/com.justapp.meds/databases/";
    private static String DB_NAME = "db";
    private static final String TABLE_CATEGORIES = "categories";
    private static final String TABLE_DRUGS = "drugs";

    private SQLiteDatabase myDataBase;
    private final Context myContext;

    public DBHelper(Context context) {
        super(context, DB_NAME, null, 1);
        this.myContext = context;
    }

    public void createDataBase() throws IOException {

        boolean dbExist = checkDataBase();

        if (dbExist) {
            //do nothing - database already exist
        } else {
            this.getReadableDatabase();
            try {
                CopyAssets();
                ZIP.unzip(DB_PATH + "db.zip", DB_PATH);
            } catch (Exception e) {
                Log.e("medroid", e+"");
            }
        }
    }

    public boolean checkDataBase() {
        SQLiteDatabase checkDB = null;
        try {
            String myPath = DB_PATH + DB_NAME;
            checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
        } catch (SQLiteException e) {
            Log.e("tag", "database does't exist yet " + e.getMessage());
            //database does't exist yet.
        }
        if (checkDB != null) {
            checkDB.close();
        }
        return checkDB != null;
    }


    private void CopyAssets() {
        AssetManager assetManager = myContext.getAssets();
        String[] files = null;
        try {
            files = assetManager.list("");
        } catch (IOException e) {
            Log.e("tag", e.getMessage());
        }
        for(String filename : files) {
            InputStream in = null;
            OutputStream out = null;
            try {
                in = assetManager.open(filename);
                out = new FileOutputStream(DB_PATH + filename);
                copyFile(in, out);
                in.close();
                in = null;
                out.flush();
                out.close();
                out = null;
            } catch(Exception e) {
                Log.e("tag", e.getMessage());
            }
        }
    }
    private void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while((read = in.read(buffer)) != -1){
            out.write(buffer, 0, read);
        }
    }


    private void copyDataBase() throws IOException {
        InputStream myInput = myContext.getAssets().open(DB_NAME);
        String outFileName = DB_PATH + DB_NAME;
        OutputStream myOutput = new FileOutputStream(outFileName);

        byte[] buffer = new byte[1024];
        int length;
        while ((length = myInput.read(buffer)) > 0) {
            myOutput.write(buffer, 0, length);
        }

        myOutput.flush();
        myOutput.close();
        myInput.close();
    }

    public void openDataBase() throws SQLException {
        String myPath = DB_PATH + DB_NAME;
        myDataBase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READWRITE);
    }

    @Override
    public synchronized void close() {
        if (myDataBase != null)
            myDataBase.close();
        super.close();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public int checkSubcats(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_CATEGORIES, new String[]{"id",
                "title", "parent_id"}, "parent_id=" + id,
                null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        return cursor.getCount();
    }

    public Cursor getAllCategories() {
        String selectQuery = "SELECT  * FROM " + TABLE_CATEGORIES + " WHERE (parent_id IS null) ORDER BY title ASC";
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery(selectQuery, null);
    }

    public Cursor getDrugsById(int id) {
        String selectQuery = "SELECT  * FROM " + TABLE_DRUGS + " WHERE parent_id=" + id + " ORDER BY title ASC";
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery(selectQuery, null);
    }

    public String getDrugInfoById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_DRUGS, new String[]{"id",
                "parent_id", "text", "title",}, "id=" + id,
                null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        return new String(cursor.getString(2));
    }

    public Cursor getSubCatsById(int id) {
        String selectQuery = "SELECT  * FROM " + TABLE_CATEGORIES + " WHERE parent_id=" + id + " ORDER BY title ASC";
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery(selectQuery, null);
    }

    public Cursor fetchRecordsByQuery(String query) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(true, TABLE_DRUGS, new String[]{"id",
                "title"}, "title" + " LIKE " + "'%" + query + "%'", null,
                null, null, "title ASC", null);
    }
    public String getSqliteVersion(){
        Cursor cursor = SQLiteDatabase.openOrCreateDatabase(":memory:", null).rawQuery("select sqlite_version() AS sqlite_version", null);
        String sqliteVersion = "";
        while(cursor.moveToNext()){
            sqliteVersion += cursor.getString(0);
        }
        return sqliteVersion;
    }
}
