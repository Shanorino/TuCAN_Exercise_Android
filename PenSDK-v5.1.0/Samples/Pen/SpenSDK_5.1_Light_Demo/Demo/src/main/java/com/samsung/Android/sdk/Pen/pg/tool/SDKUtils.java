package com.samsung.android.sdk.pen.pg.tool;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.widget.Toast;
import com.samsung.android.sdk.SsdkUnsupportedException;
import com.samsung.android.sdk.pen.Spen;

public class SDKUtils {

    public static boolean processUnsupportedException(final Activity activity, SsdkUnsupportedException e) {
        e.printStackTrace();
        int errType = e.getType();
        // If the device is not a Samsung device or the device does not support Pen.
        if (errType == SsdkUnsupportedException.VENDOR_NOT_SUPPORTED
                || errType == SsdkUnsupportedException.DEVICE_NOT_SUPPORTED) {
            Toast.makeText(activity, "This device does not support Spen.", Toast.LENGTH_SHORT).show();
            activity.finish();
        } else if (errType == SsdkUnsupportedException.LIBRARY_NOT_INSTALLED) {
            // If SpenSDK APK is not installed.
            showAlertDialog(activity, "You need to install additional Spen software" 
                    + " to use this application."
                    + "You will be taken to the installation screen."
                    + "Restart this application after the software has been installed.", true);
        } else if (errType == SsdkUnsupportedException.LIBRARY_UPDATE_IS_REQUIRED) {
            // SpenSDK APK must be updated.
            showAlertDialog(activity, "You need to update your Spen software to use this application."
                    + " You will be taken to the installation screen."
                    + " Restart this application after the software has been updated.", true);
        } else if (errType == SsdkUnsupportedException.LIBRARY_UPDATE_IS_RECOMMENDED) {
            // Recommended to update SpenSDK APK to a new version available.
            showAlertDialog(activity, "We recommend that you update your Spen software"
                    + " before using this application." + " You will be taken to the installation screen."
                    + " Restart this application after the software has been updated.", false);
            return false; // Procceed to the normal activity process if it is not updated.
        }
        return true;
    }

    private static void showAlertDialog(final Activity activity, String msg, final boolean closeActivity) {

        AlertDialog.Builder dlg = new AlertDialog.Builder(activity);
        dlg.setIcon(activity.getResources().getDrawable(android.R.drawable.ic_dialog_alert));
        dlg.setTitle("Upgrade Notification").setMessage(msg)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Go to the market site and install or update APK.
                        Uri uri = Uri.parse("market://details?id=" + Spen.getSpenPackageName());
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        activity.startActivity(intent);

                        dialog.dismiss();
                        activity.finish();
                    }
                }).setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (closeActivity == true) {
                            // Terminate the activity if APK is not installed.
                            activity.finish();
                        }
                        dialog.dismiss();
                    }
                }).setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        if (closeActivity == true) {
                            // Terminate the activity if APK is not installed.
                            activity.finish();
                        }
                    }
                }).show();
        dlg = null;
    }

    public static String getRealPathFromURI(Context context, Uri uri) {
        if (Build.VERSION.SDK_INT < 11) {
            return getRealPathFromURI_BelowAPI11(context, uri);
        }
        if (Build.VERSION.SDK_INT < 18) {
            return getRealPathFromURI_API11to18(context, uri);
        }
        if (Build.VERSION.SDK_INT < 21) {
            return getRealPathFromURI_API19to21(context, uri);
        }
        return getRealPathFromURI22(context, uri);
    }

    public static String getRealPathFromURI_API19to21(Context context, Uri uri) {
        Cursor cursor = context.getContentResolver().query(Uri.parse(uri.toString()), null, null, null, null);
        if (cursor != null)
        {
            cursor.moveToNext();
            int columnIndex = cursor.getColumnIndex(MediaStore.MediaColumns.DATA);
            if (columnIndex >= 0)
                return cursor.getString(columnIndex);
            return getRealPathFromURI_API11to18(context, uri);
        }
        return null;
    }

    @SuppressLint("NewApi")
    public static String getRealPathFromURI22(Context context, Uri uri) {
        String filePath = "";
        if (DocumentsContract.isDocumentUri(context, uri)) {
            String wholeID = DocumentsContract.getDocumentId(uri);

            // Split at colon, use second item in the array
            String id = wholeID.split(":")[1];

            String[] column = { MediaStore.Images.Media.DATA };

            // where id is equal to
            String sel = MediaStore.Images.Media._ID + "=?";

            Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, column,
                    sel, new String[] { id }, null);

            if (cursor != null)
            {
                int columnIndex = cursor.getColumnIndex(column[0]);

                if (cursor.moveToFirst()) {
                    filePath = cursor.getString(columnIndex);
                }
                cursor.close();
            }
            return filePath;
        }
        filePath = getRealPathFromURI_API19to21(context, uri);

        return filePath;

    }

    @SuppressLint("NewApi")
    public static String getRealPathFromURI_API11to18(Context context, Uri contentUri) {
        String[] proj = { MediaStore.Images.Media.DATA };
        String result = null;

        CursorLoader cursorLoader = new CursorLoader(context, contentUri, proj, null, null, null);
        Cursor cursor = cursorLoader.loadInBackground();

        if (cursor != null) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            if (column_index >= 0)
                result = cursor.getString(column_index);
        }
        return result;
    }

    public static String getRealPathFromURI_BelowAPI11(Context context, Uri contentUri) {
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
        if (cursor != null)
        {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        }
        return null;
    }
}
