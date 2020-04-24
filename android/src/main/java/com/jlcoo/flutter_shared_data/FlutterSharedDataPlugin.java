package com.jlcoo.flutter_shared_data;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.TextUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry;
import io.flutter.plugin.common.PluginRegistry.Registrar;

/**
 * FlutterSharedDataPlugin
 */
public class FlutterSharedDataPlugin implements MethodCallHandler {
    private static String sharedText;
    private static Context context;

    /**
     * Plugin registration.
     */
    public static void registerWith(Registrar registrar) {
        context = registrar.context();

        Intent intent = registrar.activity().getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        if (Intent.ACTION_VIEW.equals(action) && type != null) {
//            if ("text/plain".equals(type) || "text/csv".equals(type)) {
                handleSendText(context, intent); // Handle text being sent
//            }
        }

        if (Intent.ACTION_SEND.equals(action) && type != null) {
//            if ("text/plain".equals(type) || "text/csv".equals(type)) {
                sharedText(context, intent); // Handle text being sent
//            }
        }
        final MethodChannel channel = new MethodChannel(registrar.messenger(), "flutter_shared_data");
        channel.setMethodCallHandler(new FlutterSharedDataPlugin());
    }

    @Override
    public void onMethodCall(MethodCall call, Result result) {
        if (call.method.equals("getSharedPath")) {
            result.success(sharedText);
            sharedText=null;
        } else {
            result.notImplemented();
        }
    }

    static void handleSendText(final Context context, Intent intent) {
        //取出文件uri
        Uri uri = intent.getData();
        if (uri == null) {
            uri = intent.getParcelableExtra(Intent.EXTRA_STREAM);
        }
        String filePath = getFilePathFromContentUri(context,uri);
        if (filePath != null && (new File(filePath).canRead())) {
            sharedText = filePath;
        }
//        sharedText = intent.getStringExtra(Intent.Extra);
    }

    static void sharedText(final Context context, Intent intent) {
        Uri uri = intent.getData();
        if (uri == null) {
            uri = intent.getParcelableExtra(Intent.EXTRA_STREAM);
        }
        String filePath = getFilePathFromContentUri(context,uri);
        if (filePath != null && (new File(filePath).canRead())) {
            sharedText = filePath;
        }
    }

    /**
     * 从uri获取path
     *
     * @param uri content://media/external/file/109009
     *            <p>
     *            FileProvider适配
     *            content://com.tencent.mobileqq.fileprovider/external_files/storage/emulated/0/Tencent/QQfile_recv/
     *            content://com.tencent.mm.external.fileprovider/external/tencent/MicroMsg/Download/
     */
    private static String getFilePathFromContentUri(Context context, Uri uri) {
        if (null == uri) return null;
        String data = null;

        String[] filePathColumn = {MediaStore.MediaColumns.DATA, MediaStore.MediaColumns.DISPLAY_NAME};
        Cursor cursor = context.getContentResolver().query(uri, filePathColumn, null, null, null);
        if (null != cursor) {
            if (cursor.moveToFirst()) {
                int index = cursor.getColumnIndex(MediaStore.MediaColumns.DATA);
                if (index > -1) {
                    data = cursor.getString(index);
                } else {
                    int nameIndex = cursor.getColumnIndex(MediaStore.MediaColumns.DISPLAY_NAME);
                    String fileName = cursor.getString(nameIndex);
                    data = getPathFromInputStreamUri(context, uri, fileName);
                }
            }
            cursor.close();
        }
        return data;
    }

    /**
     * 用流拷贝文件一份到自己APP私有目录下
     *
     * @param context
     * @param uri
     * @param fileName
     */
    private static String getPathFromInputStreamUri(Context context, Uri uri, String fileName) {
        InputStream inputStream = null;
        String filePath = null;

        if (uri.getAuthority() != null) {
            try {
                inputStream = context.getContentResolver().openInputStream(uri);
                File file = createTemporalFileFrom(context, inputStream, fileName);
                filePath = file.getPath();

            } catch (Exception e) {
            } finally {
                try {
                    if (inputStream != null) {
                        inputStream.close();
                    }
                } catch (Exception e) {
                }
            }
        }

        return filePath;
    }

    private static File createTemporalFileFrom(Context context, InputStream inputStream, String fileName)
            throws IOException {
        File targetFile = null;

        if (inputStream != null) {
            int read;
            byte[] buffer = new byte[8 * 1024];
            //自己定义拷贝文件路径
            targetFile = new File(context.getExternalCacheDir(), fileName);
            if (targetFile.exists()) {
                targetFile.delete();
            }
            OutputStream outputStream = new FileOutputStream(targetFile);

            while ((read = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, read);
            }
            outputStream.flush();

            try {
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return targetFile;
    }
}
