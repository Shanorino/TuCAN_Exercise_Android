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
    public class BackgroundUpdateAllHistoryDB extends AsyncTask<Void, Void, List<ActionRecord>> {
        public Context context;
        public List<ActionRecord> arList;
        public final ContentResolver resolver;

        public
        BackgroundUpdateAllHistoryDB(Context context, List < ActionRecord > arList)
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
