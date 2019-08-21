package org.tucantest.tucan_exercise_android;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Toast;


import com.samsung.android.sdk.SsdkUnsupportedException;
import com.samsung.android.sdk.pen.Spen;
import com.samsung.android.sdk.pen.SpenSettingPenInfo;
import com.samsung.android.sdk.pen.SpenSettingRemoverInfo;
import com.samsung.android.sdk.pen.SpenSettingViewInterface;
import com.samsung.android.sdk.pen.document.SpenNoteDoc;
import com.samsung.android.sdk.pen.document.SpenPageDoc;
import com.samsung.android.sdk.pen.engine.SpenSimpleSurfaceView;
import com.samsung.android.sdk.pen.engine.SpenSurfaceView;

import java.io.IOException;
import java.security.cert.Extension;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements View.OnTouchListener, View.OnHoverListener {

    public static String TAG = "TUCAN.MainActivity";

    public SpenSurfaceView mSpenSurfaceView;
    private SpenNoteDoc mSpenNoteDoc;
    private SpenPageDoc mSpenPageDoc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.setLocale(Locale.US);

        // Add the spen to this activity.
        addSpen();

        // Enable touch and hover listener.
        mSpenSurfaceView.setOnTouchListener(this);
        mSpenSurfaceView.setOnHoverListener(this);

    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {

        // Get event time in nano seconds.
        long historicalEventTime = MotionEventExtensions.getEventTimeNano(motionEvent);

        // TODO: Record MotionEvent in CSV and/or Database.
        // TODO: Record both, the "main" and all historical events as a separate line.
        return false;
    }


    @Override
    public boolean onHover(View view, MotionEvent motionEvent) {
        // Get event time in nano seconds.
        long historicalEventTime = MotionEventExtensions.getEventTimeNano(motionEvent);

        // TODO: Record hover event in CSV and/or Database
        // TODO: Record both, the "main" and all historical events as a separate line.

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
        mSpenSurfaceView.setZOrderOnTop(true);
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

        // Initialize Eraser settings
        SpenSettingRemoverInfo removerInfo = new SpenSettingRemoverInfo();
        removerInfo.size = 1;
        removerInfo.type = SpenSettingRemoverInfo.CUTTER_TYPE_CUT;
        mSpenSurfaceView.setRemoverSettingInfo(removerInfo);

    }
}
