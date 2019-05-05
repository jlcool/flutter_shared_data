package com.jlcoo.flutter_shared_data;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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
            if ("text/plain".equals(type) || "text/csv".equals(type)) {
                handleSendText(context, intent); // Handle text being sent
            }
        }

        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if ("text/plain".equals(type) || "text/csv".equals(type)) {
                sharedText(context, intent); // Handle text being sent
            }
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
        String path = getPathFromUri(context, intent.getData());
        if (path != null && (new File(path).canRead())) {
            sharedText = path;
        }
//        sharedText = intent.getStringExtra(Intent.Extra);
    }

    static void sharedText(final Context context, Intent intent) {
        Uri uri = intent.getParcelableExtra(Intent.EXTRA_STREAM);
        String path = getPathFromUri(context, uri);
        if (path != null && (new File(path).canRead())) {
            sharedText = path;
        }
    }

    static String getPathFromUri(final Context context, final Uri uri) {

        return getPathFromRemoteUri(context, uri);

    }


    private static String getPathFromRemoteUri(final Context context, final Uri uri) {
        // The code below is why Java now has try-with-resources and the Files utility.
        File file = null;
        InputStream inputStream = null;
        InputStreamReader streamReader = null;
        OutputStreamWriter outputStream = null;
        boolean success = false;
        try {
            inputStream = context.getContentResolver().openInputStream(uri);
            streamReader = new InputStreamReader(inputStream);
            String prefix = uri.getPath().substring(uri.getPath().lastIndexOf("."));
            file = File.createTempFile("import", prefix, context.getCacheDir());
            outputStream = new OutputStreamWriter(new FileOutputStream(file));

            if (inputStream != null) {
                copy(streamReader, outputStream);
                success = true;
            }
        } catch (IOException ignored) {
        } finally {
            try {
                if (inputStream != null) inputStream.close();
                if (streamReader != null) streamReader.close();
                ;
            } catch (IOException ignored) {
            }
            try {
                if (outputStream != null) outputStream.close();
            } catch (IOException ignored) {
                // If closing the output stream fails, we cannot be sure that the
                // target file was written in full. Flushing the stream merely moves
                // the bytes into the OS, not necessarily to the file.
                success = false;
            }
        }
        return success ? file.getPath() : null;
    }

    private static void copy(InputStreamReader in, OutputStreamWriter out) throws IOException {

        char[] buffer = new char[1024];//构造一个长度为1024的字节数组
        while (in.read(buffer) != -1) {//读取
            //写入另一个文件
            out.write(buffer);
        }
        out.flush();
    }
}
