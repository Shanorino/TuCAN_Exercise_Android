package org.tucantest.tucan_exercise_android.db;

import android.database.Cursor;

import org.tucantest.tucan_exercise_android.model.ActionRecord;

import java.util.ArrayList;
import java.util.List;

public class CursorToActionRecordConverter {
    public static ActionRecord convertFromCursor(Cursor cursor){
        //            COLUMN_ID,
        //            COLUMN_EVENT_TIME,
        //            COLUMN_EVENT_TIME_NANO,
        //            COLUMN_HISTORICAL_EVENT_TIME,
        //            COLUMN_HISTORICAL_EVENT_TIME_NANO,
        //            COLUMN_EVENT_TYPE,
        //            COLUMN_TOOL_TYPE,
        //            COLUMN_MOTION_EVENT_TYPE,
        //            COLUMN_ACTION_MASKED,
        //            COLUMN_ACTION,
        //            COLUMN_X,
        //            COLUMN_Y,
        //            COLUMN_Z,
        //            COLUMN_PRESSURE,
        //            COLUMN_ORIENTATION,
        //            COLUMN_TILT,
        //            COLUMN_BUTTON_STATE,
        //            COLUMN_HISTORICAL_X,
        //            COLUMN_HISTORICAL_Y,
        //            COLUMN_HISTORICAL_Z,
        //            COLUMN_HISTORICAL_PRESSURE,
        //            COLUMN_HISTORICAL_ORIENTATION,
        //            COLUMN_HISTORICAL_TILT
        final int idColIdx = cursor.getColumnIndex(ActionRecordTable.COLUMN_ID);
        final int idColEventTime = cursor.getColumnIndex(ActionRecordTable.COLUMN_EVENT_TIME);
        final int idColEventTimeNano = cursor.getColumnIndex(ActionRecordTable.COLUMN_EVENT_TIME_NANO);
        final int idColHistoricalEventTime = cursor.getColumnIndex(ActionRecordTable.COLUMN_HISTORICAL_EVENT_TIME);
        final int idColHistoricalEventTimeNano = cursor.getColumnIndex(ActionRecordTable.COLUMN_HISTORICAL_EVENT_TIME_NANO);
        final int idColEventType = cursor.getColumnIndex(ActionRecordTable.COLUMN_EVENT_TYPE);
        final int idColToolType = cursor.getColumnIndex(ActionRecordTable.COLUMN_TOOL_TYPE);
        final int idColMotionEventType = cursor.getColumnIndex(ActionRecordTable.COLUMN_MOTION_EVENT_TYPE);
        final int idColActionMasked = cursor.getColumnIndex(ActionRecordTable.COLUMN_ACTION_MASKED);
        final int idColAction = cursor.getColumnIndex(ActionRecordTable.COLUMN_ACTION);
        final int idColX = cursor.getColumnIndex(ActionRecordTable.COLUMN_X);
        final int idColY = cursor.getColumnIndex(ActionRecordTable.COLUMN_Y);
        final int idColZ = cursor.getColumnIndex(ActionRecordTable.COLUMN_Z);
        final int idColPressure = cursor.getColumnIndex(ActionRecordTable.COLUMN_PRESSURE);
        final int idColOrientation = cursor.getColumnIndex(ActionRecordTable.COLUMN_ORIENTATION);
        final int idColTilt = cursor.getColumnIndex(ActionRecordTable.COLUMN_TILT);
        final int idColButtonState = cursor.getColumnIndex(ActionRecordTable.COLUMN_BUTTON_STATE);
        final int idColHistoricalX = cursor.getColumnIndex(ActionRecordTable.COLUMN_HISTORICAL_X);
        final int idColHistoricalY = cursor.getColumnIndex(ActionRecordTable.COLUMN_HISTORICAL_Y);
        final int idColHistoricalZ = cursor.getColumnIndex(ActionRecordTable.COLUMN_HISTORICAL_Z);
        final int idColHistoricalPressure = cursor.getColumnIndex(ActionRecordTable.COLUMN_HISTORICAL_PRESSURE);
        final int idColHistoricalOrientation = cursor.getColumnIndex(ActionRecordTable.COLUMN_HISTORICAL_ORIENTATION);
        final int idColHistoricalTilt = cursor.getColumnIndex(ActionRecordTable.COLUMN_HISTORICAL_TILT);
        ActionRecord ar = new ActionRecord();
        ar.setID(cursor.getInt(idColIdx));
        ar.setEventTime(cursor.getInt(idColEventTime));
        ar.setEventTimeNano(cursor.getInt(idColEventTimeNano));
        ar.setHistoricalEventTime(cursor.getInt(idColHistoricalEventTime));
        ar.setHistoricalEventTimeNano(cursor.getInt(idColHistoricalEventTimeNano));
        ar.setEventType(cursor.getString(idColEventType));
        ar.setToolType(cursor.getInt(idColToolType));
        ar.setMotionEventType(cursor.getInt(idColMotionEventType));
        ar.setActionMasked(cursor.getInt(idColActionMasked));
        ar.setAction(cursor.getString(idColAction));
        ar.setX(cursor.getInt(idColX));
        ar.setY(cursor.getInt(idColY));
        ar.setZ(cursor.getInt(idColZ));
        ar.setPressure(cursor.getInt(idColPressure));
        ar.setOrientation(cursor.getInt(idColOrientation));
        ar.setTilt(cursor.getInt(idColTilt));
        ar.setButtonState(cursor.getInt(idColButtonState));
        ar.setHistoricalX(cursor.getInt(idColHistoricalX));
        ar.setHistoricalY(cursor.getInt(idColHistoricalY));
        ar.setHistoricalZ(cursor.getInt(idColHistoricalZ));
        ar.setHistoricalPressure(cursor.getInt(idColHistoricalPressure));
        ar.setHistoricalOrientation(cursor.getInt(idColHistoricalOrientation));
        ar.setHistoricalTilt(cursor.getInt(idColHistoricalTilt));
        return ar;
    }

    public static List<ActionRecord> convertToListFromCursor(Cursor cursor){
        List<ActionRecord> list = new ArrayList<ActionRecord>();
        cursor.moveToFirst();
        do{
            list.add(convertFromCursor(cursor));
        } while(cursor.moveToNext());

        return list;
    }
}
