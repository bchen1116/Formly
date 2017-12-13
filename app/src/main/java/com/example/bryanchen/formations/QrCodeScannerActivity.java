package com.example.bryanchen.formations;

//import android.content.Intent;
//import android.support.v7.app.AppCompatActivity;
//import android.os.Bundle;
//import android.util.Log;
//import android.util.SparseArray;
//import android.view.SurfaceHolder;
//import android.view.SurfaceView;
//import android.view.View;
//import android.widget.Button;
//import android.widget.RelativeLayout;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import com.google.android.gms.vision.CameraSource;
//import com.google.android.gms.vision.Detector;
//import com.google.android.gms.vision.barcode.Barcode;
//import com.google.android.gms.vision.barcode.BarcodeDetector;
//
//import java.io.IOException;
//
//public class Camera extends AppCompatActivity {
//
//    private SurfaceView cameraView;
//    private TextView barcodeInfo;
//    private CameraSource cameraSource;
//    private final int CAMERACODE = 5;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_camera);
//
//        cameraView = (SurfaceView) findViewById(R.id.cameraView);
//
//        BarcodeDetector detector = new BarcodeDetector.Builder(this).setBarcodeFormats(Barcode.QR_CODE).build();
//        cameraSource = new CameraSource.Builder(this, detector).setRequestedPreviewSize(700, 900)
//                .setAutoFocusEnabled(true).build();
//
//        Button exitButton = (Button) findViewById(R.id.exit);
//        exitButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent i = new Intent();
//                i.putExtra("work", ""+1);
//                setResult(RESULT_OK, i);
//                finish();
//            }
//        });
//
//        cameraView.getHolder().addCallback(new SurfaceHolder.Callback() {
//            @Override
//            public void surfaceCreated(SurfaceHolder holder) {
//                try {
//                    cameraSource.start(cameraView.getHolder());
//                } catch (IOException | SecurityException e) {
//                    Log.e("CAMERA SOURCE", e.getMessage());
//                }
//            }
//
//            @Override
//            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
//
//            }
//
//            @Override
//            public void surfaceDestroyed(SurfaceHolder holder) {
//                cameraSource.stop();
//            }
//        });
//
//        detector.setProcessor(new Detector.Processor<Barcode>() {
//            @Override
//            public void release() {
//
//            }
//
//            @Override
//            public void receiveDetections(Detector.Detections<Barcode> detections) {
//                final SparseArray<Barcode> barcodes = detections.getDetectedItems();
//                Toast.makeText(getApplicationContext(), "we detecting?", Toast.LENGTH_SHORT).show();
//                if (barcodes.size() != 0) {
//                    barcodeInfo.post(new Runnable() {
//                        @Override
//                        public void run() {
//                            barcodeInfo.setText(barcodes.valueAt(0).displayValue);
//                            Intent i = new Intent();
//                            i.putExtra("work", barcodes.valueAt(0).displayValue);
//                            setResult(RESULT_OK, i);
//                            finish();
//                        }
//                    });
//                }
//            }
//        });
//    }
//
//}
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.zxing.Result;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class QrCodeScannerActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler{

    private ZXingScannerView mScannerView;

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        mScannerView = new ZXingScannerView(this);   // Programmatically initialize the scanner view
        setContentView(mScannerView);                // Set the scanner view as the content view
    }

    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this); // Register ourselves as a handler for scan results.
        mScannerView.startCamera();          // Start camera on resume
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();           // Stop camera on pause
    }

    @Override
    public void handleResult(Result rawResult) {
        // Do something with the result here
        Toast.makeText(getApplicationContext(), rawResult.getText(), Toast.LENGTH_SHORT).show(); // Prints scan results
        onBackPressed();
    }
}

