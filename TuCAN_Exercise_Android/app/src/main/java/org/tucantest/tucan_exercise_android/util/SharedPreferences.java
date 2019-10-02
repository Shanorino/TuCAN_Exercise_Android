package org.tucantest.tucan_exercise_android.util;

import android.content.Context;
import android.preference.PreferenceManager;

import java.io.File;
import java.security.acl.LastOwnerException;

public class SharedPreferences {
    private static SharedPreferences mSharedPreferences = null;
    private android.content.SharedPreferences sharedPreferences = null;

    public static final String LAST_ID = "org.tucantest.lastid";
    private Context mContext;

    private SharedPreferences(Context context) {
        //sharedPreferences = context.getSharedPreferences("my_preferences", Context.MODE_MULTI_PROCESS);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        mContext = context;
    }

    public static SharedPreferences getInstance(Context context) {
        if(mSharedPreferences == null) {
            mSharedPreferences = new SharedPreferences(context);
        }
        return mSharedPreferences;
    }


    public void setLastId(int value)  {
        android.content.SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(LAST_ID, value);
        editor.commit();
    }

    public int getLastId() {
        return sharedPreferences.getInt(LAST_ID, -1);
    }


    public static void clearAll(Context context) {
        {
            File dir = new File(context.getFilesDir().getParent() + "/shared_prefs/");
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                // clear each of the prefrances
                context.getSharedPreferences(children[i].replace(".xml", ""), Context.MODE_PRIVATE).edit().clear().commit();
            }
            // Make sure it has enough time to save all the commited changes
            try { Thread.sleep(1000); } catch (InterruptedException e) {}
            for (int i = 0; i < children.length; i++) {
                // delete the files
                new File(dir, children[i]).delete();
            }
        }
    }
}
