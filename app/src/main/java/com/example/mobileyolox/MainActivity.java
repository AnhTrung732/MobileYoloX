package com.example.mobileyolox;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.OptIn;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ExperimentalGetImage;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.core.VideoCapture;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.media.Image;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.util.Pair;
import android.util.Size;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.mobileyolox.CustomImageView.CustomImageView;
import com.example.mobileyolox.api.ApiService;
import com.example.mobileyolox.api.Const;
import com.example.mobileyolox.model.APIOutput;
import com.google.common.util.concurrent.ListenableFuture;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okio.ByteString;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements ImageAnalysis.Analyzer {
    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    CustomImageView yoloView;
    PreviewView previewView;
    SwitchCompat detectSwitch;
    Boolean mBoolean = false;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        yoloView = findViewById(R.id.yoloView);
        previewView = findViewById(R.id.previewView);
        detectSwitch = findViewById(R.id.detectSwitch);


        detectSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked)
                {
                    mBoolean = true;
                    yoloView.setVisibility(View.VISIBLE);
                }
                else
                {
                    mBoolean = false;
                    yoloView.setVisibility(View.INVISIBLE);
                }
            }
        });

        cameraProviderFuture = ProcessCameraProvider.getInstance(this);
        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                startCameraX(cameraProvider);
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }, getExecutor());
    }

    private void startCameraX(ProcessCameraProvider cameraProvider) {
        cameraProvider.unbindAll();
        // Camera Selector Usecase
        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build();
        //Preview use case
        Preview preview = new Preview.Builder().build();
        preview.setSurfaceProvider(previewView.getSurfaceProvider());
        
        
        //Image analysis use case
        ImageAnalysis imageAnalysis = new ImageAnalysis.Builder()
                //.setTargetResolution(new Size(640, 360))
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_BLOCK_PRODUCER)
                .build();
        
        imageAnalysis.setAnalyzer(getExecutor(), this);
        //Use image analysis

        cameraProvider.bindToLifecycle((LifecycleOwner) this, cameraSelector, preview, imageAnalysis);

    }

    @Override
    public void analyze(@NonNull ImageProxy imageProxy) {
        // Create a file to save the image
//        if (imageProxy.getImageInfo().getTimestamp() % 20 == 0)
//        {
//
//        }
        if (mBoolean)
        {
            final Bitmap bitmap = previewView.getBitmap();
            uploadAndAnalyzeImage(bitmap);
            imageProxy.close();
            try {
                Thread.sleep(100); // sleeps for 0.3 seconds
            } catch (InterruptedException e) {
                // handle the exception here
            }
        }
        else
        {
            imageProxy.close();

        }
        // Close the image proxy after its use


    }

    private Bitmap imageProxyToBitmap(ImageProxy imageProxy) {
        ByteBuffer buffer = imageProxy.getPlanes()[0].getBuffer();
        byte[] bytes = new byte[buffer.remaining()];
        buffer.get(bytes);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

    private void uploadAndAnalyzeImage(Bitmap bitmap) {
        // Convert the bitmap to a file
        File photoFile = bitmapToFile(bitmap);


        // Create the request body with the file and content type
        RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), photoFile);

        MultipartBody.Part multipartBodyPredict = MultipartBody.Part.createFormData(Const.KEY_FILE, "test.jpg", requestBody);

        ApiService.apiService.sendImageFile(multipartBodyPredict).enqueue(new Callback<APIOutput>() {
            @Override
            public void onResponse(Call<APIOutput> call, Response<APIOutput> response) {

                APIOutput output = response.body();
                if (output != null) {
                    List<Pair<RectF, String>> rectangles = new ArrayList<>();
                    for (int index = 0; index < output.getBoxes().size(); index++) {
                        List<PointF> points = new ArrayList<>();
                        PointF point1 = new PointF(output.getBoxes().get(index).get(0), output.getBoxes().get(index).get(1));
                        PointF point2 = new PointF(output.getBoxes().get(index).get(2), output.getBoxes().get(index).get(3));
                        points.add(point1); // Top left corner point
                        points.add(point2); // Bottom right corner point
                        RectF rectF = new RectF(points.get(0).x, points.get(0).y, points.get(1).x, points.get(1).y);
                        String label = output.getClasses().get(index);
                        rectangles.add(new Pair<>(rectF,label));
                    }
                    yoloView.setRectangles(rectangles);
                }
            }
            @Override
            public void onFailure(Call<APIOutput> call, Throwable t) {
                //Toast.makeText(MainActivity.this, "Call Api failed", Toast.LENGTH_SHORT).show();
            }
        });

    }



    private File bitmapToFile(Bitmap bitmap) {
        // Create a file to save the image
        File newPhotoFile = new File(getExternalFilesDir(null), "photo.jpg");

        try (FileOutputStream out = new FileOutputStream(newPhotoFile)) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out); // bmp is your Bitmap instance
        } catch (IOException e) {
            e.printStackTrace();
        }
        return newPhotoFile;
    }


    private Executor getExecutor() {
        return ContextCompat.getMainExecutor(this);
    }

}