package org.tucantest.tucan_exercise_android;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import com.samsung.android.sdk.SsdkUnsupportedException;
import com.samsung.android.sdk.pen.Spen;
import com.samsung.android.sdk.pen.SpenSettingPenInfo;
import com.samsung.android.sdk.pen.SpenSettingRemoverInfo;
import com.samsung.android.sdk.pen.SpenSettingViewInterface;
import com.samsung.android.sdk.pen.document.SpenNoteDoc;
import com.samsung.android.sdk.pen.document.SpenPageDoc;
import com.samsung.android.sdk.pen.engine.SpenLayeredReplayListener;
import com.samsung.android.sdk.pen.engine.SpenReplayListener;
import com.samsung.android.sdk.pen.engine.SpenSimpleSurfaceView;
import com.samsung.android.sdk.pen.engine.SpenSurfaceView;

import org.tucantest.tucan_exercise_android.db.DatabaseHelper;
import org.tucantest.tucan_exercise_android.model.ActionRecord;
import org.tucantest.tucan_exercise_android.util.Player;
import org.tucantest.tucan_exercise_android.util.Recorder;
import org.tucantest.tucan_exercise_android.util.SharedPreferences;

import java.io.File;
import java.io.IOException;
import java.security.cert.Extension;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements View.OnTouchListener, View.OnHoverListener {

    public static String TAG = "TUCAN.MainActivity";
    public boolean recording = false;
    public int replayStatus = 0; // 0: stopped 1: playing 2: paused
    public int pausedAt = 0;
    public SpenSurfaceView mSpenSurfaceView;
    private SpenNoteDoc mSpenNoteDoc;
    private SpenPageDoc mSpenPageDoc;
    private Button btn_play;
    private Button btn_record;
    private Button btn_pause;
    private Button btn_delete;
    public List<ActionRecord> currentARList = new ArrayList<>();
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (this.dbHelper == null) {
            this.dbHelper = DatabaseHelper.getInstance(this);
        }

        this.setLocale(Locale.US);

        // Add the spen to this activity.
        addSpen();

        // Enable touch and hover listener.
        mSpenSurfaceView.setOnTouchListener(this);
        mSpenSurfaceView.setOnHoverListener(this);

        // Add button listeners
        btn_play = findViewById(R.id.btn_play);
        btn_record = findViewById(R.id.btn_record);
        btn_pause = findViewById(R.id.btn_pause);
        btn_delete = findViewById(R.id.btn_delete);
        btn_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (replayStatus == 0){
                    replayStatus = 1;
                    btn_play.setText("Stop Playing");
                    btn_pause.setVisibility(View.VISIBLE);
                    loadRecordFromCSV();
                } else if (replayStatus == 1){
                    resetButtons();
                    mSpenPageDoc = mSpenNoteDoc.appendPage();
                    mSpenPageDoc.setBackgroundColor(0xFFFFFFFF);
                    mSpenSurfaceView.setPageDoc(mSpenPageDoc, true);
                } else if (replayStatus == 2){
                    resetButtons();
                    mSpenPageDoc = mSpenNoteDoc.appendPage();
                    mSpenPageDoc.setBackgroundColor(0xFFFFFFFF);
                    mSpenSurfaceView.setPageDoc(mSpenPageDoc, true);
                }
            }
        });
        btn_record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!recording){
                    recording = true;
                    btn_record.setText("Stop Recording");
                } else {
                    recording = false;
                    btn_record.setText("Record");
                }
            }
        });
        btn_pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (replayStatus == 1){
                    replayStatus = 2;
                    btn_pause.setText("Resume");
                } else if (replayStatus == 2){
                    replayStatus = 1;
                    btn_pause.setText("Pause");
                    loadRecordFromCSV();
                }
            }
        });
        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteHistory();
            }
        });
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if (recording) {
            // Create a new record list
            List<ActionRecord> arList = new ArrayList<>();
            int historySize = motionEvent.getHistorySize();
            // Add main event
            arList.add(createRecord(motionEvent, "Touch", "Main", 0));
            // Add historical events
            if (historySize > 0) {
                for (int i = 0; i < historySize; i++) {
                    arList.add(createRecord(motionEvent, "Touch", "Historical", i));
                }
            }
            // Record the list
            Recorder.writeRecordInCSV(getApplicationContext(), arList);
        }
        return false;
    }


    @Override
    public boolean onHover(View view, MotionEvent motionEvent) {
        if (recording) {
            List<ActionRecord> arList = new ArrayList<>();
            int historySize = motionEvent.getHistorySize();
            // Add main event
            arList.add(createRecord(motionEvent, "Hover", "Main", 0));
            // Add historical events
            if (historySize > 0) {
                for (int i = 0; i < historySize; i++) {
                    arList.add(createRecord(motionEvent, "Hover", "Historical", i));
                }
            }
            // Record the list
            Recorder.writeRecordInCSV(getApplicationContext(), arList);
        }
        return false;
    }

    /**
     * Change locale to the target locale.
     *
     * @param targetLocale
     */
    private void setLocale(Locale targetLocale) {
        // Change locale to US in order to use correct decimal in floating point numbers.
        Resources resources = getResources();
        Configuration configuration = resources.getConfiguration();
        DisplayMetrics displayMetrics = resources.getDisplayMetrics();
        configuration.setLocale(Locale.US);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            configuration.setLocale(targetLocale);
        } else {
            configuration.locale = targetLocale;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            getApplicationContext().createConfigurationContext(configuration);
        } else {
            resources.updateConfiguration(configuration, displayMetrics);
        }
    }


    /**
     * Adds the Spen capability to this activity.
     */
    public void addSpen() {
        Context myContext = getApplicationContext();
        // Initialize Spen
        boolean isSpenFeatureEnabled = false;
        Spen spenPackage = new Spen();
        try {
            spenPackage.initialize(this);
            isSpenFeatureEnabled = spenPackage.isFeatureEnabled(Spen.DEVICE_PEN);
        } catch (SsdkUnsupportedException e) {

        } catch (Exception e1) {
            Toast.makeText(myContext, "Cannot initialize Spen.",
                    Toast.LENGTH_SHORT).show();
            e1.printStackTrace();
            finish();
        }
        mSpenSurfaceView = new SpenSurfaceView(myContext);
        if (mSpenSurfaceView == null) {
            Toast.makeText(myContext, "Cannot create new SpenView.",
                    Toast.LENGTH_SHORT).show();
            finish();
        }

        // Disable scroll and zoom.
        mSpenSurfaceView.setZoomable(false);
        mSpenSurfaceView.setScrollBarEnabled(false);
        mSpenSurfaceView.setNestedScrollingEnabled(false);

        // Set pen setting:
        SpenSettingPenInfo penSettings = new SpenSettingPenInfo(mSpenSurfaceView.getPenSettingInfo());
        Log.v(TAG, String.format("penSettings=%s", penSettings.toString()));
        penSettings.name = "com.samsung.android.sdk.pen.pen.preload.Pencil";
        penSettings.size = 6f;
        mSpenSurfaceView.setPenSettingInfo(penSettings);

        ConstraintLayout layout = findViewById(R.id.mainLayout);
        layout.addView(mSpenSurfaceView);
        // Put spen surface view on top of everything.
        mSpenSurfaceView.setZOrderOnTop(false);
        // Get the dimension of the device screen.
        Display display = getWindowManager().getDefaultDisplay();
        Rect rect = new Rect();
        display.getRectSize(rect);
        mSpenNoteDoc = null;

        // Create SpenNoteDoc
        try {
            mSpenNoteDoc =
                    new SpenNoteDoc(myContext, rect.width(), rect.height());
        } catch (IOException e) {
            Toast.makeText(myContext, "Cannot create new NoteDoc.",
                    Toast.LENGTH_SHORT).show();
            e.printStackTrace();
            finish();
        } catch (Exception e) {
            e.printStackTrace();
            finish();
        }

        // Create new page.
        mSpenPageDoc = mSpenNoteDoc.appendPage();
        mSpenPageDoc.setBackgroundColor(0xFFFFFFFF);
        mSpenPageDoc.clearHistory();
        // Set PageDoc to View
        mSpenSurfaceView.setPageDoc(mSpenPageDoc, true);

        // Enable finger and pen.
        mSpenSurfaceView.setToolTypeAction(SpenSimpleSurfaceView.TOOL_FINGER, SpenSettingViewInterface.ACTION_STROKE);
        mSpenSurfaceView.setToolTypeAction(SpenSimpleSurfaceView.TOOL_SPEN, SpenSettingViewInterface.ACTION_STROKE);
        mSpenSurfaceView.setToolTypeAction(SpenSimpleSurfaceView.TOOL_UNKNOWN, SpenSettingViewInterface.ACTION_STROKE);

        // Initialize Eraser settings
        SpenSettingRemoverInfo removerInfo = new SpenSettingRemoverInfo();
        removerInfo.size = 1;
        removerInfo.type = SpenSettingRemoverInfo.CUTTER_TYPE_CUT;
        mSpenSurfaceView.setRemoverSettingInfo(removerInfo);

    }


    /**
     * This function creates action records
     *
     * @param e
     * @param action touch/hover;
     * @param eventType main/historical
     * @param pos history position; Only used for historical records
     * @return
     */
    public ActionRecord createRecord(MotionEvent e, String action, String eventType, int pos){
        ActionRecord ar = new ActionRecord();
        // Set common value
        int lastID = SharedPreferences.getInstance(getApplicationContext()).getLastId() + 1;
        ar.setID(lastID);
        ar.setAction(action);
        ar.setEventType(eventType);
        int pointerNum = e.getPointerCount();
        ar.setToolType(e.getToolType(0));
        ar.setMotionEventType(e.getAction());
        ar.setActionMasked(e.getActionMasked());
        ar.setButtonState(e.getButtonState());
        // Main record
        if (eventType.equals("Main")) {

            ar.setX(e.getX());
            ar.setY(e.getY());
            ar.setZ(e.getAxisValue(MotionEvent.AXIS_DISTANCE));
            ar.setTilt(e.getAxisValue(MotionEvent.AXIS_TILT));
            ar.setEventTime(e.getEventTime());
            ar.setEventTimeNano(MotionEventExtensions.getEventTimeNano(e));
            ar.setPressure(e.getPressure());
            ar.setOrientation(e.getOrientation());
        }
        // Historical record
        else if (eventType.equals("Historical")){
            ar.setHistoricalX(e.getHistoricalX(pos));
            ar.setHistoricalY(e.getHistoricalY(pos));
            ar.setHistoricalZ(e.getHistoricalAxisValue(MotionEvent.AXIS_DISTANCE, pos));
            ar.setHistoricalTilt(e.getHistoricalAxisValue(MotionEvent.AXIS_TILT, pos));
            ar.setHistoricalEventTime(e.getHistoricalEventTime(pos));
            ar.setHistoricalEventTimeNano(MotionEventExtensions.getHistoricalEventTimeNano(e, pos));
            ar.setHistoricalPressure(e.getHistoricalPressure(pos));
            ar.setHistoricalOrientation(e.getHistoricalOrientation(pos));
        }
        else{
            Log.e("Error", "Record type not found!");
        }
        return ar;
    }

    /**
     * Load CSV in background
     */
    public class loadRecordFromCSV extends AsyncTask<Void, Void, List<ActionRecord>> {

        @Override
        protected List<ActionRecord> doInBackground(Void... voids) {
            currentARList = Player.readFromCSV(getApplicationContext());
            return currentARList;
        }

        @Override
        protected void onPostExecute(final List<ActionRecord> currentARList) {
            super.onPostExecute(currentARList);
            if (currentARList != null) {
                Toast.makeText(getApplicationContext(), currentARList.size() + " actions found!", Toast.LENGTH_SHORT).show();
//                MotionEvent.PointerProperties pp = new MotionEvent.PointerProperties();
//                pp.id = 0;
//                pp.toolType = MotionEvent.TOOL_TYPE_STYLUS;
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        for (int i=pausedAt; i<currentARList.size(); i++, pausedAt++){
                            if (replayStatus!=1)
                                break;
                            ActionRecord ar = currentARList.get(i);
                            convertHoverToTouch(ar);
                            long downTime = SystemClock.uptimeMillis();
                            long eventTime = SystemClock.uptimeMillis() + 100;
                            if (ar.getEventType().equals("Main")) {
                                MotionEvent motionEvent = MotionEvent.obtain(
                                        downTime,
                                        eventTime,
                                        ar.getMotionEventType(),
                                        ar.getX(),
                                        ar.getY(),
                                        ar.getPressure(),
                                        1.0f,
                                        0,
                                        0.01f,
                                        0.01f,
                                        0,
                                        0
                                );
                                if (ar.getAction().equals("Hover")) {
                                    setPenColor(generateGradientColor(Color.valueOf(Color.BLUE), Color.valueOf(Color.CYAN), ar.getZ())); // plus an offset for color gradient
                                    mSpenSurfaceView.dispatchTouchEvent(motionEvent);
                                }
                                else if (ar.getAction().equals("Touch")) {
                                    setPenColor(Color.BLACK);
                                    mSpenSurfaceView.dispatchTouchEvent(motionEvent);
                                }
                            } else{
                                MotionEvent motionEvent = MotionEvent.obtain(
                                        downTime,
                                        eventTime,
                                        ar.getMotionEventType(),
                                        ar.getHistoricalX(),
                                        ar.getHistoricalY(),
                                        ar.getPressure(),
                                        1.0f,
                                        0,
                                        0.01f,
                                        0.01f,
                                        0,
                                        0
                                );
                                if (ar.getAction().equals("Hover")) {
                                    setPenColor(generateGradientColor(Color.valueOf(Color.BLUE), Color.valueOf(Color.CYAN), ar.getHistoricalZ())); // plus an offset for color gradient
                                    mSpenSurfaceView.dispatchTouchEvent(motionEvent);
                                }
                                else if (ar.getAction().equals("Touch")) {
                                    setPenColor(Color.BLACK);
                                    mSpenSurfaceView.dispatchTouchEvent(motionEvent);
                                }
                            }
                        }
                        // reset if replay completed
                        if (pausedAt == currentARList.size())
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    resetButtons();
                                }
                            });
                    }
                }).start();
            }
            else
                Toast.makeText(getApplicationContext(), "No actions found!", Toast.LENGTH_SHORT).show();
        }
    }

    // load in background and play
    private void loadRecordFromCSV(){
        new loadRecordFromCSV().execute();
    }

    // hover to touch so it can be visualized. 9 7 10 --> 0 2 1
    private void convertHoverToTouch(ActionRecord arHover){
        if (arHover.getMotionEventType()==9)
            arHover.setMotionEventType(0);
        else if (arHover.getMotionEventType()==7)
            arHover.setMotionEventType(2);
        else if (arHover.getMotionEventType()==10)
            arHover.setMotionEventType(1);
    }

    // set a color for the pen to distinguish different actions
    private void setPenColor(int color){
        SpenSettingPenInfo penSettings = new SpenSettingPenInfo(mSpenSurfaceView.getPenSettingInfo());
        penSettings.name = "com.samsung.android.sdk.pen.pen.preload.Pencil";
        penSettings.size = 6f;
        penSettings.color = color;
        mSpenSurfaceView.setPenSettingInfo(penSettings);
    }

    // reset all the states and visibility
    private void resetButtons(){
        pausedAt = 0;
        replayStatus = 0;
        recording = false;
        btn_play.setText("Play");
        btn_record.setText("Record");
        btn_pause.setVisibility(View.GONE);
    }

    // delete history (csv file, shared preference, db, etc...)
    private void deleteHistory(){
        File csvFile = new File(getFilesDir().getAbsolutePath() + File.separator + "new_csv_file" + ".csv");
        if (csvFile.exists())
            csvFile.delete();
        File spFile= new File("/data/data/"+getPackageName()+"/shared_prefs","org.tucantest.tucan_exercise_android_preferences.xml");
        if (spFile.exists())
            spFile.delete();
        Toast.makeText(this, "History deleted!", Toast.LENGTH_SHORT).show();
    }

    private int generateGradientColor(Color c1, Color c2, float distance){
        float ratio = (float)distance / (float)50;
        int red = (int)(c2.red() * ratio + c1.red() * (1 - ratio));
        int green = (int)(c2.green() * ratio + c1.green() * (1 - ratio));
        int blue = (int)(c2.blue() * ratio + c1.blue() * (1 - ratio));
        Color c = Color.valueOf(red, green, blue);
        return c.toArgb();
    }
}
