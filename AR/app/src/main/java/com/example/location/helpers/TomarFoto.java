package com.example.location.helpers;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.icu.text.SimpleDateFormat;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.view.PixelCopy;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.FileProvider;

import com.google.ar.sceneform.ArSceneView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

public class TomarFoto {
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void takePhoto(ArSceneView arFragmentSceneView, Context context, CoordinatorLayout main_content) {
        final String filename = generateFilename();

        // Create a bitmap the size of the scene view.
        final Bitmap bitmap = Bitmap.createBitmap(arFragmentSceneView.getWidth(), arFragmentSceneView.getHeight(),
                Bitmap.Config.ARGB_8888);

        // Create a handler thread to offload the processing of the image.
        final HandlerThread handlerThread = new HandlerThread("PixelCopier");
        handlerThread.start();
        // Make the request to copy.
        PixelCopy.request(arFragmentSceneView, bitmap, (copyResult) -> {
            if (copyResult == PixelCopy.SUCCESS) {
                try {
                    saveBitmapToDisk(bitmap, filename, context);
                } catch (IOException e) {
                    Toast toast = Toast.makeText(context, e.toString(),
                            Toast.LENGTH_LONG);
                    toast.show();
                    return;
                }
            } else {
                Toast toast = Toast.makeText(context,
                        "Failed to copyPixels: " + copyResult, Toast.LENGTH_LONG);
                toast.show();
            }
            handlerThread.quitSafely();
        }, new Handler(handlerThread.getLooper()));
    }

    private void saveBitmapToDisk(Bitmap bitmap, String filename, Context context) throws IOException {
        File out = new File(filename);
        if (!out.getParentFile().exists()) {
            out.getParentFile().mkdirs();
        }

        try (FileOutputStream outputStream = new FileOutputStream(filename);
             ByteArrayOutputStream outputData = new ByteArrayOutputStream()) {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputData);
            outputData.writeTo(outputStream);
            outputStream.flush();
            outputStream.close();
        } catch (IOException ex) {
            Log.d("TAG", "saveBitmapToDisk: " + ex.getMessage());
            throw new IOException("Failed to save bitmap to disk", ex);
        }
        MediaScannerConnection.scanFile(context, new String[]{filename}, null,
                new MediaScannerConnection.OnScanCompletedListener() {
                    public void onScanCompleted(String path, Uri uri) {
                        File photoFile = new File(filename);
                        Uri photoURI = FileProvider.getUriForFile(context,
                                context.getPackageName() + ".ar.codelab.name.provider",
                                photoFile);
                        Intent intent = new Intent(Intent.ACTION_VIEW, photoURI);
                        intent.setDataAndType(photoURI, "image/*");
                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        context.startActivity(intent);
                    }
                });
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private String generateFilename() {
        String date =
                new SimpleDateFormat("yyyyMMddHHmmss", java.util.Locale.getDefault()).format(new Date());
        return Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES) + File.separator + "Sceneform/" + date + "_screenshot.jpg";
    }
}
