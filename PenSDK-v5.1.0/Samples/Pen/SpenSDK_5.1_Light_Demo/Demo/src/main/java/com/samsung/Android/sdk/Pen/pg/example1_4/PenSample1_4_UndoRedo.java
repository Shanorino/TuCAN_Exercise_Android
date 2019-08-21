package com.samsung.android.sdk.pen.pg.example1_4;

import java.io.IOException;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;
import com.samsung.android.sdk.SsdkUnsupportedException;
import com.samsung.android.sdk.pen.Spen;
import com.samsung.android.sdk.pen.engine.SpenTouchListener;
import com.samsung.android.sdk.pen.SpenSettingEraserInfo;
import com.samsung.android.sdk.pen.SpenSettingPenInfo;
import com.samsung.android.sdk.pen.document.SpenNoteDoc;
import com.samsung.android.sdk.pen.document.SpenPageDoc;
import com.samsung.android.sdk.pen.document.SpenPageDoc.HistoryListener;
import com.samsung.android.sdk.pen.document.SpenPageDoc.HistoryUpdateInfo;
import com.samsung.android.sdk.pen.engine.SpenColorPickerListener;
import com.samsung.android.sdk.pen.engine.SpenSimpleSurfaceView;
import com.samsung.android.sdk.pen.pg.tool.SDKUtils;
import com.samsung.android.sdk.pen.settingui.SpenSettingEraserLayout;
import com.samsung.android.sdk.pen.settingui.SpenSettingEraserLayout.EventListener;
import com.samsung.android.sdk.pen.settingui.SpenSettingPenLayout;
import com.samsung.spensdk4light.example.R;

public class PenSample1_4_UndoRedo extends Activity {

    private Context mContext;
    private SpenNoteDoc mSpenNoteDoc;
    private SpenPageDoc mSpenPageDoc;
    private SpenSimpleSurfaceView mSpenSimpleSurfaceView;
    private SpenSettingPenLayout mPenSettingView;
    private SpenSettingEraserLayout mEraserSettingView;

    private ImageView mPenBtn;
    private ImageView mEraserBtn;
    private ImageView mUndoBtn;
    private ImageView mRedoBtn;

