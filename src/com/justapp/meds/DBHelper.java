package com.justapp.meds;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {
    //The Android's default system path of your application database.
    private static String DB_PATH = "/data/data/com.justapp.meds/databases/";
    private static String DB_NAME = "meds_db.sqlite3";
    private static final String TABLE_CATEGORIES = "categories";
    private static final String TABLE_DRUGS = "drugs";

    private SQLiteDatabase myDataBase;
    private final Context myContext;

    public DBHelper(Context context) {
        super(context, DB_NAME, null, 1);
        this.myContext = context;
    }

    /**
     * Creates a empty database on the system and rewrites it with your own database.
     * */
    public void createDataBase() throws IOException {

        boolean dbExist = checkDataBase();

        if(dbExist){
            //do nothing - database already exist
        }else{
            this.getReadableDatabase();
            try {
                copyDataBase();

            } catch (IOException e) {
                throw new Error("Error copying database");
            }
        }
    }

    private boolean checkDataBase(){
        SQLiteDatabase checkDB = null;
        try{
            String myPath = DB_PATH + DB_NAME;
            checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
        }catch(SQLiteException e){
            //database does't exist yet.
        }
        if(checkDB != null){
            checkDB.close();
        }
        return checkDB != null ? true : false;
    }

     private void copyDataBase() throws IOException{
        InputStream myInput = myContext.getAssets().open(DB_NAME);
        String outFileName = DB_PATH + DB_NAME;
        OutputStream myOutput = new FileOutputStream(outFileName);

        byte[] buffer = new byte[1024];
        int length;
        while ((length = myInput.read(buffer))>0){
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
        if(myDataBase != null)
            myDataBase.close();
        super.close();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    // Getting single contact
    public int checkSubcats(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_CATEGORIES, new String[] { "id",
                "title", "parent_id" }, "parent_id=" + id,
                null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        return cursor.getCount();
    }

    public List<String> getAllCategories() {
        List<String> allCats = new ArrayList<String>();
        String selectQuery = "SELECT  * FROM " + TABLE_CATEGORIES + " WHERE (parent_id IS null)";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                allCats.add(stringHelper.asUpperCaseFirstChar(cursor.getString(1)));
            } while (cursor.moveToNext());
        }
        return allCats;
    }

    public List getAllCategoriesId() {
        List allCatsId = new ArrayList<Integer>();
        String selectQuery = "SELECT  * FROM " + TABLE_CATEGORIES + " WHERE (parent_id IS null)";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                allCatsId.add(Integer.parseInt(cursor.getString(0)));
            } while (cursor.moveToNext());
        }
        return allCatsId;
    }

    public List<String> getDrugsTitleById(int id) {
        List drugs = new ArrayList<String>();
        String selectQuery = "SELECT  * FROM " + TABLE_DRUGS + " WHERE parent_id="+id;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                drugs.add(stringHelper.asUpperCaseFirstChar(cursor.getString(1)));
            } while (cursor.moveToNext());
        }
        return drugs;
    }

    public List<Integer> getDrugsIdById(int id) {
        List drugs = new ArrayList<Integer>();
        String selectQuery = "SELECT  * FROM " + TABLE_DRUGS + " WHERE parent_id="+id;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                drugs.add(Integer.parseInt(cursor.getString(0)));
            } while (cursor.moveToNext());
        }
        return drugs;
    }
    public String getDrugInfoById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_DRUGS, new String[] { "id",
                 "parent_id", "text", "title",}, "id=" + id,
                null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        return new String(cursor.getString(2));
    }
    public List<String> getSubCatsTitlesById(int id) {
        List subCats = new ArrayList<String>();
        String selectQuery = "SELECT  * FROM " + TABLE_CATEGORIES + " WHERE parent_id="+id;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                subCats.add(stringHelper.asUpperCaseFirstChar(cursor.getString(1)));
            } while (cursor.moveToNext());
        }
        return subCats;
    }

    public List<Integer> getSubCatsIdsById(int id) {
        List subCatsIds = new ArrayList<Integer>();
        String selectQuery = "SELECT  * FROM " + TABLE_CATEGORIES + " WHERE parent_id="+id;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                subCatsIds.add(Integer.parseInt(cursor.getString(0)));
            } while (cursor.moveToNext());
        }
        return subCatsIds;
    }
}
