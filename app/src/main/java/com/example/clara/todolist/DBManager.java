package com.example.clara.todolist;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * Created by clara on 5-3-2017.
 */

public class DBManager {
    private DatabaseHelper dbHelper;
    private Context context;
    private SQLiteDatabase database;

    //Constructor
    public DBManager(Context c) { context = c; }

    // Opening the database
    public DBManager open() throws SQLException {
        dbHelper = new DatabaseHelper(context);
        database = dbHelper.getWritableDatabase();
        return this;
    }

    // closing the database
    public void close() { dbHelper.close(); }

    // Inserting an item into the database
    public long insert(String name) {
        ContentValues contentValue = new ContentValues();
        contentValue.put(DatabaseHelper.TODOSUBJECT, name);
        long id = database.insert(DatabaseHelper.TABLE_NAME, null, contentValue);
        String idS = String.valueOf(id);
        Log.d("idinDBMan",idS);
        fetch();
        return id;

    }

    // Retrieving Cursor fetch()
    public Cursor fetch() {
        String[] columns = new String[] {DatabaseHelper._ID, DatabaseHelper.TODOSUBJECT};
        Cursor cursor = database.query(DatabaseHelper.TABLE_NAME, columns, null, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        Log.d("cursor", Integer.toString(cursor.getCount()));
        return cursor;
    }

    // Update the database
    public int update(long _id, String name) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelper.TODOSUBJECT, name);
        int i = database.update(DatabaseHelper.TABLE_NAME, contentValues,
                DatabaseHelper._ID + " = " + _id, null);
        return i;
    }

    public void delete(long _id) {
        database.delete(DatabaseHelper.TABLE_NAME, DatabaseHelper._ID + " = " + _id, null);
    }


}
