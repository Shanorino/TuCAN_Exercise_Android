package com.samsung.android.sdk.pen.pg.example1_1;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.lang.reflect.Method;
import java.sql.Timestamp;
import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.samsung.android.sdk.SsdkUnsupportedException;
import com.samsung.android.sdk.pen.Spen;
import com.samsung.android.sdk.pen.document.SpenNoteDoc;
import com.samsung.android.sdk.pen.document.SpenObjectBase;
import com.samsung.android.sdk.pen.document.SpenObjectStroke;
import com.samsung.android.sdk.pen.document.SpenPageDoc;
import com.samsung.android.sdk.pen.engine.SpenSimpleSurfaceView;
import com.samsung.android.sdk.pen.engine.SpenTouchListener;
import com.samsung.spensdk4light.example.R;

public class PenSample1_1_HelloPen extends Activity {

    public ArrayList<MyMotionEvent> _events = new ArrayList<MyMotionEvent>();

    private Context mContext;
    private SpenNoteDoc mSpenNoteDoc;
    private SpenPageDoc mSpenPageDoc;
    private SpenSimpleSurfaceView mSpenSimpleSurfaceView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hello_pen);
        mContext = this;

        // Initialize Spen
        boolean isSpenFeatureEnabled = false;
        Spen spenPackage = new Spen();
        try {
            spenPackage.initialize(this);
            isSpenFeatureEnabled = spenPackage.isFeatureEnabled(Spen.DEVICE_PEN);

        } catch (SsdkUnsupportedException e) {
            if (processUnsupportedException(e) == true) {
                return;
            }
        } catch (Exception e1) {
            Toast.makeText(mContext, "Cannot initialize Spen.",
                    Toast.LENGTH_SHORT).show();
            e1.printStackTrace();
            finish();
        }

        // Create Spen View
        RelativeLayout spenViewLayout =
                (RelativeLayout) findViewById(R.id.spenViewLayout);
        mSpenSimpleSurfaceView = new SpenSimpleSurfaceView(mContext);
        if (mSpenSimpleSurfaceView == null) {
            Toast.makeText(mContext, "Cannot create new SpenView.",
                    Toast.LENGTH_SHORT).show();
            finish();
        }
        spenViewLayout.addView(mSpenSimpleSurfaceView);
        mSpenSimpleSurfaceView.setTouchListener(onSPenListener);
        mSpenSimpleSurfaceView.setOnHoverListener(onHoverListener);


        // Get the dimension of the device screen.
        Display display = getWindowManager().getDefaultDisplay();
        Rect rect = new Rect();
        display.getRectSize(rect);
        // Create SpenNoteDoc
        try {
            mSpenNoteDoc =
                    new SpenNoteDoc(mContext, rect.width(), rect.height());
        } catch (IOException e) {
            Toast.makeText(mContext, "Cannot create new NoteDoc.",
                    Toast.LENGTH_SHORT).show();
            e.printStackTrace();
            finish();
        } catch (Exception e) {
            e.printStackTrace();
            finish();
        }
        // Add a Page to NoteDoc, get an instance, and set it to the member variable.
        mSpenPageDoc = mSpenNoteDoc.appendPage();
        mSpenPageDoc.setBackgroundColor(0xFFD6E6F5);
        mSpenPageDoc.clearHistory();
        // Set PageDoc to View.
        mSpenSimpleSurfaceView.setPageDoc(mSpenPageDoc, true);


        if (isSpenFeatureEnabled == false) {
            mSpenSimpleSurfaceView.setToolTypeAction(SpenSimpleSurfaceView.TOOL_FINGER, SpenSimpleSurfaceView.ACTION_STROKE);
            Toast.makeText(mContext,
                    "Device does not support Spen. \n You can draw stroke by finger.",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private boolean processUnsupportedException(SsdkUnsupportedException e) {

        e.printStackTrace();
        int errType = e.getType();
        // If the device is not a Samsung device or if the device does not support Pen.
        if (errType == SsdkUnsupportedException.VENDOR_NOT_SUPPORTED
                || errType == SsdkUnsupportedException.DEVICE_NOT_SUPPORTED) {
            Toast.makeText(mContext, "This device does not support Spen.",
                    Toast.LENGTH_SHORT).show();
            finish();
        } else if (errType == SsdkUnsupportedException.LIBRARY_NOT_INSTALLED) {
            // If SpenSDK APK is not installed.
            showAlertDialog("You need to install additional Spen software"
                            + " to use this application."
                            + "You will be taken to the installation screen."
                            + "Restart this application after the software has been installed."
                    , true);
        } else if (errType
                == SsdkUnsupportedException.LIBRARY_UPDATE_IS_REQUIRED) {
            // SpenSDK APK must be updated.
            showAlertDialog("You need to update your Spen software "
                            + "to use this application."
                            + " You will be taken to the installation screen."
                            + " Restart this application after the software has been updated."
                    , true);
        } else if (errType
                == SsdkUnsupportedException.LIBRARY_UPDATE_IS_RECOMMENDED) {
            // Update of SpenSDK APK to an available new version is recommended.
            showAlertDialog("We recommend that you update your Spen software"
                            + " before using this application."
                            + " You will be taken to the installation screen."
                            + " Restart this application after the software has been updated."
                    , false);
            return false;
        }
        return true;
    }

    private void showAlertDialog(String msg, final boolean closeActivity) {

        AlertDialog.Builder dlg = new AlertDialog.Builder(mContext);
        dlg.setIcon(getResources().getDrawable(
                android.R.drawable.ic_dialog_alert));
        dlg.setTitle("Upgrade Notification")
                .setMessage(msg)
                .setPositiveButton(android.R.string.yes,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(
                                    DialogInterface dialog, int which) {
                                // Go to the market website and install/update APK.
                                Uri uri = Uri.parse("market://details?id=" + Spen.getSpenPackageName());
                                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                                        | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);

                                dialog.dismiss();
                                finish();
                            }
                        })
                .setNegativeButton(android.R.string.no,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(
                                    DialogInterface dialog, int which) {
                                if (closeActivity == true) {
                                    // Terminate the activity if APK is not installed.
                                    finish();
                                }
                                dialog.dismiss();
                            }
                        })
                .setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        if (closeActivity == true) {
                            // Terminate the activity if APK is not installed.
                            finish();
                        }
                    }
                })
                .show();
        dlg = null;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mSpenSimpleSurfaceView != null) {
            mSpenSimpleSurfaceView.close();
            mSpenSimpleSurfaceView = null;
        }

        if (mSpenNoteDoc != null) {

            ArrayList<SpenObjectBase> strokes = mSpenPageDoc.getObjectList();

            String mStrokes = "Time;X;Y;Pressure;Tilt;Type\n";
            for (SpenObjectBase s : strokes) {
                SpenObjectStroke stroke = (SpenObjectStroke) s;

                for (int i = 0; i < stroke.getTimeStamps().length; i++) {
                    float x = stroke.getXPoints()[i];
                    float y = stroke.getYPoints()[i];
                    float pres = stroke.getPressures()[i];
                    float tilt = stroke.getTilts()[i];
                    long timeStamp = stroke.getTimeStamps()[i];
                    boolean visible = stroke.isVisible();

                    Log.v(TAG, "Stroke[" + i + "] " + timeStamp + "; " + x + "; " + y + "; " + pres + "; " + tilt + "; " + stroke.isVisible());
                    mStrokes += timeStamp + ";" + x + ";" + y + ";" + pres + ";" + tilt + ";STROKE;PAGEDOC_STROKE;" + visible + "\n";
                }

            }
            writeToFile(mStrokes, System.currentTimeMillis() + "_strokes.csv", this);

            try {
                mSpenNoteDoc.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            mSpenNoteDoc = null;

            String s = "Time;X;Y;Pressure;Tilt;Type\n";
            // save events
            for (MyMotionEvent event : _events) {
                s += event.toCSV() + "\n";

            }
            writeToFile(s, System.currentTimeMillis() + "_events.csv", this);

        }
    }

    ;
    static final File mocaPath = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "MOCA");

    private void writeToFile(String data, String name, Context context) {
        BufferedWriter buffWriter;

        try {
            buffWriter = new BufferedWriter(new FileWriter(mocaPath.getPath() + "/" + name));
            buffWriter.write(data);
            buffWriter.close();
        } catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

    public static String TAG = "SPEN_Heiko";

    private View.OnHoverListener onHoverListener = new View.OnHoverListener() {
        @Override
        public boolean onHover(View v, MotionEvent event) {
            // TODO Auto-generated method stub
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                case MotionEvent.ACTION_POINTER_DOWN:
                    break;
                case MotionEvent.ACTION_CANCEL:
                case MotionEvent.ACTION_UP:
                    break;
            }

            for (int pos = 0; pos < event.getHistorySize(); pos++) {
                float histX = event.getHistoricalX(pos);
                float histY = event.getHistoricalY(pos);
                long histTime = getHistoricalEventTimeNano(event, pos);// event.getHistoricalEventTimeNano(pos);
                float histPresure = event.getHistoricalPressure(pos);
                //Log.v(TAG, "Hover_Hist " + pos + ": " + histTime + "; " + histX + "; " + histY + "; " + histPresure);

                _events.add(new MyMotionEvent(histTime, histX, histY, histPresure, -1, event.getAction(), "Historical"));
            }

            //Log.v(TAG, "Hover_Main: " + event.getEventTime() + "; " + event.getX() + "; " + event.getY() + "; " + event.getPressure());

            StringBuilder sb= new StringBuilder();
            sb.append("Hover --");
            for(int i= 0; i < 47;i++){
                sb.append("Axis " +i+ ": " + event.getAxisValue(i)+ "; ");
            }
            Log.v(TAG, sb.toString());

            _events.add(new MyMotionEvent(getEventTimeNano(event), event.getX(), event.getY(), event.getPressure(), -1, event.getAction(), "Main"));
            return false;
        }
    };


    private double getReflectionField(MotionEvent event, String field) {
        double ret = -1;
        try {
            Class cls = Class.forName("android.view.MotionEvent");
            Method myMethod = cls.getMethod(field);
            ret =(double) myMethod.invoke(event);
        } catch (Exception e) {
            Log.i("WALT.MsMotionEvent", e.getMessage());
        }

        return ret;
    }

    private double getEventTilt(MotionEvent event) {
        double tilt = -1;
        try {
            Class cls = Class.forName("android.view.MotionEvent");
            Method myMethod = cls.getMethod("getTiltX");
            tilt =(double) myMethod.invoke(event);
        } catch (Exception e) {
            Log.i("WALT.MsMotionEvent", e.getMessage());
        }

        return tilt;
    }

    private long getEventTimeNano(MotionEvent event) {
        long t_nanos = -1;
        try {
            Class cls = Class.forName("android.view.MotionEvent");
            Method myTimeGetter = cls.getMethod("getEventTimeNano");
            t_nanos = (long) myTimeGetter.invoke(event);
        } catch (Exception e) {
            Log.i("WALT.MsMotionEvent", e.getMessage());
        }

        return t_nanos;
    }

    private long getHistoricalEventTimeNano(MotionEvent event, int pos) {
        long t_nanos = -1;
        try {
            Class cls = Class.forName("android.view.MotionEvent");
            Method myTimeGetter = cls.getMethod("getHistoricalEventTimeNano", new Class[]{int.class});
            t_nanos = (long) myTimeGetter.invoke(event, new Object[]{pos});
        } catch (Exception e) {
            Log.i("WALT.MsMotionEvent", e.getMessage());
        }

        return t_nanos;
    }

    private SpenTouchListener onSPenListener = new SpenTouchListener() {

        @Override
        public boolean onTouch(View view, MotionEvent event) {
            // TODO Auto-generated method stub
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                case MotionEvent.ACTION_POINTER_DOWN:
                    break;
                case MotionEvent.ACTION_CANCEL:
                case MotionEvent.ACTION_UP:
                    break;
            }

            for (int pos = 0; pos < event.getHistorySize(); pos++) {
                float histX = event.getHistoricalX(pos);
                float histY = event.getHistoricalY(pos);
                long histTime = getHistoricalEventTimeNano(event,pos);// event.getHistoricalEventTime(pos);
                float histPresure = event.getHistoricalPressure(pos);
                // Log.v(TAG, "Hist " + pos + ": " + histTime + "; " + histX + "; " + histY + "; " + histPressure);
                _events.add(new MyMotionEvent(histTime, histX, histY, histPresure, -1, event.getAction(), "Historical"));

            }

            // Log.v(TAG, "Main: " + event.getEventTime() + "; " + event.getX() + "; " + event.getY() + "; " + event.getPressure());
            _events.add(new MyMotionEvent(getEventTimeNano(event), event.getX(), event.getY(), event.getPressure(), -1, event.getAction(), "Main"));

            StringBuilder sb= new StringBuilder();
            for(int i= 0; i < 47;i++){
                sb.append("Axis " +i+ ": " + event.getAxisValue(i)+ "; ");
            }
            Log.v(TAG, sb.toString());
            // Log.v(TAG,"getOrientation:" + event.getOrientation() + " <-> getAxisValue(8): " + event.getAxisValue(8));
            return false;
        }
    };

    public class MyMotionEvent {
        public float X;
        public float Y;
        public float Pressure;
        public long TimeStamp;
        public float Tilt;
        public int Type;
        public String MainOrHist;

        public MyMotionEvent(long timeStamp, float x, float y, float pressure, float tilt, int type, String mainOrHist) {
            TimeStamp = timeStamp;
            X = x;
            Y = y;
            Pressure = pressure;
            Tilt = tilt;
            Type = type;
            MainOrHist = mainOrHist;

        }

        public String toCSV() {
            return TimeStamp + ";" + X + ";" + Y + ";" + Pressure + ";" + Tilt + ";" + Type + ";" + MainOrHist;
        }
    }
}


