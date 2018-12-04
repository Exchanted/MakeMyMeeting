package com.example.callum.makemymeeting;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "meeting.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_NAME = "meeting_table";

    private static final String COL_1 = "ID";
    private static final String COL_2 = "MEETINGNAME";
    private static final String COL_3 = "ATTENDEES";
    private static final String COL_4 = "DATE";
    private static final String COL_5 = "TIME";
    private static final String COL_6 = "NOTES";
    private static final String COL_7 = "LOCATION";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("Create Table " + TABLE_NAME +" (ID INTEGER PRIMARY KEY AUTOINCREMENT, MEETINGNAME TEXT, ATTENDEES TEXT, DATE TEXT, TIME TEXT, NOTES TEXT, LOCATION TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " +TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

    public boolean insertData(String meetingname, String attendees, String date, String time,
                              String notes, String location) {

        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_2, meetingname);
        contentValues.put(COL_3, attendees);
        contentValues.put(COL_4, date);
        contentValues.put(COL_5, time);
        contentValues.put(COL_6, notes);
        contentValues.put(COL_7, location);
        long result = sqLiteDatabase.insert(TABLE_NAME, null, contentValues);

        if (result == -1)
            return false;
            else
                return true;
    }

    public boolean updateData(String id, String meetingname, String attendees, String time,
                              String date, String notes, String location) {

        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(COL_1, id);
        contentValues.put(COL_2, meetingname);
        contentValues.put(COL_3, attendees);
        contentValues.put(COL_4, date);
        contentValues.put(COL_5, time);
        contentValues.put(COL_6, notes);
        contentValues.put(COL_7, location);

        sqLiteDatabase.update(TABLE_NAME, contentValues, "ID = ?", new String[] {id});

        return true;
    }

    public Integer deleteData (String id) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        return sqLiteDatabase.delete(TABLE_NAME, "ID = ?", new String[] {id});
    }


    public Cursor getAllData() {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();

        Cursor res = sqLiteDatabase.rawQuery("select * from "+TABLE_NAME, null);
        return res;
    }
}
