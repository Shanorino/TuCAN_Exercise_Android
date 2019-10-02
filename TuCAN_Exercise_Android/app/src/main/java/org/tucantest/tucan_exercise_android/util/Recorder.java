package org.tucantest.tucan_exercise_android.util;

import android.content.Context;

import org.tucantest.tucan_exercise_android.model.ActionRecord;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;

public class Recorder {
    public static void writeRecordInCSV(Context context, List<ActionRecord> arList){
        //TODO: Write in background for better performance
        try {
            File file = new File(context.getFilesDir().getAbsolutePath() + File.separator + "new_csv_file" + ".csv");
            // Add title
            // ID;EventTime;EventTimeNano;HistoricalEventTime;HistoricalEventTimeNano;EventType;ToolType;MotionEventType;ActionMasked;Action;X;Y;Z;Pressure;Orientation;Tilt;ButtonState;HistoricalX;HistoricalY;HistoricalZ;HistoricalPressure;HistoricalOrientation;HistoricalTilt
            if (!file.exists()) {
                BufferedWriter bw = new BufferedWriter(new FileWriter(file, true));
                bw.write("ID" + ";" + "EventTime" + ";" + "EventTimeNano" + ";"
                        + "HistoricalEventTime" + ";" + "HistoricalEventTimeNano" + ";"
                        + "EventType" + ";" + "ToolType" + ";" + "MotionEventType" + ";" + "ActionMasked" + ";" + "Action" + ";"
                        + "X" + ";" + "Y" + ";" + "Z" + ";"
                        + "Pressure" + ";" + "Orientation" + ";" + "Tilt" + ";" + "ButtonState" + ";"
                        + "HistoricalX" + ";" + "HistoricalY" + ";" + "HistoricalZ" + ";"
                        + "HistoricalPressure" + ";" + "HistoricalOrientation" + ";" + "HistoricalTilt" + ";");
                bw.newLine();
                bw.close();
            }
            BufferedWriter bw = new BufferedWriter(new FileWriter(file, true));
            // Add content
            for (int i = 0; i < arList.size(); i++) {
                bw.write(arList.get(i).getID() + ";" + arList.get(i).getEventTime() + ";" + arList.get(i).getEventTimeNano() + ";"
                        + arList.get(i).getHistoricalEventTime() + ";" + arList.get(i).getHistoricalEventTimeNano() + ";"
                        + arList.get(i).getEventType() + ";" + arList.get(i).getToolType() + ";" + arList.get(i).getMotionEventType() + ";" + arList.get(i).isActionMasked() + ";" + arList.get(i).getAction() + ";"
                        + arList.get(i).getX() + ";" + arList.get(i).getY() + ";" + arList.get(i).getZ() + ";"
                        + arList.get(i).getPressure() + ";" + arList.get(i).getOrientation() + ";" + arList.get(i).getTilt() + ";" + arList.get(i).getButtonState() + ";"
                        + arList.get(i).getHistoricalX() + ";" + arList.get(i).getHistoricalY() + ";" + arList.get(i).getHistoricalZ() + ";"
                        + arList.get(i).getHistoricalPressure() + ";" + arList.get(i).getHistoricalOrientation() + ";" + arList.get(i).getHistoricalTilt() + ";");
                bw.newLine();
            }
            bw.close();
            // last id += 1
            SharedPreferences.getInstance(context).setLastId( SharedPreferences.getInstance(context).getLastId() + 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void writeRecordIntoDB(List<ActionRecord> arList){

    }

    public static int readLastID(Context context){
        return -1;
    }
}
