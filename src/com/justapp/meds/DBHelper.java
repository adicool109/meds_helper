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

public class DBHelper extends SQLiteOpenHelper {
    private static String DB_PATH = "/data/data/com.justapp.meds/databases/";
    private static String DB_NAME = "meds_db.sqlite3";
    private static final String TABLE_CATEGORIES = "categories";
    private static final String TABLE_DRUGS = "drugs";
    private static final String TABLE_DRUGS_CATEGORIES = "drugs_categories";

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
                copyDataBase();

            } catch (IOException e) {
                throw new Error("Error copying database");
            }
        }
    }

    private boolean checkDataBase() {
        SQLiteDatabase checkDB = null;
        try {
            String myPath = DB_PATH + DB_NAME;
            checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
        } catch (SQLiteException e) {
            //database does't exist yet.
        }
        if (checkDB != null) {
            checkDB.close();
        }
        return checkDB != null ? true : false;
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

    public Cursor getDrugsByParentId(int id) {
        String selectQuery = String.format("SELECT %1$s.id, %1$s.title FROM %2$s INNER JOIN %1$s on %1$s.id = %2$s.drug_id WHERE %2$s.category_id = %3$d", TABLE_DRUGS, TABLE_DRUGS_CATEGORIES, id);
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery(selectQuery, null);
    }

    public String getDrugInfoById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_DRUGS, new String[]{"id", "text", "title",}, "id=" + id, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        return new String(cursor.getString(1));
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
}
