package com.example.callum.makemymeeting;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;

/**
 * This class will create the database and the rows that it contains
 * Contains inserting data, updating data and deleting data methods for when
 *          the database is instantiated in other classes
 */

public class DBHelper extends SQLiteOpenHelper {

    /**
     * Create the database, along with how many rows are needed
     * Also give each meeting a unique ID
     */

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


    /**
     * @param context - Capture database
     */
    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * {@inheritDoc}
     * @param sqLiteDatabase - Execute an SQL command on the database to create the database with
     *                       given fields
     * Give each meeting a unique ID to perform edits/updates
     */
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("Create Table " + TABLE_NAME + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, MEETINGNAME TEXT, ATTENDEES TEXT, DATE TEXT, TIME TEXT, NOTES TEXT, LOCATION TEXT)");
    }

    /**
     * {@inheritDoc}
     * @param sqLiteDatabase Execute an SQL command on the database to create the database with
     *      *                       given fields
     * @param i - Check for table existing
     * @param i1 - On upgrade of database create a fresh version
     */
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

    /**
     * {@inheritDoc}
     * @param meetingname - Meeting name Column 1
     * @param attendees - Attendees of Meeting - List in Column 2
     * @param date - Date of Meeting in Column 3
     * @param time - Time of Meeting in Column 4
     * @param notes - Notes of Meeting in Column 5
     * @param location - Location of Meeting for Places API Column 6
     * @return - True if values written into database, false is incorrectly entered
     */
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

    /**
     * @param id - Unique ID of meeting
     * @param meetingname - new Meeting name
     * @param attendees - new attendee list
     * @param time - new time of meeting
     * @param date - new date of meeting
     * @param notes - new notes of meeting
     * @param location - new location of meeting
     * @return - true if values entered into database correctly
     */
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

        sqLiteDatabase.update(TABLE_NAME, contentValues, "ID = ?", new String[]{id});

        return true;
    }

    /**
     * @param id - Unique ID of meeting
     * @return - Deletion of meeting using ID
     */
    public Integer deleteData(String id) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        return sqLiteDatabase.delete(TABLE_NAME, "ID = ?", new String[]{id});
    }

    /**
     * @return - Get all row data from meeting into Cursor view
     */
    public Cursor getAllData() {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();

        Cursor res = sqLiteDatabase.rawQuery("select * from " + TABLE_NAME, null);
        return res;
    }
}
