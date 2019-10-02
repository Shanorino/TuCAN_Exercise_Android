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

        ActionRecord ar = new ActionRecord();
        ar.setID(cursor.getInt(idColIdx));
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
