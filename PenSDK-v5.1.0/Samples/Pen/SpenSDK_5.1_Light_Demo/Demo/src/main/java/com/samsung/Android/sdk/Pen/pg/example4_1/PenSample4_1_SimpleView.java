package com.samsung.android.sdk.pen.pg.example4_1;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Rect;
import android.media.MediaScannerConnection;
import android.media.MediaScannerConnection.OnScanCompletedListener;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;
import com.samsung.android.sdk.SsdkUnsupportedException;
import com.samsung.android.sdk.pen.Spen;
import com.samsung.android.sdk.pen.SpenSettingPenInfo;
import com.samsung.android.sdk.pen.document.SpenNoteDoc;
import com.samsung.android.sdk.pen.document.SpenPageDoc;
import com.samsung.android.sdk.pen.engine.SpenSimpleView;
import com.samsung.android.sdk.pen.engine.SpenSimpleSurfaceView;
import com.samsung.android.sdk.pen.pg.tool.SDKUtils;
import com.samsung.spensdk4light.example.R;

public class PenSample4_1_SimpleView extends Activity {
    public static final int SDK_VERSION = Build.VERSION.SDK_INT;
    private static final int PEMISSION_REQUEST_CODE = 1;
    private Context mContext;
    private SpenNoteDoc mSpenNoteDoc;
    private SpenPageDoc mSpenPageDoc;
    private SpenPageDoc mSpenPageDocSimpleView;
    private SpenNoteDoc mSpenNoteDocSimpleView;
    private SpenSimpleSurfaceView mSpenSimpleSurfaceView;
    private SpenSimpleView mSpenSimpleView;
    
    private RelativeLayout mSpenSimpleViewContainer;

    private ImageView mSmartScrollBtn;
    private ImageView mSmartZoomBtn;
    private ImageView mSimpleViewBtn;
    