    private int mToolType = SpenSimpleSurfaceView.TOOL_SPEN;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_undo_redo);
        mContext = this;

        // Initialize Spen
        boolean isSpenFeatureEnabled = false;
        Spen spenPackage = new Spen();
        try {
            spenPackage.initialize(this);
            isSpenFeatureEnabled = spenPackage.isFeatureEnabled(Spen.DEVICE_PEN);
        } catch (SsdkUnsupportedException e) {
            if (SDKUtils.processUnsupportedException(this, e) == true) {
                return;
            }
        } catch (Exception e1) {
            Toast.makeText(mContext, "Cannot initialize Spen.", Toast.LENGTH_SHORT).show();
            e1.printStackTrace();
            finish();
        }

        FrameLayout spenViewContainer = (FrameLayout) findViewById(R.id.spenViewContainer);
        RelativeLayout spenViewLayout = (RelativeLayout) findViewById(R.id.spenViewLayout);

        // Create PenSettingView
        mPenSettingView = new SpenSettingPenLayout(getApplicationContext(), "", spenViewLayout);

        // Create EraserSettingView
        mEraserSettingView = new SpenSettingEraserLayout(getApplicationContext(), "", spenViewLayout);

        spenViewContainer.addView(mPenSettingView);
        spenViewContainer.addView(mEraserSettingView);

        // Create SpenView
        mSpenSimpleSurfaceView = new SpenSimpleSurfaceView(mContext);
        if (mSpenSimpleSurfaceView == null) {
            Toast.makeText(mContext, "Cannot create new SpenSimpleSurfaceView.", Toast.LENGTH_SHORT).show();
            finish();
        }
        mSpenSimpleSurfaceView.setToolTipEnabled(true);
        spenViewLayout.addView(mSpenSimpleSurfaceView);
        mPenSettingView.setCanvasView(mSpenSimpleSurfaceView);
        mEraserSettingView.setCanvasView(mSpenSimpleSurfaceView);

        // Get the dimension of the device screen.
        Display display = getWindowManager().getDefaultDisplay();
        Rect rect = new Rect();
        display.getRectSize(rect);
        // Create SpenNoteDoc
        try {
            mSpenNoteDoc = new SpenNoteDoc(mContext, rect.width(), rect.height());
        } catch (IOException e) {
            Toast.makeText(mContext, "Cannot create new NoteDoc", Toast.LENGTH_SHORT).show();
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
        // Set PageDoc to View
        mSpenSimpleSurfaceView.setPageDoc(mSpenPageDoc, true);

        initSettingInfo();
        // Register the listener
        mSpenSimpleSurfaceView.setColorPickerListener(mColorPickerListener);
        mSpenSimpleSurfaceView.setPreTouchListener(onPreTouchSurfaceViewListener);
        mSpenPageDoc.setHistoryListener(mHistoryListener);
        mEraserSettingView.setEraserListener(mEraserListener);

        // Set a button
        mPenBtn = (ImageView) findViewById(R.id.penBtn);
        mPenBtn.setOnClickListener(mPenBtnClickListener);

        mEraserBtn = (ImageView) findViewById(R.id.eraserBtn);
        mEraserBtn.setOnClickListener(mEraserBtnClickListener);

        mUndoBtn = (ImageView) findViewById(R.id.undoBtn);
        mUndoBtn.setOnClickListener(undoNredoBtnClickListener);
        mUndoBtn.setEnabled(mSpenPageDoc.isUndoable());

        mRedoBtn = (ImageView) findViewById(R.id.redoBtn);
        mRedoBtn.setOnClickListener(undoNredoBtnClickListener);
        mRedoBtn.setEnabled(mSpenPageDoc.isRedoable());

        selectButton(mPenBtn);

        if (isSpenFeatureEnabled == false) {
            mToolType = SpenSimpleSurfaceView.TOOL_FINGER;
            Toast.makeText(mContext, "Device does not support Spen. \n You can draw stroke by finger.",
                    Toast.LENGTH_SHORT).show();
        } else {
            mToolType = SpenSimpleSurfaceView.TOOL_SPEN;
        }
        mSpenSimpleSurfaceView.setToolTypeAction(mToolType, SpenSimpleSurfaceView.ACTION_STROKE);
    }

    private void initSettingInfo() {
        // Initialize Pen settings
        SpenSettingPenInfo penInfo = new SpenSettingPenInfo();
        penInfo.color = Color.BLUE;
        penInfo.size = 10;
        mSpenSimpleSurfaceView.setPenSettingInfo(penInfo);
        mPenSettingView.setInfo(penInfo);

        // Initialize Eraser settings
        SpenSettingEraserInfo eraserInfo = new SpenSettingEraserInfo();
        eraserInfo.size = 30;
        mSpenSimpleSurfaceView.setEraserSettingInfo(eraserInfo);
        mEraserSettingView.setInfo(eraserInfo);
    }

    private SpenTouchListener onPreTouchSurfaceViewListener = new SpenTouchListener() {

        @Override
        public boolean onTouch(View view, MotionEvent event) {
            // TODO Auto-generated method stub
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                case MotionEvent.ACTION_POINTER_DOWN:
                    enableButton(false);
                    break;
                case MotionEvent.ACTION_CANCEL:
                case MotionEvent.ACTION_UP:
                    enableButton(true);
                    break;
            }
            return false;
        }
    };

    private final OnClickListener mPenBtnClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            // When Spen is in stroke (pen) mode
            if (mSpenSimpleSurfaceView.getToolTypeAction(mToolType) == SpenSimpleSurfaceView.ACTION_STROKE) {
                // If PenSettingView is open, close it.
                if (mPenSettingView.isShown()) {
                    mPenSettingView.setVisibility(View.GONE);
                    // If PenSettingView is not open, open it.
                } else {
                    mPenSettingView.setViewMode(SpenSettingPenLayout.VIEW_MODE_NORMAL);
                    mPenSettingView.setVisibility(View.VISIBLE);
                }
                // If Spen is not in stroke (pen) mode, change it to stroke mode.
            } else {
                int curAction = mSpenSimpleSurfaceView.getToolTypeAction(SpenSimpleSurfaceView.TOOL_FINGER);
                mSpenSimpleSurfaceView.setToolTypeAction(mToolType, SpenSimpleSurfaceView.ACTION_STROKE);
                int newAction = mSpenSimpleSurfaceView.getToolTypeAction(SpenSimpleSurfaceView.TOOL_FINGER);
                if (mToolType == SpenSimpleSurfaceView.TOOL_FINGER) {
                    if (curAction != newAction) {
                        selectButton(mPenBtn);
                    }
                } else {
                    selectButton(mPenBtn);
                }
            }
        }
    };

    private final OnClickListener mEraserBtnClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            // When Spen is in eraser mode
            if (mSpenSimpleSurfaceView.getToolTypeAction(mToolType) == SpenSimpleSurfaceView.ACTION_ERASER) {
                // If EraserSettingView is open, close it.
                if (mEraserSettingView.isShown()) {
                    mEraserSettingView.setVisibility(View.GONE);
                    // If EraserSettingView is not open, open it.
                } else {
                    mEraserSettingView.setVisibility(View.VISIBLE);
                }
                // If Spen is not in eraser mode, change it to eraser mode.
            } else {
                selectButton(mEraserBtn);
                mSpenSimpleSurfaceView.setToolTypeAction(mToolType, SpenSimpleSurfaceView.ACTION_ERASER);
            }
        }
    };

    private final OnClickListener undoNredoBtnClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mSpenPageDoc == null) {
                return;
            }
            // Undo button is clicked.
            if (v.equals(mUndoBtn)) {
                if (mSpenPageDoc.isUndoable()) {
                    HistoryUpdateInfo[] userData = mSpenPageDoc.undo();
                    mSpenSimpleSurfaceView.updateUndo(userData);
                }
                // Redo button is clicked.
            } else if (v.equals(mRedoBtn)) {
                if (mSpenPageDoc.isRedoable()) {
                    HistoryUpdateInfo[] userData = mSpenPageDoc.redo();
                    mSpenSimpleSurfaceView.updateRedo(userData);
                }
            }
        }
    };

    private final SpenColorPickerListener mColorPickerListener = new SpenColorPickerListener() {
        @Override
        public void onChanged(int color, int x, int y) {
            // Set the color from the Color Picker to the setting view.
            if (mPenSettingView != null) {
                SpenSettingPenInfo penInfo = mPenSettingView.getInfo();
                penInfo.color = color;
                mPenSettingView.setInfo(penInfo);
            }
        }
    };

    private final EventListener mEraserListener = new EventListener() {
        @Override
        public void onClearAll() {
            // ClearAll button action routines of EraserSettingView
            mSpenPageDoc.removeAllObject();
            mSpenSimpleSurfaceView.update();
        }
    };

    private final HistoryListener mHistoryListener = new HistoryListener() {
        @Override
        public void onCommit(SpenPageDoc page) {
        }

        @Override
        public void onUndoable(SpenPageDoc page, boolean undoable) {
            // Enable or disable the button according to the availability of undo.
            mUndoBtn.setEnabled(undoable);
        }

        @Override
        public void onRedoable(SpenPageDoc page, boolean redoable) {
            // Enable or disable the button according to the availability of redo.
            mRedoBtn.setEnabled(redoable);
        }
    };

    private void enableButton(boolean isEnable) {
        mPenBtn.setEnabled(isEnable);
        mEraserBtn.setEnabled(isEnable);
        mUndoBtn.setEnabled(isEnable && mSpenPageDoc.isUndoable());
        mRedoBtn.setEnabled(isEnable && mSpenPageDoc.isRedoable());
    }

    private void selectButton(View v) {
        // Enable or disable the button according to the current mode.
        mPenBtn.setSelected(false);
        mEraserBtn.setSelected(false);
        v.setSelected(true);

        closeSettingView();
    }

    private void closeSettingView() {
        // Close all the setting views.
        mEraserSettingView.setVisibility(SpenSimpleSurfaceView.GONE);
        mPenSettingView.setVisibility(SpenSimpleSurfaceView.GONE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mPenSettingView != null) {
            mPenSettingView.close();
        }
        if (mEraserSettingView != null) {
            mEraserSettingView.close();
        }

        if (mSpenSimpleSurfaceView != null) {
            mSpenSimpleSurfaceView.close();
            mSpenSimpleSurfaceView = null;
        }

        if (mSpenNoteDoc != null) {
            try {
                mSpenNoteDoc.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            mSpenNoteDoc = null;
        }
    };
}
