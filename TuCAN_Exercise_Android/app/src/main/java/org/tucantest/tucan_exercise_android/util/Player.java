package org.tucantest.tucan_exercise_android.util;

import android.animation.ArgbEvaluator;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import org.tucantest.tucan_exercise_android.db.ActionRecordTable;
import org.tucantest.tucan_exercise_android.db.CursorToActionRecordConverter;
import org.tucantest.tucan_exercise_android.db.DatabaseHelper;
import org.tucantest.tucan_exercise_android.model.ActionRecord;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Player {
    public static List<ActionRecord> readFromCSV(Context context){
        List<ActionRecord> arList = new ArrayList<>();
        String path = context.getFilesDir().getAbsolutePath() + File.separator + "new_csv_file" + ".csv";
        File file = new File(path);
        FileInputStream fileInputStream;
        Scanner in;
        try {
            fileInputStream = new FileInputStream(file);
            in = new Scanner(fileInputStream, "UTF-8");
            in.nextLine();
            while (in.hasNextLine()) {
                String[] lines = in.nextLine().split(";");
                ActionRecord ar = new ActionRecord();
                // ID;EventTime;EventTimeNano;HistoricalEventTime;HistoricalEventTimeNano;EventType;ToolType;MotionEventType;ActionMasked;Action;X;Y;Z;Pressure;Orientation;Tilt;ButtonState;HistoricalX;HistoricalY;HistoricalZ;HistoricalPressure;HistoricalOrientation;HistoricalTilt
                ar.setID(Integer.parseInt(lines[0]));
                ar.setEventTime(Long.parseLong(lines[1]));
                ar.setEventTimeNano(Long.parseLong(lines[2]));
                ar.setHistoricalEventTime(Long.parseLong(lines[3]));
                ar.setHistoricalEventTimeNano(Long.parseLong(lines[4]));
                ar.setEventType(lines[5]);
                ar.setToolType(Integer.parseInt(lines[6]));
                ar.setMotionEventType(Integer.parseInt(lines[7]));
                ar.setActionMasked(Integer.parseInt(lines[8]));
                ar.setAction(lines[9]);
                ar.setX(Float.parseFloat(lines[10]));
                ar.setY(Float.parseFloat(lines[11]));
                ar.setZ(Float.parseFloat(lines[12]));
                ar.setPressure(Float.parseFloat(lines[13]));
                ar.setOrientation(Float.parseFloat(lines[14]));
                ar.setTilt(Float.parseFloat(lines[15]));
                ar.setButtonState(Integer.parseInt(lines[16]));
                ar.setHistoricalX(Float.parseFloat(lines[17]));
                ar.setHistoricalY(Float.parseFloat(lines[18]));
                ar.setHistoricalZ(Float.parseFloat(lines[19]));
                ar.setHistoricalPressure(Float.parseFloat(lines[20]));
                ar.setHistoricalOrientation(Float.parseFloat(lines[21]));
                ar.setHistoricalTilt(Float.parseFloat(lines[22]));
                arList.add(ar);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return arList;
    }

    public static List<ActionRecord> readFromDB(Context context){
        DatabaseHelper dbHelper;
        dbHelper = DatabaseHelper.getInstance(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        List<ActionRecord> arList = new ArrayList<>();

        String selection = null;
        String[] selectionArgs = null;
        final Cursor c = db.query(ActionRecordTable.TABLE_NAME, ActionRecordTable.fullProjection, selection, selectionArgs, null, null, null);
        if( c != null && c.getCount() > 0 ){
            arList = CursorToActionRecordConverter.convertToListFromCursor(c);
        }
        else{
            Log.d("TAG", "DEBUG: MainActivity: (ActionRecord) Database is empty!");
            arList = null;
        }
        if (c != null)
            c.close();
        return arList;

    }
}
