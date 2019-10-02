package org.tucantest.tucan_exercise_android.db;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.os.CancellationSignal;

import java.util.HashMap;

public class ActionRecordProvider extends ContentProvider {
    private Context mContext = null;

    static final String PROVIDER_NAME = "org.tucantest.tucan_exercise_android.db.ActionRecordProvider";
    static final String URL = "content://" + PROVIDER_NAME + "/actionRecord";
    static final Uri CONTENT_URI = Uri.parse(URL);

    static final String _ID = "id";
    static final String downloadDate = "ID";

    static final int RECORD = 1;

    static final UriMatcher uriMatcher;
    static{
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(PROVIDER_NAME, "actionRecord", RECORD);
    }


    private SQLiteDatabase db;
    private static HashMap<String, String> RECORD_PROJECTION_MAP;

    @Override
    public boolean onCreate() {
        db = DatabaseHelper.getInstance(getContext()).getWritableDatabase();

        return true;
    }



    @Override
    public String getType(Uri uri) {
        switch (uriMatcher.match(uri)){
            case RECORD:
                return "vnd.android.cursor.dir/vnd.example.record";
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        Uri ret = null;
        long rowID = db.insertWithOnConflict(ActionRecordTable.TABLE_NAME, "", values, SQLiteDatabase.CONFLICT_IGNORE);
        // Succeed
        if (rowID != -1) {
            Uri _uri = ContentUris.withAppendedId(CONTENT_URI, rowID);
            getContext().getContentResolver().notifyChange(_uri, null);
            return _uri;
        }
        return ret;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int count = 0;
        switch (uriMatcher.match(uri)) {
            case RECORD:
                count = db.update(ActionRecordTable.TABLE_NAME, values, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri );
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int count = 0;
        switch (uriMatcher.match(uri)){
            case RECORD:
                count = db.delete(ActionRecordTable.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder, CancellationSignal cancellationSignal) {
        //return super.query(uri, projection, selection, selectionArgs, sortOrder, cancellationSignal);
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(ActionRecordTable.TABLE_NAME);

        switch (uriMatcher.match(uri)) {
            case RECORD:
                qb.setProjectionMap(RECORD_PROJECTION_MAP);
                break;

            default:
        }
        if (sortOrder == null || sortOrder == ""){
            /**
             * By default sort on package names
             */
            sortOrder = downloadDate;
        }

        Cursor c = qb.query(db,	projection,	selection,
                selectionArgs,null, null, sortOrder);
        /**
         * register to watch a content URI for changes
         */
        c.setNotificationUri(getContext().getContentResolver(), uri);
        return c;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        return null;
    }

}
