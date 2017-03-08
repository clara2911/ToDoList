package com.example.clara.todolist;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by clara on 5-3-2017.
 */

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "TODOLISTDATABASE";
    private static final int DB_VERSION = 1;

    public static final String _ID = "_id";
    public static final String TABLE_NAME = "TODOLIST";
    public static final String TODOSUBJECT = "todosubject";

    // Creating table query
    private static final String CREATE_TABLE = "create table " + TABLE_NAME
            + "(" + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "+ TODOSUBJECT +
            " TEXT NOT NULL " + ");";

    // constructor
    public DatabaseHelper(Context context) {

        super(context, DB_NAME, null, DB_VERSION);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //db.execSQL("DROP TABLE IF EXISTS "+ TABLE_NAME);
        db.execSQL(CREATE_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+ TABLE_NAME);
        onCreate(db);
    }

}