    private AlertDialog dlgSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple_view);
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

        // Create SpenSimpleSurfaceView
        mSpenSimpleSurfaceView = new SpenSimpleSurfaceView(mContext);
        if (mSpenSimpleSurfaceView == null) {
            Toast.makeText(mContext, "Cannot create new SpenSimpleSurfaceView.", Toast.LENGTH_SHORT).show();
            finish();
        }
        spenViewLayout.addView(mSpenSimpleSurfaceView);

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

        // Set a background image
        String path = mContext.getFilesDir().getPath();
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.smemo_bg);
        saveBitmapToFileCache(bitmap, path + "/smemo_bg.jpg");
        mSpenPageDoc.setBackgroundImageMode(SpenPageDoc.BACKGROUND_IMAGE_MODE_STRETCH);
        mSpenPageDoc.setBackgroundImage(path + "/smemo_bg.jpg");
        mSpenPageDoc.clearHistory();

        // Set PageDoc to View
        mSpenSimpleSurfaceView.setPageDoc(mSpenPageDoc, true);

        initPenSettingInfo();

        mSimpleViewBtn = (ImageView) findViewById(R.id.simpleViewBtn);
        mSimpleViewBtn.setOnClickListener(mSimpleViewBtnClickListener);

        if (isSpenFeatureEnabled == false) {
            mSpenSimpleSurfaceView.setToolTypeAction(SpenSimpleSurfaceView.TOOL_FINGER, SpenSimpleSurfaceView.ACTION_STROKE);
            Toast.makeText(mContext, "Device does not support Spen. \n You can draw stroke by finger",
                    Toast.LENGTH_SHORT).show();
        }
    }

    static public void saveBitmapToFileCache(Bitmap bitmap, String strFilePath) {
        // Save images from resources as a file, which can be set as a background image.
        File file = new File(strFilePath);
        OutputStream out = null;

        if (file.exists() == true) {
            return;
        }
        try {
            file.createNewFile();
            out = new FileOutputStream(file);

            if (strFilePath.endsWith(".jpg")) {
                bitmap.compress(CompressFormat.JPEG, 100, out);
            } else {
                bitmap.compress(CompressFormat.PNG, 100, out);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void initPenSettingInfo() {
        // Initialize Pen settings
        SpenSettingPenInfo penInfo = new SpenSettingPenInfo();
        penInfo.color = Color.RED;
        penInfo.size = 10;
        mSpenSimpleSurfaceView.setPenSettingInfo(penInfo);
    }


    private final OnClickListener mSimpleViewBtnClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if(checkPermission()){
                return;
            }
            // Disable Simple View button to prevent a redundant action.
            mSimpleViewBtn.setEnabled(false);

            mSpenSimpleViewContainer = (RelativeLayout) findViewById(R.id.spenSimpleViewContainer);

            mSpenSimpleViewContainer.setVisibility(View.VISIBLE);

            RelativeLayout spenSimpleViewLayout = (RelativeLayout) findViewById(R.id.spenSimpleViewLayout);

            FrameLayout.LayoutParams simpleViewContainerParams = (FrameLayout.LayoutParams) mSpenSimpleViewContainer
                    .getLayoutParams();
            FrameLayout.LayoutParams simpleViewLayoutParams = (FrameLayout.LayoutParams) spenSimpleViewLayout
                    .getLayoutParams();

            // Get the dimension of the device screen.
            Display display = getWindowManager().getDefaultDisplay();
            Rect rect = new Rect();
            display.getRectSize(rect);
            int btnHeight = 100;
            // Set the height of Simple View with a certain ratio to the width of the screen.
            if (rect.width() > rect.height()) {
                simpleViewContainerParams.width = (int) (rect.height() * .6);
                simpleViewContainerParams.height = (int) (rect.height() * .6) + btnHeight;
            } else {
                simpleViewContainerParams.width = (int) (rect.width() * .6);
                simpleViewContainerParams.height = (int) (rect.width() * .6) + btnHeight;
            }
            simpleViewLayoutParams.width = (int) (simpleViewContainerParams.width * .9);
            simpleViewLayoutParams.height = (int) ((simpleViewContainerParams.height)
                    - (simpleViewContainerParams.width * .1) - btnHeight);
            mSpenSimpleViewContainer.setLayoutParams(simpleViewContainerParams);
            spenSimpleViewLayout.setLayoutParams(simpleViewLayoutParams);

            int screenWidth = simpleViewLayoutParams.width;
            int screenHeight = simpleViewLayoutParams.height;
            try {
                mSpenNoteDocSimpleView =
                        new SpenNoteDoc(mContext, screenWidth, screenHeight);
            } catch (IOException e) {
                Toast.makeText(mContext, "Cannot create new NoteDoc.",
                        Toast.LENGTH_SHORT).show();
                e.printStackTrace();
                finish();
            } catch (Exception e) {
                e.printStackTrace();
                finish();
            }
            mSpenPageDocSimpleView = mSpenNoteDocSimpleView.appendPage();
            mSpenPageDocSimpleView.setBackgroundColor(0);
            mSpenPageDocSimpleView.clearHistory();
            // Create SimpleView
            mSpenSimpleView = new SpenSimpleView(mContext);
            mSpenSimpleView.setPageDoc(mSpenPageDocSimpleView, true);
            spenSimpleViewLayout.addView(mSpenSimpleView);
            mSpenSimpleView.setToolTypeAction(SpenSimpleView.TOOL_FINGER, SpenSimpleView.ACTION_STROKE);
            mSpenSimpleView.setToolTypeAction(SpenSimpleView.TOOL_SPEN, SpenSimpleView.ACTION_STROKE);
            mSpenSimpleView.setBlankColor(0);
            mSpenSimpleView.update();

            initSimpleViewPenSettingInfo();

            // Set a button
            Button doneBtn = (Button) findViewById(R.id.done_btn);
            doneBtn.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mSpenSimpleView != null) {
                        inputFileName();
                    }
                }
            });

            Button cancelBtn = (Button) findViewById(R.id.cancel_btn);
            cancelBtn.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(dlgSave != null && dlgSave.isShowing()) {
                        return;
                    }
                    closeSimpleView();
                    return;
                }
            });
        }
    };

    private void initSimpleViewPenSettingInfo() {
        // Initialize Pen settings for Simple View.
        SpenSettingPenInfo penInfo = new SpenSettingPenInfo();
        penInfo.color = Color.BLUE;
        penInfo.size = 10;
        mSpenSimpleView.setPenSettingInfo(penInfo);
    }

    private void inputFileName() {
        // Prompt Save File dialog, get the file name, and save it.
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);
        final View layout = inflater.inflate(R.layout.save_image_dialog, (ViewGroup) findViewById(R.id.layout_root));

        AlertDialog.Builder builderSave = new AlertDialog.Builder(mContext);
        builderSave.setTitle("Enter file name");
        builderSave.setView(layout);

        final EditText inputPath = (EditText) layout.findViewById(R.id.input_path);
        inputPath.setText("image");

        builderSave.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                // Set the save directory for the file.
                File filePath = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/SPen/images");
                if (!filePath.exists()) {
                    if (!filePath.mkdirs()) {
                        Toast.makeText(mContext, "Save Path Creation Error", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                String saveFilePath = filePath.getPath() + '/';
                String fileName = inputPath.getText().toString();
                if (!fileName.equals("")) {
                    saveFilePath += fileName + ".png";
                    saveImageFile(saveFilePath);

                    closeSimpleView();
                }
            }
        });
        builderSave.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        dlgSave = builderSave.create();
        dlgSave.show();
    }

    private void saveImageFile(String strFileName) {
        // Set the directory for a captured image.
        File fileCacheItem = new File(strFileName);
        // Capture and save it in bitmap.
        Bitmap imgBitmap = mSpenSimpleView.captureCurrentView(false);

        OutputStream out = null;
        try {
            // Save the captured bitmap in the directory set above.
            fileCacheItem.createNewFile();
            out = new FileOutputStream(fileCacheItem);
            imgBitmap.compress(CompressFormat.PNG, 100, out);
            Toast.makeText(mContext, "Captured images were stored.", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(mContext, "Capture failed.", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }

                scanImage(strFileName);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        imgBitmap.recycle();
    }

    private void scanImage(final String imageFileName) {
        MediaScannerConnection.scanFile(mContext,
                new String[] { imageFileName }, null,
                new OnScanCompletedListener() {
                    @Override
                    public void onScanCompleted(String path, Uri uri) {
                    }
                });
    }

    private void closeSimpleView() {
        // Close Simple View
        mSimpleViewBtn.setEnabled(true);
        mSpenSimpleViewContainer.setVisibility(View.GONE);
        mSpenSimpleView.setVisibility(View.GONE);
        mSpenSimpleView.close();
        mSpenSimpleView = null;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mSpenSimpleView != null) {
            mSpenSimpleView.close();
            mSpenSimpleView = null;
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