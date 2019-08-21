package com.samsung.android.sdk.pen.pg.example3_1;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.RippleDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.samsung.android.sdk.SsdkUnsupportedException;
import com.samsung.android.sdk.pen.Spen;
import com.samsung.android.sdk.pen.SpenSettingPenInfo;
import com.samsung.android.sdk.pen.SpenSettingSelectionInfo;
import com.samsung.android.sdk.pen.document.SpenInvalidPasswordException;
import com.samsung.android.sdk.pen.document.SpenNoteDoc;
import com.samsung.android.sdk.pen.document.SpenNoteFile;
import com.samsung.android.sdk.pen.document.SpenObjectBase;
import com.samsung.android.sdk.pen.document.SpenObjectStroke;
import com.samsung.android.sdk.pen.document.SpenPageDoc;
import com.samsung.android.sdk.pen.document.SpenUnsupportedTypeException;
import com.samsung.android.sdk.pen.document.SpenUnsupportedVersionException;
import com.samsung.android.sdk.pen.engine.SpenColorPickerListener;
import com.samsung.android.sdk.pen.engine.SpenContextMenuItemInfo;
import com.samsung.android.sdk.pen.engine.SpenControlBase;
import com.samsung.android.sdk.pen.engine.SpenControlListener;
import com.samsung.android.sdk.pen.engine.SpenSelectionChangeListener;
import com.samsung.android.sdk.pen.engine.SpenSimpleSurfaceView;
import com.samsung.android.sdk.pen.engine.SpenTouchListener;
import com.samsung.android.sdk.pen.pen.SpenPenInfo;
import com.samsung.android.sdk.pen.pen.SpenPenManager;
import com.samsung.android.sdk.pen.pg.tool.SDKUtils;
import com.samsung.android.sdk.pen.settingui.SpenSettingPenLayout;
import com.samsung.android.sdk.pen.settingui.SpenSettingSelectionLayout;
import com.samsung.spensdk4light.example.R;

public class PenSample3_1_SelectionSetting extends Activity {
    public static final int SDK_VERSION = Build.VERSION.SDK_INT;
    private static final int PEMISSION_REQUEST_CODE = 1;
    private final int CONTEXT_MENU_DELETE_ID = 10;

    private final int MODE_SELECTION = 0;
    private final int MODE_PEN = 1;
    private final int MODE_STROKE_OBJ = 2;

    private Context mContext;
    private SpenNoteDoc mSpenNoteDoc;
    private SpenPageDoc mSpenPageDoc;
    private FrameLayout mSettingView;
    private SpenSimpleSurfaceView mSpenSimpleSurfaceView;
    private SpenSettingPenLayout mPenSettingView;
    private SpenSettingSelectionLayout mSelectionSettingView;

    private ImageView mSelectionBtn;
    private ImageView mPenBtn;
    private ImageView mStrokeObjBtn;
    private ImageView mSaveFileBtn;
    private ImageView mLoadFileBtn;
    private ImageView mAddPageBtn;

    private ImageView mPrevPageBtn;
    private ImageView mNextPageBtn;
    private TextView mPageIndexText;

