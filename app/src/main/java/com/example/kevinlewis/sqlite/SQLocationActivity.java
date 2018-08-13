package com.example.kevinlewis.sqlite;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Location;
import android.provider.BaseColumns;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

// Code derived by the example code from the CS496 lectures

public class SQLocationActivity extends AppCompatActivity {

    private TextView mLatText;
    private TextView mLonText;
    private Location mLastLocation;
    private static final int LOCATION_PERMISSON_RESULT = 17;

    NoteSQLite mSQLiteExample;
    Button mSQLSubmitButton;
    Button mPermissionRequestButton;
    Cursor mSQLCursor;
    SimpleCursorAdapter mSQLCursorAdapter;
    private static final String TAG = "SQLActivity";
    SQLiteDatabase mSQLDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sqlocation);

        // Database Setup

        mSQLiteExample = new NoteSQLite(this);
        mSQLDB = mSQLiteExample.getWritableDatabase();

        mSQLSubmitButton = (Button) findViewById(R.id.sql_add_row_button);
        mSQLSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mSQLDB != null){
                    ContentValues vals = new ContentValues();

                    // Get the current location
                    //SimpleLocation tempLoc = getLocation();

                    //vals.put(DBContract.NoteTable.COLUMN_NAME_LATITUDE, tempLoc.latitude);
                    //vals.put(DBContract.NoteTable.COLUMN_NAME_LONGITUDE, tempLoc.longitude);
                    vals.put(DBContract.NoteTable.COLUMN_NAME_NOTE, ((EditText)findViewById(R.id.sql_text_input)).getText().toString());
                    mSQLDB.insert(DBContract.NoteTable.TABLE_NAME,null,vals);
                    populateTable();
                } else {
                    Log.d(TAG, "Unable to access database for writing.");
                }
            }
        });

        // Setup Permission Request Button

        mPermissionRequestButton = (Button) findViewById(R.id.sql_request_permission);
        mPermissionRequestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Requesting Permission");

            }
        });

        populateTable();
    }

    private void populateTable(){
        if(mSQLDB != null) {
            try {
                if(mSQLCursorAdapter != null && mSQLCursorAdapter.getCursor() != null){
                    if(!mSQLCursorAdapter.getCursor().isClosed()){
                        mSQLCursorAdapter.getCursor().close();
                    }
                }
                mSQLCursor = mSQLDB.query(DBContract.NoteTable.TABLE_NAME,
                        new String[]{DBContract.NoteTable._ID, DBContract.NoteTable.COLUMN_NAME_NOTE,
                                DBContract.NoteTable.COLUMN_NAME_LATITUDE, DBContract.NoteTable.COLUMN_NAME_LONGITUDE},
                        DBContract.NoteTable.COLUMN_NAME_LATITUDE + " > ?", new String[]{"-1000"},
                        null, null, null);
                ListView SQLListView = (ListView) findViewById(R.id.sql_list_view);
                mSQLCursorAdapter = new SimpleCursorAdapter(this,
                        R.layout.note_item,
                        mSQLCursor,
                        new String[]{DBContract.NoteTable.COLUMN_NAME_NOTE, DBContract.NoteTable.COLUMN_NAME_LATITUDE,
                                    DBContract.NoteTable.COLUMN_NAME_LONGITUDE},
                        new int[]{R.id.sql_listview_note, R.id.sql_listview_latitude, R.id.sql_listview_longitude},
                        0);
                SQLListView.setAdapter(mSQLCursorAdapter);
            } catch (Exception e) {
                Log.d(TAG, "Error loading data from database");
            }
        }
    }

}

class NoteSQLite extends SQLiteOpenHelper {

    public NoteSQLite(Context context) {
        super(context, DBContract.NoteTable.DB_NAME, null, DBContract.NoteTable.DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DBContract.NoteTable.SQL_CREATE_DEMO_TABLE);

        /*
        ContentValues testValues = new ContentValues();
        testValues.put(DBContract.NoteTable.COLUMN_NAME_LATITUDE, "422.2");
        testValues.put(DBContract.NoteTable.COLUMN_NAME_LONGITUDE, "422.2");
        testValues.put(DBContract.NoteTable.COLUMN_NAME_NOTE, "Hello SQLite");
        db.insert(DBContract.NoteTable.TABLE_NAME,null,testValues);
        */
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DBContract.NoteTable.SQL_DROP_DEMO_TABLE);
        onCreate(db);
    }
}

final class DBContract {
    private DBContract(){};

    public final class NoteTable implements BaseColumns {
        public static final String DB_NAME = "note_db";
        public static final String TABLE_NAME = "sqliteNote";
        public static final String COLUMN_NAME_NOTE = "note_save";
        public static final String COLUMN_NAME_LATITUDE = "latitude";
        public static final String COLUMN_NAME_LONGITUDE = "longitude";
        public static final int DB_VERSION = 4;


        public static final String SQL_CREATE_DEMO_TABLE = "CREATE TABLE " +
                NoteTable.TABLE_NAME + "(" + NoteTable._ID + " INTEGER PRIMARY KEY NOT NULL," +
                NoteTable.COLUMN_NAME_NOTE + " VARCHAR(255)," +
                NoteTable.COLUMN_NAME_LATITUDE + " FLOAT," +
                NoteTable.COLUMN_NAME_LONGITUDE + " FLOAT);";

        public static final String SQL_TEST_DEMO_TABLE_INSERT = "INSERT INTO " + TABLE_NAME +
                " (" + COLUMN_NAME_NOTE + "," + COLUMN_NAME_LATITUDE + "," + COLUMN_NAME_LONGITUDE + ") VALUES ('test', 123.1, 321.1);";

        public  static final String SQL_DROP_DEMO_TABLE = "DROP TABLE IF EXISTS " + NoteTable.TABLE_NAME;
    }
}
