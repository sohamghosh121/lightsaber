package sohamghosh.lightsaberapp;


import android.Manifest;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.os.Build;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Size;
import android.util.SparseArray;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.View;
import android.widget.RelativeLayout;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class QRCodeActivity extends AppCompatActivity {

    private BarcodeDetector mDetector;

    private boolean cameraStarted;

    private Intent intent;


    private CameraSource mCameraSource;
    private SurfaceView mCameraView;
    private SurfaceTexture mPreviewSurfaceTexture;

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcode);

        cameraStarted = false;

        intent = this.getIntent();



        // permissions check
        int permissionCheck = ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA);
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            Log.d("QRCODE", "Has camera permissions");
            try {
                this.startCamera();
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        } else {
            this.requestPermissions(new String[]{ Manifest.permission.CAMERA}, 0);
        }


    }

    @Override
    public void onRequestPermissionsResult(int reqCode, String[] permissions, int[] grantResults) {
        if (reqCode == 0){
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED && !cameraStarted)
                try {
                    this.startCamera();
                } catch (CameraAccessException e) {
                    e.printStackTrace();
                }
        }
    }

    private void startCamera() throws CameraAccessException {
        mCameraView = (SurfaceView) this.findViewById(R.id.cameraSurface);
        mDetector = new BarcodeDetector.Builder(getApplicationContext())
                .setBarcodeFormats(Barcode.QR_CODE)
                .build();

        CameraManager manager = (CameraManager) getSystemService(this.getApplicationContext().CAMERA_SERVICE);
        String[] cameraIds = manager.getCameraIdList();
        CameraCharacteristics character = manager.getCameraCharacteristics(cameraIds[0]);
        StreamConfigurationMap map = character.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
        Size[] previewSizes = map.getOutputSizes(SurfaceTexture.class);

        int h = previewSizes[0].getHeight();
        int w = previewSizes[0].getWidth();

        CameraSource.Builder builder = new CameraSource.Builder(this, mDetector)
                .setFacing(CameraSource.CAMERA_FACING_BACK)
                .setRequestedPreviewSize(w, h);
        mCameraSource = builder.build();
        if (mDetector.isOperational())
            Log.d("QRCODE", "Started detector");

        mDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {
            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> barcodes = detections.getDetectedItems();

                if (barcodes.size() != 0) {
                    Barcode thisCode = barcodes.valueAt(0);
                    String result =  thisCode.rawValue;
                    Log.d("QRCODE", "got result " + result);
                    intent.putExtra("result", result);
                    QRCodeActivity.this.setResult(RESULT_OK, intent);
                    QRCodeActivity.this.finish();
                }
            }
        });
        mCameraView.getHolder().addCallback(new SurfaceHolder.Callback() {

            @Override
            public void surfaceCreated(SurfaceHolder surfaceHolder) {
                try {
                    mCameraSource.start(mCameraView.getHolder());
                } catch (IOException ie) {
                    Log.e("QRCODE", ie.getMessage());
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
                mCameraSource.stop();
            }
        });

        mCameraView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cameraFocus(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
            }
        });



    }

    private final boolean cameraFocus(String focusMode) {
        Field[] declaredFields = CameraSource.class.getDeclaredFields();

        for (Field field : declaredFields) {
            if (field.getType() == Camera.class) {
                field.setAccessible(true);
                try {
                    Camera camera = (Camera) field.get(mCameraSource);
                    if (camera != null) {
                        Camera.Parameters params = camera.getParameters();
                        params.setFocusMode(focusMode);
                        camera.setParameters(params);
                        return true;
                    }

                    return false;
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }

                break;
            }
        }

        return false;
    }
}
