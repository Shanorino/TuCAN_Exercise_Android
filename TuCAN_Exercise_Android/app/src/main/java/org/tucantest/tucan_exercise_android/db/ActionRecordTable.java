package org.tucantest.tucan_exercise_android.db;

import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

public class ActionRecordTable implements BaseColumns {
    // ID;EventTime;EventTimeNano;HistoricalEventTime;HistoricalEventTimeNano;EventType;ToolType;MotionEventType;ActionMasked;Action;X;Y;Z;Pressure;Orientation;Tilt;ButtonState;HistoricalX;HistoricalY;HistoricalZ;HistoricalPressure;HistoricalOrientation;HistoricalTilt
    public static final String TABLE_NAME = "ActionRecord";
    public static final String COLUMN_ID = "ID";
    public static final String COLUMN_EVENT_TIME = "EventTime";
    public static final String COLUMN_EVENT_TIME_NANO = "EventTimeNano";
    public static final String COLUMN_HISTORICAL_EVENT_TIME = "HistoricalEventTime";
    public static final String COLUMN_HISTORICAL_EVENT_TIME_NANO = "HistoricalEventTimeNano";
    public static final String COLUMN_EVENT_TYPE = "EventType";
    public static final String COLUMN_TOOL_TYPE = "ToolType";
    public static final String COLUMN_MOTION_EVENT_TYPE = "MotionEventType";
    public static final String COLUMN_ACTION_MASKED = "ActionMasked";
    public static final String COLUMN_ACTION = "ActionName";
    public static final String COLUMN_X = "X";
    public static final String COLUMN_Y = "Y";
    public static final String COLUMN_Z = "Z";
    public static final String COLUMN_PRESSURE = "Pressure";
    public static final String COLUMN_ORIENTATION = "Orientation";
    public static final String COLUMN_TILT = "Tilt";
    public static final String COLUMN_BUTTON_STATE = "ButtonState";
    public static final String COLUMN_HISTORICAL_X = "HistoricalX";
    public static final String COLUMN_HISTORICAL_Y = "HistoricalY";
    public static final String COLUMN_HISTORICAL_Z = "HistoricalZ";
    public static final String COLUMN_HISTORICAL_PRESSURE = "HistoricalPressure";
    public static final String COLUMN_HISTORICAL_ORIENTATION = "HistoricalOrientation";
    public static final String COLUMN_HISTORICAL_TILT = "HistoricalTilt";


    public static final String[] fullProjection = new String[]{
            COLUMN_ID,
            COLUMN_EVENT_TIME,
            COLUMN_EVENT_TIME_NANO,
            COLUMN_HISTORICAL_EVENT_TIME,
            COLUMN_HISTORICAL_EVENT_TIME_NANO,
            COLUMN_EVENT_TYPE,
            COLUMN_TOOL_TYPE,
            COLUMN_MOTION_EVENT_TYPE,
            COLUMN_ACTION_MASKED,
            COLUMN_ACTION,
            COLUMN_X,
            COLUMN_Y,
            COLUMN_Z,
            COLUMN_PRESSURE,
            COLUMN_ORIENTATION,
            COLUMN_TILT,
            COLUMN_BUTTON_STATE,
            COLUMN_HISTORICAL_X,
            COLUMN_HISTORICAL_Y,
            COLUMN_HISTORICAL_Z,
            COLUMN_HISTORICAL_PRESSURE,
            COLUMN_HISTORICAL_ORIENTATION,
            COLUMN_HISTORICAL_TILT
            };

    private static final String DATABASE_CREATE = "create table "
            + TABLE_NAME
            + "("
            + COLUMN_ID + " INTEGER DEFAULT 0, "
            + COLUMN_EVENT_TIME + " INTEGER DEFAULT 0, "
            + COLUMN_EVENT_TIME_NANO + " INTEGER DEFAULT 0, "
            + COLUMN_HISTORICAL_EVENT_TIME + " INTEGER DEFAULT 0, "
            + COLUMN_HISTORICAL_EVENT_TIME_NANO + " INTEGER DEFAULT 0 ,"
            + COLUMN_EVENT_TYPE + " TEXT , "
            + COLUMN_TOOL_TYPE + " INTEGER DEFAULT 0, "
            + COLUMN_MOTION_EVENT_TYPE + " INTEGER DEFAULT 0, "
            + COLUMN_ACTION_MASKED + " INTEGER DEFAULT 0, "
            + COLUMN_ACTION + " TEXT, "
            + COLUMN_X + " REAL DEFAULT 0, "
            + COLUMN_Y + " REAL DEFAULT 0, "
            + COLUMN_Z + " REAL DEFAULT 0, "
            + COLUMN_PRESSURE + " REAL DEFAULT 0, "
            + COLUMN_ORIENTATION + " REAL DEFAULT 0, "
            + COLUMN_TILT + " REAL DEFAULT 0, "
            + COLUMN_BUTTON_STATE + "INTEGER DEFAULT 0, "
            + COLUMN_HISTORICAL_X + " REAL DEFAULT 0, "
            + COLUMN_HISTORICAL_Y + " REAL DEFAULT 0, "
            + COLUMN_HISTORICAL_Z + " REAL DEFAULT 0, "
            + COLUMN_HISTORICAL_PRESSURE + " REAL DEFAULT 0, "
            + COLUMN_HISTORICAL_ORIENTATION + " REAL DEFAULT 0, "
            + COLUMN_HISTORICAL_TILT + " REAL DEFAULT 0 "
            + ");";

    public static void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);
    }

    public static void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        if(oldVersion < 6 && newVersion >= 6) {
            /*
            Example DB Migration Query:

            String someQuery = "DELETE FROM "
                    + TABLE_NAME
                    + " WHERE "
                    + COLUMN_STATUS
                    + " a < b"
                    + ";";

            database.execSQL(deleteHistoryQuery);
            */
        }
    }

}