    private int mMode = MODE_PEN;
    private Rect mScreenRect;
    private File mFilePath;
    private String mSpdPath = null;
    private boolean mIsDiscard = false;
    private int mToolType = SpenSimpleSurfaceView.TOOL_SPEN;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selection_setting);
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

        RelativeLayout spenViewLayout = (RelativeLayout) findViewById(R.id.spenViewLayout);

        // Create PenSettingView
        mPenSettingView = new SpenSettingPenLayout(getApplicationContext(), "", spenViewLayout);

        // Create SelectionSettingView
        mSelectionSettingView = new SpenSettingSelectionLayout(mContext, "", spenViewLayout);

        mSettingView = (FrameLayout) findViewById(R.id.settingView);
        mSettingView.addView(mPenSettingView);
        mSettingView.addView(mSelectionSettingView);

        // Create SpenSimpleSurfaceView
        mSpenSimpleSurfaceView = new SpenSimpleSurfaceView(mContext);
        if (mSpenSimpleSurfaceView == null) {
            Toast.makeText(mContext, "Cannot create new SpenSimpleSurfaceView.", Toast.LENGTH_SHORT).show();
            finish();
        }
        mSpenSimpleSurfaceView.setToolTipEnabled(true);
        spenViewLayout.addView(mSpenSimpleSurfaceView);
        mPenSettingView.setCanvasView(mSpenSimpleSurfaceView);
        mSelectionSettingView.setCanvasView(mSpenSimpleSurfaceView);

        // Get the dimension of the device screen.
        Display display = getWindowManager().getDefaultDisplay();
        mScreenRect = new Rect();
        display.getRectSize(mScreenRect);
        // Create SpenNoteDoc.
        try {
            mSpenNoteDoc = new SpenNoteDoc(mContext, mScreenRect.width(), mScreenRect.height());
        } catch (IOException e) {
            Toast.makeText(mContext, "Cannot create new NoteDoc.", Toast.LENGTH_SHORT).show();
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

        initSettingInfo();
        // Register the listener
        mSpenSimpleSurfaceView.setTouchListener(mPenTouchListener);
        mSpenSimpleSurfaceView.setColorPickerListener(mColorPickerListener);
        mSpenSimpleSurfaceView.setControlListener(mControlListener);
        mSpenSimpleSurfaceView.setSelectionChangeListener(mSelectionListener);

        // Set a button
        mSelectionBtn = (ImageView) findViewById(R.id.selectionBtn);
        mSelectionBtn.setOnClickListener(mSelectionBtnClickListener);

        mPenBtn = (ImageView) findViewById(R.id.penBtn);
        mPenBtn.setOnClickListener(mPenBtnClickListener);

        mStrokeObjBtn = (ImageView) findViewById(R.id.strokeObjBtn);
        mStrokeObjBtn.setOnClickListener(mStrokeObjBtnClickListener);

        mSaveFileBtn = (ImageView) findViewById(R.id.saveFileBtn);
        mSaveFileBtn.setOnClickListener(mSaveFileBtnClickListener);

        mLoadFileBtn = (ImageView) findViewById(R.id.loadFileBtn);
        mLoadFileBtn.setOnClickListener(mLoadFileBtnClickListener);

        mAddPageBtn = (ImageView) findViewById(R.id.addPageBtn);
        mAddPageBtn.setOnClickListener(mAddPageBtnClickListener);

        mNextPageBtn = (ImageView) findViewById(R.id.btnNextPage);
        mNextPageBtn.setOnClickListener(mNextPageBtnClickListener);
        mNextPageBtn.setEnabled(false);
        setRippleBackground(mNextPageBtn);

        mPrevPageBtn = (ImageView) findViewById(R.id.btnPrevPage);
        mPrevPageBtn.setOnClickListener(mPrevPageBtnClickListener);
        mPrevPageBtn.setEnabled(false);
        setRippleBackground(mPrevPageBtn);

        mPageIndexText = (TextView) findViewById(R.id.textPageIndex);

        selectButton(mPenBtn);

        String filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/SPen/";
        mFilePath = new File(filePath);
        if (!mFilePath.exists()) {
            if (!mFilePath.mkdirs()) {
                Toast.makeText(mContext, "Save Path Creation Error", Toast.LENGTH_SHORT).show();
                return;
            }
        }

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
        List<SpenPenInfo> penList = new ArrayList<SpenPenInfo>();
        SpenPenManager penManager = new SpenPenManager(mContext);
        penList = penManager.getPenInfoList();
        SpenSettingPenInfo penInfo = new SpenSettingPenInfo();
        for (SpenPenInfo info : penList) {
            if (info.name.equalsIgnoreCase("Brush")) {
                penInfo.name = info.className;
                break;
            }
        }
        penInfo.color = Color.BLUE;
        penInfo.size = 10;
        mSpenSimpleSurfaceView.setPenSettingInfo(penInfo);
        mPenSettingView.setInfo(penInfo);

        // Initialize selection settings
        SpenSettingSelectionInfo mSelectionInfo = mSpenSimpleSurfaceView.getSelectionSettingInfo();
        mSelectionInfo.type = SpenSettingSelectionInfo.TYPE_LASSO;
        mSelectionSettingView.setInfo(mSelectionInfo);
        mSpenSimpleSurfaceView.setSelectionSettingInfo(mSelectionInfo);
    }

    private final SpenTouchListener mPenTouchListener = new SpenTouchListener() {

        @Override
        public boolean onTouch(View view, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_UP && event.getToolType(0) == mToolType) {
                // Check if the control is created.
                SpenControlBase control = mSpenSimpleSurfaceView.getControl();
                if (control == null) {
                    PointF canvasPos = getCanvasPoint(event);
                    // When Pen touches the display while it is in Add ObjectStroke mode
                    if (mMode == MODE_STROKE_OBJ) {
                        addStrokeObject(canvasPos.x, canvasPos.y);
                        return true;

                    }
                }
            }
            return false;
        }
    };

    private final OnClickListener mSelectionBtnClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            mSpenSimpleSurfaceView.closeControl();

            // When Spen is in selection mode
            if (mSpenSimpleSurfaceView.getToolTypeAction(mToolType) == SpenSimpleSurfaceView.ACTION_SELECTION) {
                // If SelectionSettingView is open, close it.
                if (mSelectionSettingView.isShown()) {
                    mSelectionSettingView.setVisibility(View.GONE);
                    // If SelectionSettingView is not open, open it.
                } else {
                    mSelectionSettingView.setVisibility(View.VISIBLE);
                }
                // If Spen is not in selection mode, change it to selection mode.
            } else {
                mMode = MODE_SELECTION;
                selectButton(mSelectionBtn);
                mSpenSimpleSurfaceView.setToolTypeAction(mToolType, SpenSimpleSurfaceView.ACTION_SELECTION);
            }
        }
    };

    private final OnClickListener mPenBtnClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            mSpenSimpleSurfaceView.closeControl();

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
                mMode = MODE_PEN;
                selectButton(mPenBtn);
                mSpenSimpleSurfaceView.setToolTypeAction(mToolType, SpenSimpleSurfaceView.ACTION_STROKE);
            }
        }
    };

    private final OnClickListener mStrokeObjBtnClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            mSpenSimpleSurfaceView.closeControl();

            mMode = MODE_STROKE_OBJ;
            selectButton(mStrokeObjBtn);
            mSpenSimpleSurfaceView.setToolTypeAction(mToolType, SpenSimpleSurfaceView.ACTION_NONE);
        }
    };

    private final OnClickListener mSaveFileBtnClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if(checkPermission()){
                return;
            }
            mSpenSimpleSurfaceView.closeControl();

            closeSettingView();
            saveNoteFile(false);
        }
    };

    private final OnClickListener mLoadFileBtnClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if(checkPermission()){
                return;
            }
            mSpenSimpleSurfaceView.closeControl();

            closeSettingView();
            loadNoteFile();
        }
    };


    private final OnClickListener mAddPageBtnClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            mSpenSimpleSurfaceView.closeControl();

            closeSettingView();
            // Create a page next to the current page.
            int pageIndex = mSpenNoteDoc.getPageIndexById(mSpenPageDoc.getId()) + 1;
            mSpenPageDoc = mSpenNoteDoc.insertPage(pageIndex);
            mSpenPageDoc.setBackgroundColor(0xFFD6E6F5);
            mSpenPageDoc.clearHistory();

            v.setClickable(false);
            mSpenSimpleSurfaceView.setPageDoc(mSpenPageDoc, true);
            v.setClickable(true);

            updatePageButton(pageIndex);
        }
    };

    private final OnClickListener mNextPageBtnClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            int index = mSpenNoteDoc.getPageIndexById(mSpenPageDoc.getId()) + 1;
            mSpenPageDoc = mSpenNoteDoc.getPage(index);
            mSpenSimpleSurfaceView.setPageDoc(mSpenPageDoc, true);
            updatePageButton(index);
        }
    };

    private final OnClickListener mPrevPageBtnClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            int index = mSpenNoteDoc.getPageIndexById(mSpenPageDoc.getId()) - 1;
            mSpenPageDoc = mSpenNoteDoc.getPage(index);
            mSpenSimpleSurfaceView.setPageDoc(mSpenPageDoc, true);
            updatePageButton(index);
        }
    };

    private void updatePageButton(int pageIndex) {
        mPrevPageBtn.setEnabled(true);
        mNextPageBtn.setEnabled(true);

        if (pageIndex == 0) {
            mPrevPageBtn.setEnabled(false);
        }

        if (pageIndex == mSpenNoteDoc.getPageCount() - 1) {
            mNextPageBtn.setEnabled(false);
        }

        mPageIndexText.setText(pageIndex + 1 + "/" + mSpenNoteDoc.getPageCount());
    }


    private void addStrokeObject(float x, float y) {
        // Set the location to insert ObjectStroke and add it to PageDoc.
        int pointSize = 157;
        PointF[] points = new PointF[pointSize];
        float[] pressures = new float[pointSize];
        int[] timestamps = new int[pointSize];

        for (int i = 0; i < pointSize; i++) {
            points[i] = new PointF();
            points[i].x = x++;
            points[i].y = (float) (y + Math.sin(.04 * i) * 50);
            pressures[i] = 1;
            timestamps[i] = (int) android.os.SystemClock.uptimeMillis();
        }

        SpenObjectStroke strokeObj = new SpenObjectStroke(mPenSettingView.getInfo().name, points, pressures, timestamps);
        strokeObj.setPenSize(mPenSettingView.getInfo().size);
        strokeObj.setColor(mPenSettingView.getInfo().color);
        mSpenPageDoc.appendObject(strokeObj);
        mSpenSimpleSurfaceView.update();
    }

    private PointF getCanvasPoint(MotionEvent event) {
        float panX = mSpenSimpleSurfaceView.getPan().x;
        float panY = mSpenSimpleSurfaceView.getPan().y;
        float zoom = mSpenSimpleSurfaceView.getZoomRatio();
        return new PointF(event.getX() / zoom + panX, event.getY() / zoom + panY);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (data == null) {
                Toast.makeText(mContext, "Cannot find the image", Toast.LENGTH_SHORT).show();
                return;
            }
        }
    }

    private boolean saveNoteFile(final boolean isClose) {
        // Prompt Save File dialog to get the file name
        // and get its save format option (note file or image).
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);
        final View layout = inflater.inflate(R.layout.save_file_dialog, (ViewGroup) findViewById(R.id.layout_root));

        AlertDialog.Builder builderSave = new AlertDialog.Builder(mContext);
        builderSave.setTitle("Enter file name");
        builderSave.setView(layout);

        final EditText inputPath = (EditText) layout.findViewById(R.id.input_path);
        inputPath.setText("Note");

        builderSave.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                final RadioGroup selectFileExt = (RadioGroup) layout.findViewById(R.id.radioGroup);

                // Set the save directory for the file.
                String saveFilePath = mFilePath.getPath() + '/';
                String fileName = inputPath.getText().toString();
                if (!fileName.equals("")) {
                    saveFilePath += fileName;
                    int checkedRadioButtonId = selectFileExt.getCheckedRadioButtonId();
                    if (checkedRadioButtonId == R.id.radioNote) {
                        saveFilePath += ".spd";
                        saveNoteFile(saveFilePath);
                    } else if (checkedRadioButtonId == R.id.radioImage) {
                        saveFilePath += ".png";
                        captureSpenSimpleSurfaceView(saveFilePath);
                    }
                    if (isClose) {
                        finish();
                    }
                } else {
                    Toast.makeText(mContext, "Invalid filename !!!", Toast.LENGTH_LONG).show();
                }
            }
        });
        builderSave.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (isClose) {
                    finish();
                }
            }
        });

        AlertDialog dlgSave = builderSave.create();
        dlgSave.show();

        return true;
    }

    private boolean saveNoteFile(String strFileName) {
        try {
            // Save NoteDoc
            mSpenNoteDoc.save(strFileName, false);
            Toast.makeText(mContext, "Save success to " + strFileName, Toast.LENGTH_SHORT).show();
            mSpdPath = strFileName;
        } catch (IOException e) {
            Toast.makeText(mContext, "Cannot save NoteDoc file.", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private void captureSpenSimpleSurfaceView(String strFileName) {

        // Capture the view
        Bitmap imgBitmap = mSpenSimpleSurfaceView.captureCurrentView(true);
        if (imgBitmap == null) {
            Toast.makeText(mContext, "Capture failed." + strFileName, Toast.LENGTH_SHORT).show();
            return;
        }

        OutputStream out = null;
        try {
            // Create FileOutputStream and save the captured image.
            out = new FileOutputStream(strFileName);
            imgBitmap.compress(CompressFormat.PNG, 100, out);
            // Save the note information.
            mSpenNoteDoc.save(out, false);
            out.close();
            Toast.makeText(mContext, "Captured images were stored in the file" + strFileName, Toast.LENGTH_SHORT)
                    .show();
        } catch (IOException e) {
            File tmpFile = new File(strFileName);
            if (tmpFile.exists()) {
                tmpFile.delete();
            }
            Toast.makeText(mContext, "Failed to save the file.", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        } catch (Exception e) {
            File tmpFile = new File(strFileName);
            if (tmpFile.exists()) {
                tmpFile.delete();
            }
            Toast.makeText(mContext, "Failed to save the file.", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
        imgBitmap.recycle();
    }

    private void loadNoteFile() {
        // Load the file list.
        final String fileList[] = setFileList();
        if (fileList == null) {
            return;
        }

        // Prompt Load File dialog.
        new AlertDialog.Builder(mContext).setTitle("Select file")
                .setItems(fileList, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String strFilePath = mFilePath.getPath() + '/' + fileList[which];

                        try {
                            // Create NoteDoc with the selected file.
                            SpenNoteDoc tmpSpenNoteDoc = new SpenNoteDoc(mContext, strFilePath, mScreenRect.width(),
                                    SpenNoteDoc.MODE_WRITABLE, true);
                            mSpenNoteDoc.close();
                            mSpenNoteDoc = tmpSpenNoteDoc;
                            if (mSpenNoteDoc.getPageCount() == 0) {
                                mSpenPageDoc = mSpenNoteDoc.appendPage();
                            } else {
                                mSpenPageDoc = mSpenNoteDoc.getPage(mSpenNoteDoc.getLastEditedPageIndex());
                            }
                            mSpenSimpleSurfaceView.setPageDoc(mSpenPageDoc, true);
                            updatePageButton(mSpenNoteDoc.getPageIndexById(mSpenPageDoc.getId()));

                            mSpenSimpleSurfaceView.update();
                            Toast.makeText(mContext, "Successfully loaded noteFile.", Toast.LENGTH_SHORT).show();
                            mSpdPath = strFilePath;
                        } catch (IOException e) {
                            Toast.makeText(mContext, "Cannot open this file.", Toast.LENGTH_LONG).show();
                        } catch (SpenUnsupportedTypeException e) {
                            Toast.makeText(mContext, "This file is not supported.", Toast.LENGTH_LONG).show();
                        } catch (SpenInvalidPasswordException e) {
                            Toast.makeText(mContext, "This file is locked by a password.", Toast.LENGTH_LONG).show();
                        } catch (SpenUnsupportedVersionException e) {
                            Toast.makeText(mContext, "This file is the version that does not support.",
                                    Toast.LENGTH_LONG).show();
                        } catch (Exception e) {
                            Toast.makeText(mContext, "Failed to load noteDoc.", Toast.LENGTH_LONG).show();
                        }
                    }
                }).show();
    }

    private String[] setFileList() {
        // Call the file list under the directory in mFilePath.
        if (!mFilePath.exists()) {
            if (!mFilePath.mkdirs()) {
                Toast.makeText(mContext, "Save Path Creation Error", Toast.LENGTH_SHORT).show();
                return null;
            }
        }
        // Filter in spd and png files.
        File[] fileList = mFilePath.listFiles(new txtFileFilter());
        if (fileList == null) {
            Toast.makeText(mContext, "File does not exist.", Toast.LENGTH_SHORT).show();
            return null;
        }

        int i = 0;
        String[] strFileList = new String[fileList.length];
        for (File file : fileList) {
            strFileList[i++] = file.getName();
        }

        return strFileList;
    }

    static class txtFileFilter implements FilenameFilter {
        @Override
        public boolean accept(File dir, String name) {
            return (name.endsWith(".spd") || name.endsWith(".png"));
        }
    }

    private void setRippleBackground(View view) {
        if (android.os.Build.VERSION.SDK_INT >= 21) {
            view.setBackground(new RippleDrawable(ColorStateList.valueOf(Color.LTGRAY), getDrawable(R.drawable.page_circle), null));
        }
    }

    private final SpenColorPickerListener mColorPickerListener = new SpenColorPickerListener() {
        @Override
        public void onChanged(int color, int x, int y) {
            // Set the color from the Color Picker to the setting view.
            if (mPenSettingView != null) {
                if (mMode == MODE_PEN) {
                    SpenSettingPenInfo penInfo = mPenSettingView.getInfo();
                    penInfo.color = color;
                    mPenSettingView.setInfo(penInfo);
                }
            }
        }
    };

    private final SpenControlListener mControlListener = new SpenControlListener() {

        @Override
        public void onRotationChanged(float arg0, SpenObjectBase arg1) {
        }

        @Override
        public void onRectChanged(RectF arg0, SpenObjectBase arg1) {
        }

        @Override
        public void onObjectChanged(ArrayList<SpenObjectBase> arg0) {
        }

        @Override
        public boolean onMenuSelected(ArrayList<SpenObjectBase> objectList, int itemId) {
            switch (itemId) {
                // Remove the selected object.
                case CONTEXT_MENU_DELETE_ID:
                    // mSpenPageDoc.removeSelectedObject();
                    for (SpenObjectBase obj : objectList) {
                        mSpenPageDoc.removeObject(obj);
                    }
                    mSpenSimpleSurfaceView.closeControl();
                    mSpenSimpleSurfaceView.update();
                    break;
            }
            return true;
        }

        @Override
        public boolean onCreated(ArrayList<SpenObjectBase> objectList, ArrayList<Rect> relativeRectList,
                                 ArrayList<SpenContextMenuItemInfo> menu, ArrayList<Integer> styleList, int pressType, PointF point) {
            // Set context menu.
            menu.add(new SpenContextMenuItemInfo(CONTEXT_MENU_DELETE_ID, "Delete", true));
            return true;
        }

        @Override
        public boolean onClosed(ArrayList<SpenObjectBase> arg0) {
            return false;
        }
    };

    private final SpenSelectionChangeListener mSelectionListener = new SpenSelectionChangeListener() {

        @Override
        public void onChanged(SpenSettingSelectionInfo info) {
            // Close Setting view if selection type is changed.
            mSelectionSettingView.setVisibility(SpenSimpleSurfaceView.GONE);
        }
    };

    private void selectButton(View v) {
        // Enable or disable the button according to the current mode.
        mSelectionBtn.setSelected(false);
        mPenBtn.setSelected(false);
        mStrokeObjBtn.setSelected(false);

        v.setSelected(true);
        closeSettingView();
    }

    private void closeSettingView() {
        // Close all the setting views.
        mPenSettingView.setVisibility(SpenSimpleSurfaceView.GONE);
        mSelectionSettingView.setVisibility(SpenSimpleSurfaceView.GONE);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mSpenSimpleSurfaceView.cancelStroke();
    }

    @Override
    public void onBackPressed() {
        int pageCount = mSpenNoteDoc.getPageCount();
        int objectCount = mSpenPageDoc.getObjectCount(true);
        try {
            mSpenPageDoc.save();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
        if (mSpenNoteDoc.isChanged()
                && ((pageCount == 1 && objectCount > 0) || pageCount > 1 || SpenNoteFile.hasUnsavedData(mContext,
                mSpdPath, Long.valueOf(0)))) {
            AlertDialog.Builder dlg = new AlertDialog.Builder(mContext);
            dlg.setIcon(mContext.getResources().getDrawable(android.R.drawable.ic_dialog_alert));
            dlg.setTitle(mContext.getResources().getString(R.string.app_name))
                    .setMessage("Do you want to exit after save?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if(checkPermission()){
                                return;
                            }
                            saveNoteFile(true);
                            dialog.dismiss();
                        }
                    }).setNeutralButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    mIsDiscard = true;
                    finish();
                }
            }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }).show();
            dlg = null;
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mPenSettingView != null) {
            mPenSettingView.close();
        }

        if (mSelectionSettingView != null) {
            mSelectionSettingView.close();
        }

        if (mSpenSimpleSurfaceView != null) {
            mSpenSimpleSurfaceView.closeControl();
            mSpenSimpleSurfaceView.close();
            mSpenSimpleSurfaceView = null;
        }

        if (mSpenNoteDoc != null) {
            try {
                if (mIsDiscard) {
                    mSpenNoteDoc.discard();
                } else {
                    mSpenNoteDoc.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            mSpenNoteDoc = null;
        }
    }
    @TargetApi(Build.VERSION_CODES.M)
    public boolean checkPermission() {
        if (SDK_VERSION < 23) {
            return false;
        }
        List<String> permissionList = new ArrayList<String>(Arrays.asList(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE));
        if(PackageManager.PERMISSION_GRANTED == checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)){
            permissionList.remove(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if(PackageManager.PERMISSION_GRANTED == checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)){
            permissionList.remove(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        if(permissionList.size()>0) {
            requestPermissions(permissionList.toArray(new String[permissionList.size()]), PEMISSION_REQUEST_CODE);
            return true;
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PEMISSION_REQUEST_CODE) {
            if (grantResults != null ) {
                for(int i= 0; i< grantResults.length;i++){
                    if(grantResults[i]!= PackageManager.PERMISSION_GRANTED){
                        Toast.makeText(mContext,"permission: " + permissions[i] + " is denied", Toast.LENGTH_SHORT).show();
                    }
                }

            }
        }
    }
}