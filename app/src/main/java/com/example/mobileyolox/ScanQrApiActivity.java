package com.example.mobileyolox;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.example.mobileyolox.api.ApiService;
import com.example.mobileyolox.model.TestAPI;
import com.google.zxing.Result;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ScanQrApiActivity extends AppCompatActivity {
    public static String SCAN_DOMAIN = "";
    private CodeScanner mCodeScanner;

    private static final int PERMISSION_REQUEST_CODE = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_qr_api);
        CodeScannerView scanner_view = findViewById(R.id.scanner_view);
        mCodeScanner = new CodeScanner(this, scanner_view);

        if (checkPermission()) {
            mCodeScanner.setDecodeCallback(new DecodeCallback() {
                @Override
                public void onDecoded(@NonNull Result result) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            SCAN_DOMAIN = result.getText();
                            ApiService.apiService.testResponse().enqueue(new Callback<TestAPI>() {
                                @Override
                                public void onResponse(Call<TestAPI> call, Response<TestAPI> response) {
                                    if (response.isSuccessful()) {
                                        TestAPI api = response.body();
                                        assert api != null;
                                        String result = api.getResponse();
                                        if (result.equals("true"))
                                        {
                                            //Toast.makeText(ScanQrApiActivity.this, result.getText(), Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(ScanQrApiActivity.this, MainActivity.class);
                                            startActivity(intent);
                                        }
                                    }
                                }
                                @Override
                                public void onFailure(Call<TestAPI> call, Throwable t) {
                                    Toast.makeText(ScanQrApiActivity.this, "Scan again please !", Toast.LENGTH_SHORT).show();
                                    mCodeScanner.startPreview();
                                }


                            });

                        }
                    });
                }
            });

            scanner_view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mCodeScanner.startPreview();
                }
            });

        } else {
            requestPermission();
        }

    }



    private boolean checkPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            return false;
        }
        return true;
    }

    private void requestPermission() {

        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.CAMERA},
                PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getApplicationContext(), "Permission Granted", Toast.LENGTH_SHORT).show();

                    // main logic
                } else {
                    Toast.makeText(getApplicationContext(), "Permission Denied", Toast.LENGTH_SHORT).show();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                                != PackageManager.PERMISSION_GRANTED) {
                            showMessageOKCancel(
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            requestPermission();
                                        }
                                    });
                        }
                    }
                }
                break;
        }
    }

    private void showMessageOKCancel(DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(ScanQrApiActivity.this)
                .setMessage("You need to allow access permissions")
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mCodeScanner.startPreview();
    }

    @Override
    protected void onPause() {
        mCodeScanner.releaseResources();
        super.onPause();
    }
}