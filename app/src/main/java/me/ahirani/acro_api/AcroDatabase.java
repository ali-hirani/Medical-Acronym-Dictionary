package me.ahirani.acro_api;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class AcroDatabase extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_NAME = "acro_database";

    private static final String TABLE_SEARCH_NAME = "acro_search_history_table";
    public static final String TABLE_SEARCH_COLUMN_SEARCH_TERM = "acro_search_terms";

    // CREATE TABLE acro_search_history_table(acro_search_terms TEXT PRIMARY KEY)
    private static final String CREATE_TABLE_SEARCH = "CREATE TABLE " + TABLE_SEARCH_NAME + "("
            + TABLE_SEARCH_COLUMN_SEARCH_TERM + " TEXT PRIMARY KEY)";

    private static AcroDatabase INSTANCE = null;

    // Singleton pattern: private constructor so nobody else can instantiate database.
    private AcroDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Singleton pattern: If an instance of the database exists, we return it, else create new one
    public static AcroDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = new AcroDatabase(context);
        }

        return INSTANCE;
    }

    // Instance method, no need to pass in the db
    public Cursor queryAllSearchTerms() {
        final String query = "SELECT " + TABLE_SEARCH_COLUMN_SEARCH_TERM + " FROM " + TABLE_SEARCH_NAME + " ASC";

        // Assuming we have [SELECT name FROM users WHERE id=? AND age=?] then use selectionArgs = new String[]{ id, age }
        return getReadableDatabase().rawQuery(query, null);
    }

    public void insertSearchTerm(String searchTerm) {
        // Key value pair. Key is column name, value is value to insert.
        final ContentValues values = new ContentValues();
        values.put(TABLE_SEARCH_COLUMN_SEARCH_TERM, searchTerm);

        getWritableDatabase().insert(TABLE_SEARCH_NAME, null, values);
    }

    // The first time you create the database (OK)
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_SEARCH);
    }

    // Whenever you add a new column, make a change to the database (OK)
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_SEARCH_NAME);
        }
    }
}
