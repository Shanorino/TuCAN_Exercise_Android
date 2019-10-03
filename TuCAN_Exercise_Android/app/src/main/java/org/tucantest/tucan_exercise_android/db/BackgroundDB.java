package org.tucantest.tucan_exercise_android.db;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;

import org.tucantest.tucan_exercise_android.model.ActionRecord;
import org.tucantest.tucan_exercise_android.tools.Consts;

import java.util.List;

public class BackgroundDB {
    public class BackgroundUpdateAllRecordDB extends AsyncTask<Void, Void, List<ActionRecord>> {
        public Context context;
        public List<ActionRecord> arList;
        public final ContentResolver resolver;

        public
        BackgroundUpdateAllRecordDB(Context context, List < ActionRecord > arList)
        {
            this.context = context;
            this.arList = arList;
            resolver = context.getContentResolver();
        }

        @Override
        protected List<ActionRecord> doInBackground (Void...voids){
            // write into Content Provider
            for (int i = 0; i < arList.size(); i++) {
                ContentValues values = new ContentValues();
                // 23 columns in total...
                values.put(ActionRecordTable.COLUMN_ID, arList.get(i).getID());
                values.put(ActionRecordTable.COLUMN_EVENT_TIME, arList.get(i).getEventTime());
                values.put(ActionRecordTable.COLUMN_EVENT_TIME_NANO, arList.get(i).getEventTimeNano());
                values.put(ActionRecordTable.COLUMN_HISTORICAL_EVENT_TIME, arList.get(i).getHistoricalEventTime());
                values.put(ActionRecordTable.COLUMN_HISTORICAL_EVENT_TIME_NANO, arList.get(i).getHistoricalEventTimeNano());
                values.put(ActionRecordTable.COLUMN_EVENT_TYPE, arList.get(i).getEventType());
                values.put(ActionRecordTable.COLUMN_TOOL_TYPE, arList.get(i).getToolType());
                values.put(ActionRecordTable.COLUMN_MOTION_EVENT_TYPE, arList.get(i).getMotionEventType());
                values.put(ActionRecordTable.COLUMN_ACTION_MASKED, arList.get(i).isActionMasked());
                values.put(ActionRecordTable.COLUMN_ACTION, arList.get(i).getAction());
                values.put(ActionRecordTable.COLUMN_X, arList.get(i).getX());
                values.put(ActionRecordTable.COLUMN_Y, arList.get(i).getY());
                values.put(ActionRecordTable.COLUMN_Z, arList.get(i).getZ());
                values.put(ActionRecordTable.COLUMN_PRESSURE, arList.get(i).getPressure());
                values.put(ActionRecordTable.COLUMN_ORIENTATION, arList.get(i).getOrientation());
                values.put(ActionRecordTable.COLUMN_TILT, arList.get(i).getTilt());
                values.put(ActionRecordTable.COLUMN_BUTTON_STATE, arList.get(i).getButtonState());
                values.put(ActionRecordTable.COLUMN_HISTORICAL_X, arList.get(i).getHistoricalX());
                values.put(ActionRecordTable.COLUMN_HISTORICAL_Y, arList.get(i).getHistoricalY());
                values.put(ActionRecordTable.COLUMN_HISTORICAL_Z, arList.get(i).getHistoricalZ());
                values.put(ActionRecordTable.COLUMN_HISTORICAL_PRESSURE, arList.get(i).getHistoricalPressure());
                values.put(ActionRecordTable.COLUMN_HISTORICAL_ORIENTATION, arList.get(i).getHistoricalOrientation());
                values.put(ActionRecordTable.COLUMN_HISTORICAL_TILT, arList.get(i).getHistoricalTilt());
                Uri uri = Uri.parse(Consts.CONTENT_PROVIDER_URL_RECORD);
                Uri _uri = resolver.insert(uri, values);
                if (_uri == null) {
                    // insertion failed. Impossible
                }
            }
            return arList;
        }

        @Override
        protected void onPostExecute (List < ActionRecord > historyInfos) {
            super.onPostExecute(historyInfos);

        }
    }
}
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